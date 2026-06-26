package me.involuting.meetups.game.countdown;

import lombok.Getter;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.state.GameState;
import me.involuting.meetups.scatter.ScatterManager;
import me.involuting.meetups.player.MeetupPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class GameCountdown extends BukkitRunnable {

    private final Meetups plugin;
    private final Game game;
    private final ScatterManager scatterManager;

    private int seconds;

    public GameCountdown(Meetups plugin, Game game, ScatterManager scatterManager, int seconds) {
        this.plugin = plugin;
        this.game = game;
        this.scatterManager = scatterManager;
        this.seconds = seconds;
    }

    @Override
    public void run() {

        long alivePlayers = game.getPlayers().stream()
                .filter(MeetupPlayer::isAlive)
                .count();

        if (alivePlayers < game.getArena().getMinPlayers()) {
            Bukkit.broadcastMessage("§cNot enough players to start yet.");
            game.setGameState(GameState.WAITING);
            cancel();
            return;
        }

        if (seconds <= 0) {
            Bukkit.broadcastMessage("§aThe game has started!");
            cancel();
            onFinish();
            return;
        }

        if (seconds == 30 || seconds == 15 || seconds == 10 || seconds <= 5) {

            Bukkit.broadcastMessage(
                    "§eUHC Meetups will start in §7" + seconds + " §eseconds."
            );

            for (MeetupPlayer mp : game.getPlayers()) {
                Player player = mp.getPlayer();
                if (player == null || !player.isOnline()) continue;

                player.playSound(
                        player.getLocation(),
                        Sound.BLOCK_NOTE_BLOCK_PLING,
                        1F,
                        1F
                );
            }
        }

        game.setCountdown(seconds);
        seconds--;
    }


    public  void start() {

        if (scatterManager != null) {
            scatterManager.precompute(game);
            Bukkit.broadcastMessage("§7Preparing scatter locations...");
        }

        game.setGameState(GameState.STARTING);

        runTaskTimer(plugin, 20L, 20L);
    }

    public abstract void onFinish();
}