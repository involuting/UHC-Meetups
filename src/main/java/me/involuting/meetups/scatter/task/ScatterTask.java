package me.involuting.meetups.scatter.task;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.state.GameState;
import me.involuting.meetups.player.MeetupPlayer;
import me.involuting.meetups.scatter.ScatterManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

@RequiredArgsConstructor
public abstract class ScatterTask extends BukkitRunnable {

    private final Meetups plugin;
    private final Game game;
    private final ScatterManager scatterManager;

    private Iterator<MeetupPlayer> iterator;

    public void start() {

        game.setGameState(GameState.SCATTERING);

        scatterManager.reset();

        iterator = game.getPlayers().iterator();

        runTaskTimer(plugin, 1L, 2L);
    }

    @Override
    public void run() {

        if (!iterator.hasNext()) {
            cancel();
            onFinish();
            return;
        }

        MeetupPlayer meetupPlayer = iterator.next();

        Player player = meetupPlayer.getPlayer();

        if (player == null || !player.isOnline()) {
            return;
        }

        Location location = scatterManager.findScatterLocation(game);

        if (location == null) {
            player.sendMessage("§cUnable to find a safe scatter location.");
            return;
        }

        player.teleport(location);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();

        meetupPlayer.setScattered(true);
    }

    public abstract void onFinish();
}