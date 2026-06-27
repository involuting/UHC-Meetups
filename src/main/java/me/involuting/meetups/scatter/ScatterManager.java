package me.involuting.meetups.scatter;

import me.involuting.meetups.game.Game;
import me.involuting.meetups.player.MeetupPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class ScatterManager {

    private static final int MAX_ATTEMPTS = 60;
    private static final int MIN_DISTANCE = 100;

    private final Random random = new Random();

    private final List<Location> usedLocations = new ArrayList<>();

    public void reset() {
        usedLocations.clear();
    }

    public Location findScatterLocation(Game game) {

        World world = game.getArena().getWorld();
        Location center = game.getArena().getBorderCenter();
        int radius = (int) game.getArena().getStartingBorderSize() / 2;

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {

            double angle = random.nextDouble() * Math.PI * 2;
            double distance = Math.sqrt(random.nextDouble()) * radius;

            int x = center.getBlockX() + (int) (Math.cos(angle) * distance);
            int z = center.getBlockZ() + (int) (Math.sin(angle) * distance);

            if (isTooClose(x, z)){
                continue;
            }

            int y = world.getHighestBlockYAt(x, z);

            Location location = new Location(world, x + 0.5, y + 1, z + 0.5);

            if (tooClose(location))
                continue;

            if (!isSafe(location))
                continue;


            usedLocations.add(location);

            return location;
        }

        return center.clone().add(0.5, 100, 0.5);
    }

    private boolean tooClose(Location location) {

        for (Location other : usedLocations) {

            if (other.distanceSquared(location) < MIN_DISTANCE * MIN_DISTANCE) {
                return true;
            }

        }

        return false;
    }

    public boolean isTooClose(int x, int z){
        for (Location other : usedLocations){
            double dx = other.getX()  - x;
            double dz = other.getZ() - z;
            double distance = Math.sqrt(dx * dx + dz * dz);

            if (distance < MIN_DISTANCE * MIN_DISTANCE){
                return true;
            }
        }


        return false;
    }

    private boolean isSafe(Location location) {

        Material ground = location.clone()
                .subtract(0, 1, 0)
                .getBlock()
                .getType();

        return ground.isSolid()
                && ground != Material.WATER
                && ground != Material.LAVA
                && ground != Material.CACTUS
                && ground != Material.MAGMA_BLOCK;
    }
}