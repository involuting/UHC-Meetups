package me.involuting.meetups.scatter;

import me.involuting.meetups.game.Game;
import me.involuting.meetups.player.MeetupPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class ScatterManager {

    private static final int MAX_ATTEMPTS = 100;

    private final Random random = new Random();

    public void scatter(Game game){
        Set<Location> useLocations = new HashSet<>();

        for (MeetupPlayer player :  game.getPlayers()){
            Location location = findSafeLocation(
                    game.getArena().getWorld(),
                    game.getArena().getBorderCenter(),
                    game.getArena().getStartingBorderSize(),
                    useLocations
                    );

            player.getPlayer().teleport(location);

            useLocations.add(location);
        }
    }

    public Location findSafeLocation(World world, Location center, double borderSize, Set<Location> usedLocations){

        int radius = (int) (borderSize / 2) ;

        for (int attempt = 0; attempt < MAX_ATTEMPTS;  attempt++){

            int x = center.getBlockX() + random.nextInt(radius * 2) - radius;
            int z = center.getBlockZ() + random.nextInt(radius * 2) - radius;

            int y = world.getHighestBlockYAt(x, z);

            Location location = new Location(world, x + 0.5, y + 1, z + 0.5);

            if (isSafe(location)){
                continue;
            }

            if (usedLocations.stream().anyMatch(loc -> loc.distanceSquared(location) < 2500)){
                continue;
            }

            return location;
        }
        throw new IllegalStateException("Unable to find a safe scatter location.");

    }

    private boolean isSafe(Location location) {

        Material ground = location.clone().subtract(0, 1, 0).getBlock().getType();

        return ground != Material.LAVA
                && ground != Material.WATER;
    }
}
