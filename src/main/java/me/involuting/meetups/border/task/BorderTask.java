package me.involuting.meetups.border.task;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.border.BorderManager;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public abstract class BorderTask extends BukkitRunnable {
    private final Meetups plugin;
    private final Game game;
    private final BorderManager borderManager;

    private int seconds;

    public void start(int delay){
        this.seconds = delay;
        runTaskTimer(plugin, 20L, 20L);
    }
    @Override
    public void run() {

        if (game.getGameState() != GameState.PLAYING){
            cancel();
            return;
        }

        if (seconds == 0){
            Bukkit.broadcastMessage("&l&cThe border is now shrinking");

            borderManager.shrink();
            cancel();
            return;
        }

        if (seconds ==  60 || seconds == 30 || seconds == 10 || seconds == 5){
            Bukkit.broadcastMessage("&eThe border shrinks in &6" + seconds + "&e seconds");
        }

        seconds--;

    }

    public abstract void onFinish();
}
