package me.involuting.meetups.game.service;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.border.BorderManager;
import me.involuting.meetups.border.task.BorderTask;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.countdown.GameCountdown;
import me.involuting.meetups.game.manager.GameManager;
import me.involuting.meetups.game.state.GameState;
import me.involuting.meetups.grace.GracePeriodTask;
import me.involuting.meetups.player.MeetupPlayer;
import me.involuting.meetups.player.PlayerManager;
import me.involuting.meetups.scatter.ScatterManager;
import me.involuting.meetups.scatter.task.ScatterTask;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.HashSet;

@RequiredArgsConstructor
public class GameService {

    private final Meetups plugin;
    private final GameManager gameManager;
    private final BorderManager borderManager;
    private final ScatterManager scatterManager;
    private final PlayerManager playerManager;

    private GameCountdown countdown;
    private ScatterTask scatterTask;
    private BorderTask borderTask;
    private GracePeriodTask gracePeriodTask;



    public void start(Arena arena) {

        if (gameManager.hasGame()) {
            throw new IllegalStateException("Game already running.");
        }

        if (arena.getPlayers().size() < arena.getMinPlayers()) {
            throw new IllegalStateException("Not enough players.");
        }

        Game game = gameManager.create(arena);

        for (var uuid : new HashSet<>(arena.getPlayers())) {

            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) continue;

            MeetupPlayer mp = playerManager.getOrCreate(player);
            mp.setAlive(true);
            mp.setSpectating(false);

            game.addPlayer(mp);
        }

        arena.getPlayers().clear();

        startCountdown(game);
    }



    private void startCountdown(Game game) {

        game.setGameState(GameState.STARTING);

        countdown = new GameCountdown(plugin, game, plugin.getScatterManager(), game.getArena().getCountdown()) {
            @Override
            public void onFinish() {
                scatterPlayers();
            }
        };

        countdown.start();
    }



    public void scatterPlayers() {

        Game game = safeGame();
        game.setGameState(GameState.SCATTERING);

        scatterTask = new ScatterTask(plugin, game, scatterManager) {
            @Override
            public void onFinish() {
                startGracePeriod();
            }
        };

        scatterTask.start();
    }



    public void startGracePeriod() {

        Game game = safeGame();
        game.setGameState(GameState.GRACE_PERIOD);

        Bukkit.broadcastMessage(ChatColor.GREEN + "Grace period has started.");

        gracePeriodTask = new GracePeriodTask(plugin, game, this, game.getArena().getGracePeriod()) {
            @Override
            public void onFinish() {
                startPlaying();
            }
        };

        gracePeriodTask.start();
    }



    public void startPlaying() {

        Game game = safeGame();
        game.setGameState(GameState.PLAYING);

        Bukkit.broadcastMessage(ChatColor.GREEN + "The game has started!");

        startBorder(game);
    }



    private void startBorder(Game game) {

        borderManager.setup(game);

        borderTask = new BorderTask(plugin, game, borderManager) {
            @Override
            public void onFinish() {
                startDeathmatch();
            }
        };

        borderTask.start(game.getArena().getBorderDelay());
    }



    public void startDeathmatch() {

        Game game = safeGame();
        game.setGameState(GameState.DEATHMATCH);

        Bukkit.broadcastMessage(ChatColor.RED + "Deathmatch has begun!");

        Arena arena = game.getArena();

        Location dmCenter = arena.getBorderCenter();
        Location spectatorSpawn = arena.getSpectatorSpawn();

        for (MeetupPlayer mp : game.getPlayers()) {

            Player player = mp.getPlayer();
            if (player == null || !player.isOnline()) continue;

            if (mp.isAlive()) {
                player.teleport(dmCenter);
                player.setGameMode(GameMode.SURVIVAL);
            } else {
                player.setGameMode(GameMode.SPECTATOR);
                if (spectatorSpawn != null) {
                    player.teleport(spectatorSpawn);
                }
            }
        }

        borderManager.shrinkTo(game, arena.getDeathmatchBorderSize(), 10);
    }



    public void eliminate(MeetupPlayer player) {

        Game game = safeGame();

        player.addDeath();
        player.setAlive(false);
        player.setSpectating(true);

        game.removePlayer(player);
        game.addSpectator(player);

        checkWinner(game);
    }

    private void checkWinner(Game game) {

        long alive = game.getPlayers().stream()
                .filter(MeetupPlayer::isAlive)
                .count();

        if (alive > 1) return;

        MeetupPlayer winner = game.getPlayers().stream()
                .filter(MeetupPlayer::isAlive)
                .findFirst()
                .orElse(null);

        if (winner != null) {
            Bukkit.broadcastMessage(ChatColor.GOLD +
                    winner.getPlayer().getName() +
                    " has won the game!");
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + "No winner.");
        }

        endGame();
        resetBorder();
    }



    public void endGame() {

        Game game = safeGame();
        game.setGameState(GameState.ENDING);

        stopTasks();

        game.getPlayers().forEach(this::resetPlayer);
        game.getSpectators().forEach(this::resetPlayer);

        gameManager.destroy();
    }

    private void resetPlayer(MeetupPlayer mp) {

        Player player = mp.getPlayer();
        if (player == null || !player.isOnline()) return;

        mp.setAlive(false);
        mp.setSpectating(false);
        mp.setLastAttacker(null);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20F);
        player.setFireTicks(0);

        player.setExp(0F);
        player.setLevel(0);

        player.setGameMode(GameMode.ADVENTURE);

        player.getActivePotionEffects()
                .forEach(e -> player.removePotionEffect(e.getType()));
    }



    private void stopTasks() {

        if (countdown != null) countdown.cancel();
        if (scatterTask != null) scatterTask.cancel();
        if (borderTask != null) borderTask.cancel();
        if (gracePeriodTask != null) gracePeriodTask.cancel();

        countdown = null;
        scatterTask = null;
        borderTask = null;
        gracePeriodTask = null;
    }



    private Game safeGame() {
        if (!gameManager.hasGame()) {
            throw new IllegalStateException("No active game.");
        }
        return gameManager.getGame();
    }



    public void join(Player player, Arena arena) {

        if (arena == null) {
            player.sendMessage("§cNo arena is available.");
            return;
        }

        if (arena.hasPlayer(player.getUniqueId())) {
            player.sendMessage("§cYou are already in this arena.");
            return;
        }

        if (arena.isFull()) {
            player.sendMessage("§cThis arena is full.");
            return;
        }

        arena.addPlayer(player.getUniqueId());

        Bukkit.broadcastMessage(
                "§a" + player.getName() +
                        " §7joined the game §8(" +
                        arena.getPlayers().size() + "/" +
                        arena.getMaxPlayers() + ")"
        );
    }

    public void resetBorder() {
        WorldBorder border = gameManager.getGame().getArena().getWorld().getWorldBorder();

        border.setSize(6000); //
        border.setCenter(gameManager.getGame().getArena().getBorderCenter());
        border.setWarningDistance(0);
        border.setDamageAmount(0);
        border.setDamageBuffer(0);
    }
}