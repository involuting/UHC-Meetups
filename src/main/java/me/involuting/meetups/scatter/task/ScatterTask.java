package me.involuting.meetups.scatter.task;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.state.GameState;
import me.involuting.meetups.player.MeetupPlayer;
import me.involuting.meetups.scatter.ScatterManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

@RequiredArgsConstructor
public class ScatterTask extends BukkitRunnable {

    private final Meetups plugin;

    private final Game game;
    private final ScatterManager scatterManager;

    private Iterator<MeetupPlayer> iterator;

    public void start(){
        this.iterator = game.getPlayers().iterator();
        runTaskTimer(plugin, 0L, 1L);
    }


    @Override
    public void run() {

        if (!iterator.hasNext()){

            game.setGameState(GameState.PLAYING);

            cancel();
            return;
        }

        MeetupPlayer meetupPlayer = iterator.next();

        scatterManager.scatter(game);

    }
}
