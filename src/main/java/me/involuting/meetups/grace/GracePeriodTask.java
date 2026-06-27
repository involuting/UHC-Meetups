package me.involuting.meetups.grace;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.service.GameService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public abstract class GracePeriodTask extends BukkitRunnable {

    private final Meetups plugin;
    private final Game game;
    private final GameService gameService;

    private int seconds;

    public GracePeriodTask(
            Meetups plugin,
            Game game,
            GameService gameService,
            int seconds
    ) {
        this.plugin = plugin;
        this.game = game;
        this.gameService = gameService;
        this.seconds = seconds;
    }

    public void start() {
        runTaskTimer(plugin, 20L, 20L);
    }

    @Override
    public void run() {

        if (seconds <= 0) {

            Bukkit.broadcastMessage(
                    ChatColor.RED + "Grace period has ended and PvP is now enabled"
            );

            cancel();



            gameService.startPlaying(game);

            return;
        }

        if (seconds == 60
                || seconds == 30
                || seconds == 15
                || seconds == 10
                || seconds <= 5) {

            Bukkit.broadcastMessage(
                    ChatColor.GREEN + "Grace period ends in "
                            + ChatColor.GRAY + seconds
                            + ChatColor.GREEN + " seconds."
            );
        }

        seconds--;
    }

    public abstract void onFinish();
}