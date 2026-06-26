package me.involuting.meetups.deathmatch;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.service.GameService;
import me.involuting.meetups.game.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class DeathmatchTask extends BukkitRunnable {

    private final Meetups plugin;
    private final Game game;
    private final GameService gameService;
    private final int countdown;

    private int timeLeft;

    public void start() {
        this.timeLeft = countdown;
        runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void run() {

        if (timeLeft <= 0) {
            startDeathmatch();
            cancel();
            return;
        }

        if (timeLeft == countdown || timeLeft <= 10) {
            Bukkit.broadcastMessage(ChatColor.RED +
                    "Deathmatch starting in " + timeLeft + "s!");
        }

        timeLeft--;
    }

    private void startDeathmatch() {
        gameService.startDeathmatch();
    }
}