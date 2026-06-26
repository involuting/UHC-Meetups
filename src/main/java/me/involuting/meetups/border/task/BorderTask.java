package me.involuting.meetups.border.task;

import lombok.Getter;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.border.BorderManager;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class BorderTask extends BukkitRunnable {

    private final Meetups plugin;
    private final Game game;
    private final BorderManager borderManager;

    private int seconds;

    public BorderTask(Meetups plugin, Game game, BorderManager borderManager) {
        this.plugin = plugin;
        this.game = game;
        this.borderManager = borderManager;
    }

    public void start(int delay) {
        this.seconds = delay;
        runTaskTimer(plugin, 20L, 20L);
    }

    @Override
    public void run() {

        if (game.getGameState() != GameState.PLAYING) {
            cancel();
            return;
        }

        if (seconds <= 0) {
            Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "The border is now shrinking!");

            borderManager.shrink(game);

            cancel();
            onFinish();
            return;
        }

        if (seconds == 60 || seconds == 30 || seconds == 15 || seconds == 10 || seconds <= 5) {

            Bukkit.broadcastMessage(
                    ChatColor.YELLOW + "The border shrinks in "
                            + ChatColor.GOLD + seconds
                            + ChatColor.YELLOW + " seconds."
            );

            Bukkit.getOnlinePlayers().forEach(player ->
                    player.playSound(
                            player.getLocation(),
                            Sound.BLOCK_NOTE_BLOCK_PLING,
                            1F,
                            1F
                    ));
        }

        seconds--;
    }

    public abstract void onFinish();
}