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
import me.involuting.meetups.scatter.ScatterManager;
import me.involuting.meetups.scatter.task.ScatterTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class GameService {

    private final Meetups plugin;
    private final GameManager gameManager;
    private final BorderManager borderManager;
    private final ScatterManager scatterManager;

    private GameCountdown countdown;
    private ScatterTask scatterTask;
    private BorderTask borderTask;
    private GracePeriodTask gracePeriodTask;



    public void start(Arena arena) {

        if (gameManager.hasGame()) {
            throw new IllegalStateException("Game already running.");
        }

        gameManager.create(arena);
        startCountdown();
    }

    private void startCountdown() {

        Game game = safeGame();
        game.setGameState(GameState.STARTING);

        countdown = new GameCountdown(plugin, game, game.getArena().getCountdown()) {
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

        startBorder();
    }



    private void startBorder() {

        Game game = safeGame();

        borderManager.setup();

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

            if (!mp.isAlive()) continue;

            Player player = mp.getPlayer();
            if (player == null || !player.isOnline()) continue;

            player.teleport(dmCenter);
            player.setGameMode(GameMode.SURVIVAL);
        }

        for (MeetupPlayer mp : game.getSpectators()) {

            Player player = mp.getPlayer();
            if (player == null || !player.isOnline()) continue;

            player.setGameMode(GameMode.SPECTATOR);

            if (spectatorSpawn != null) {
                player.teleport(spectatorSpawn);
            }
        }

        borderManager.shrinkTo(arena.getDeathmatchBorderSize(), 10);
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

        long aliveCount = game.getPlayers()
                .stream()
                .filter(MeetupPlayer::isAlive)
                .count();

        if (aliveCount > 1) return;

        MeetupPlayer winner = game.getPlayers()
                .stream()
                .filter(MeetupPlayer::isAlive)
                .findFirst()
                .orElse(null);

        if (winner != null) {
            Bukkit.broadcastMessage(ChatColor.GOLD +
                    winner.getPlayer().getName() +
                    " has won the game!");
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + "There was no winner.");
        }

        endGame();
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

        if (countdown != null) {
            countdown.cancel();
            countdown = null;
        }

        if (scatterTask != null) {
            scatterTask.cancel();
            scatterTask = null;
        }

        if (borderTask != null) {
            borderTask.cancel();
            borderTask = null;
        }

        if (gracePeriodTask != null) {
            gracePeriodTask.cancel();
            gracePeriodTask = null;
        }
    }



    private Game safeGame() {
        if (!gameManager.hasGame()) {
            throw new IllegalStateException("No active game.");
        }
        return gameManager.getGame();
    }

    public void join(Player player) {

        Game game = safeGame();

        if (game.getGameState() != GameState.STARTING &&
                game.getGameState() != GameState.WAITING) {
            player.sendMessage("§cYou cannot join right now.");
            return;
        }

        MeetupPlayer mp = game.getMeetupPlayer(player.getUniqueId());

        if (mp != null) {
            player.sendMessage("§cYou are already in the game.");
            return;
        }

        MeetupPlayer newPlayer = new MeetupPlayer(player.getUniqueId());
        game.addPlayer(newPlayer);

        int current = game.getAlivePlayers();
        int max = game.getArena().getMaxPlayers();

        player.sendMessage("§aYou have joined the game! §7(" + current + "/" + max + ")");

        Bukkit.broadcastMessage(
                "§a" + player.getName() +
                        " §7joined the game §8(" + current + "/" + max + ")"
        );
    }
}