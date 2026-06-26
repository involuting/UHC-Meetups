package me.involuting.meetups.scatter;

import me.involuting.meetups.game.Game;
import me.involuting.meetups.player.MeetupPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ScatterManager {

    private static final int MAX_ATTEMPTS = 100;
    private static final double MIN_DISTANCE_SQUARED = 16; // 4 blocks spacing

    private final Random random = new Random();

    public void scatter(Game game) {

        Set<Location> usedLocations = new HashSet<>();

        for (MeetupPlayer player : game.getPlayers()) {

            if (player == null || player.getPlayer() == null) continue;

            Location location = findSafeLocation(
                    game.getArena().getWorld(),
                    game.getArena().getBorderCenter(),
                    game.getArena().getStartingBorderSize(),
                    usedLocations
            );

            if (location == null) continue;

            player.getPlayer().teleport(location);
            usedLocations.add(location);
        }
    }

    public Location findSafeLocation(World world, Location center, double borderSize, Set<Location> usedLocations) {

        int radius = (int) (borderSize / 2);

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {

            int x = center.getBlockX() + random.nextInt(radius * 2) - radius;
            int z = center.getBlockZ() + random.nextInt(radius * 2) - radius;

            int y = world.getHighestBlockYAt(x, z);

            Location location = new Location(world, x + 0.5, y + 1, z + 0.5);

            if (!isSafe(location)) continue;

            if (isTooClose(location, usedLocations)) continue;

            return location;
        }

        return null;
    }

    private boolean isSafe(Location location) {

        if (location == null) return false;

        Material ground = location.clone()
                .subtract(0, 1, 0)
                .getBlock()
                .getType();

        return ground.isSolid()
                && ground != Material.LAVA
                && ground != Material.WATER;
    }

    private boolean isTooClose(Location location, Set<Location> used) {

        for (Location loc : used) {
            if (loc.distanceSquared(location) < MIN_DISTANCE_SQUARED) {
                return true;
            }
        }
        return false;
    }
}