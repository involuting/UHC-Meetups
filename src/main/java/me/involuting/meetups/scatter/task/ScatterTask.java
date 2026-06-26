package me.involuting.meetups.scatter.task;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.state.GameState;
import me.involuting.meetups.scatter.ScatterManager;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public abstract class ScatterTask extends BukkitRunnable {

    private final Meetups plugin;
    private final Game game;
    private final ScatterManager scatterManager;

    public void start() {
        game.setGameState(GameState.SCATTERING);
        runTaskLater(plugin, 1L);
    }

    @Override
    public void run() {

        scatterManager.scatter(game);

        cancel();
        onFinish();
    }

    public abstract void onFinish();
}