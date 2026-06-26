package me.involuting.meetups.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.GameRule;

public class WorldManager {

    private World world;

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public void prepareWorld() {
        if (world == null) return;

        world.setTime(1000);
        world.setStorm(false);
        world.setThundering(false);

        world.setPVP(false);

        world.setGameRule(GameRule.ADVANCE_WEATHER, false);
        world.setGameRule(GameRule.SPAWN_MOBS, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
    }

    public void enablePVP() {
        if (world == null) return;
        world.setPVP(true);
    }

    public void disablePVP() {
        if (world == null) return;
        world.setPVP(false);
    }

    public void resetWorld() {
        if (world == null) return;

        world.setTime(1000);
        world.setStorm(false);
        world.setThundering(false);
        world.setPVP(false);
    }
}