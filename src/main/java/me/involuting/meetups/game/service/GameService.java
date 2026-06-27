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
import me.involuting.meetups.queue.QueueManager;
import me.involuting.meetups.scatter.ScatterManager;
import me.involuting.meetups.scatter.task.ScatterTask;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@RequiredArgsConstructor
public class GameService {

    private final Meetups plugin;
    private final GameManager gameManager;
    private final BorderManager borderManager;
    private final ScatterManager scatterManager;
    private final PlayerManager playerManager;
    private QueueManager queueManager;

    private GameCountdown countdown;
    private ScatterTask scatterTask;
    private BorderTask borderTask;
    private GracePeriodTask gracePeriodTask;

    private boolean starting = false;


    public void startFromQueue(Arena arena, Set<UUID> queuedPlayers) {

        if (gameManager.hasGame() || starting) {
            throw new IllegalStateException("Game already running or starting.");
        }

        if (arena == null) {
            throw new IllegalStateException("Arena cannot be null.");
        }

        if (queuedPlayers.size() < arena.getMinPlayers()) {
            throw new IllegalStateException("Not enough players.");
        }

        starting = true;

        Game game = gameManager.create(arena);

        for (UUID uuid : new HashSet<>(queuedPlayers)) {

            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) continue;

            MeetupPlayer mp = playerManager.getOrCreate(player);
            mp.setAlive(true);
            mp.setSpectating(false);

            game.addPlayer(mp);
        }

        queuedPlayers.clear();

        startCountdown(game);
    }

    private void startCountdown(Game game) {

        game.setGameState(GameState.STARTING);

        countdown = new GameCountdown(
                plugin,
                game,
                scatterManager,
                game.getArena().getCountdown()
        ) {
            @Override
            public void onFinish() {
                scatterPlayers(game);
            }
        };

        countdown.start();
    }


    public void scatterPlayers(Game game) {

        game.setGameState(GameState.SCATTERING);

        scatterTask = new ScatterTask(plugin, game, scatterManager) {
            @Override
            public void onFinish() {
                startGracePeriod(game);
            }
        };

        scatterTask.start();

    }


    public void startGracePeriod(Game game) {

        game.setGameState(GameState.GRACE_PERIOD);

        Bukkit.broadcastMessage(ChatColor.GREEN + "Grace period has started.");

        gracePeriodTask = new GracePeriodTask(plugin, game, this, game.getArena().getGracePeriod()) {
            @Override
            public void onFinish() {
                startPlaying(game);
            }
        };

        gracePeriodTask.start();
    }


    public void startPlaying(Game game) {

        game.setGameState(GameState.PLAYING);

        Bukkit.broadcastMessage(ChatColor.GREEN + "The game has started");

        startBorder(game);
    }


    private void startBorder(Game game) {

        borderManager.setup(game);

        borderTask = new BorderTask(plugin, game, borderManager) {
            @Override
            public void onFinish() {
                startDeathmatch(game);
            }
        };

        borderTask.start(game.getArena().getBorderDelay());
    }


    public void startDeathmatch(Game game) {

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

        Game game = gameManager.getCurrentGame();

        if (game == null) {
            return;
        }

        if (!player.isAlive()) {
            return;
        }

        player.addDeath();
        player.setAlive(false);
        player.setSpectating(true);

        game.removePlayer(player);
        game.addSpectator(player);

        Player bukkit = player.getPlayer();

        if (bukkit != null) {
            bukkit.setGameMode(GameMode.SPECTATOR);

            if (game.getArena().getSpectatorSpawn() != null) {
                bukkit.teleport(game.getArena().getSpectatorSpawn());
            }
        }

        checkWinner(game);
    }



    private void checkWinner(Game game) {

        MeetupPlayer winner = game.getPlayers().stream()
                .filter(MeetupPlayer::isAlive)
                .findFirst()
                .orElse(null);

        if (game.getAlivePlayers() > 1) {
            return;
        }

        if (winner != null && winner.getPlayer() != null) {
            Bukkit.broadcastMessage("§6§lWinner §8» §e" + winner.getPlayer().getName());
        } else {
            Bukkit.broadcastMessage("§cNobody won the match.");
        }

        endGame(game);
    }


    public void endGame(Game game) {

        game.setGameState(GameState.ENDING);

        stopTasks();

        borderManager.reset(game);
        borderManager.clear(game);

        for (MeetupPlayer mp : game.getPlayers()) {
            resetPlayer(mp);
        }

        for (MeetupPlayer mp : game.getSpectators()) {
            resetPlayer(mp);
        }

        game.reset();

        gameManager.destroy();

        if (queueManager != null) {
            queueManager.clearQueue();
        }

        starting = false;
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


    public void resetBorder() {

        WorldBorder border = gameManager.getGame().getArena().getWorld().getWorldBorder();

        border.setSize(6000);
        border.setCenter(gameManager.getGame().getArena().getBorderCenter());
        border.setWarningDistance(0);
        border.setDamageAmount(0);
        border.setDamageBuffer(0);
        border.reset();
    }

    public void setQueueManager(QueueManager queueManager) {
        this.queueManager = queueManager;
    }
}