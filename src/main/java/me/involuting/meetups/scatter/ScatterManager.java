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
    private static final double MIN_DISTANCE_SQUARED = 2500D;

    private final Random random = new Random();

    private final Map<UUID, Location> scatterLocations = new HashMap<>();
    private final List<int[]> usedPositions = new ArrayList<>();

    public void precompute(Game game) {

        scatterLocations.clear();
        usedPositions.clear();

        World world = game.getArena().getWorld();
        Location center = game.getArena().getBorderCenter();
        int radius = (int) (game.getArena().getStartingBorderSize() / 2);

        List<MeetupPlayer> players = new ArrayList<>(game.getPlayers());

        for (MeetupPlayer player : players) {

            int[] pos = findSafePosition(world, center, radius);

            if (pos == null) continue;

            Location loc = toLocation(world, pos);

            scatterLocations.put(player.getUniqueID(), loc);
            usedPositions.add(pos);
        }
    }

    public void scatter(Game game) {

        for (MeetupPlayer meetupPlayer : game.getPlayers()) {

            Player player = meetupPlayer.getPlayer();
            if (player == null || !player.isOnline()) continue;

            Location loc = scatterLocations.get(meetupPlayer.getUniqueID());

            if (loc == null) {
                player.sendMessage("§cNo scatter location was generated.");
                continue;
            }

            player.teleport(loc);
        }

        scatterLocations.clear();
        usedPositions.clear();
    }

    private int[] findSafePosition(World world, Location center, int radius) {

        for (int i = 0; i < MAX_ATTEMPTS; i++) {

            int x = center.getBlockX() + random.nextInt(radius * 2) - radius;
            int z = center.getBlockZ() + random.nextInt(radius * 2) - radius;

            // chunk-heavy call reduced to only successful candidates
            int y = world.getHighestBlockYAt(x, z);

            if (!isSafe(world, x, y, z)) continue;

            if (isTooClose(x, z)) continue;

            return new int[]{x, z, y};
        }

        return null;
    }

    private boolean isSafe(World world, int x, int y, int z) {

        Material ground = world.getBlockAt(x, y - 1, z).getType();

        return ground.isSolid()
                && ground != Material.LAVA
                && ground != Material.WATER
                && ground != Material.CACTUS
                && ground != Material.MAGMA_BLOCK;
    }

    private boolean isTooClose(int x, int z) {

        for (int[] pos : usedPositions) {

            int dx = pos[0] - x;
            int dz = pos[1] - z;

            if (dx * dx + dz * dz < MIN_DISTANCE_SQUARED) {
                return true;
            }
        }

        return false;
    }

    private Location toLocation(World world, int[] pos) {
        return new Location(world, pos[0] + 0.5, pos[2] + 1, pos[1] + 0.5);
    }
}