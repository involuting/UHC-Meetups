package me.involuting.meetups.game.countdown;

import lombok.Getter;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.state.GameState;
import me.involuting.meetups.player.MeetupPlayer;
import me.involuting.meetups.scatter.ScatterManager;
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
    private boolean running = false;

    public GameCountdown(Meetups plugin, Game game, ScatterManager scatterManager, int seconds) {
        this.plugin = plugin;
        this.game = game;
        this.scatterManager = scatterManager;
        this.seconds = seconds;
    }

    @Override
    public void run() {

        if (!running) {
            cancel();
            return;
        }

        if (game.getGameState() != GameState.STARTING) {
            cancel();
            onCancel();
            return;
        }

        long alivePlayers = game.getPlayers().stream()
                .filter(MeetupPlayer::isAlive)
                .count();

        if (alivePlayers < game.getArena().getMinPlayers()) {
            Bukkit.broadcastMessage("§cNot enough players to start yet.");
            game.setGameState(GameState.WAITING);
            cancel();
            onCancel();
            return;
        }

        if (seconds <= 0) {
            cancel();
            onFinish();
            return;
        }

        if (shouldAnnounce(seconds)) {
            Bukkit.broadcastMessage(
                    "§eUHC Meetups starts in §6" + seconds + "§e seconds."
            );

            playTickSound();
        }

        game.setCountdown(seconds);
        seconds--;
    }

    private boolean shouldAnnounce(int seconds) {
        return seconds == 30 || seconds == 20 || seconds == 10 || seconds <= 5;
    }

    private void playTickSound() {

        for (MeetupPlayer mp : game.getPlayers()) {

            Player player = mp.getPlayer();
            if (player == null || !player.isOnline()) continue;

            player.playSound(
                    player.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_PLING,
                    1F,
                    1.2F
            );
        }
    }



    public void start() {

        if (running) return;

        running = true;

        runTaskTimer(plugin, 20L, 20L);
    }

    public void stop() {
        running = false;
        cancel();
    }



    public abstract void onFinish();

    public void onCancel() {
        // optional override
    }
}