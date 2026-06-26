package me.involuting.meetups.arena;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

@RequiredArgsConstructor
public class ArenaStorage {

    private final Meetups plugin;
    private final ArenaManager arenaManager;

    private File file;
    private YamlConfiguration config;

    public void load() {

        file = new File(plugin.getDataFolder(), "arenas.yml");

        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();

            try {
                file.createNewFile();
            } catch (IOException exception) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create arenas.yml.", exception);
                return;
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

        arenaManager.getArenas().clear();

        ConfigurationSection arenasSection = config.getConfigurationSection("arenas");

        if (arenasSection == null) {
            return;
        }

        for (String name : arenasSection.getKeys(false)) {

            ConfigurationSection section = arenasSection.getConfigurationSection(name);

            if (section == null) {
                continue;
            }

            String worldName = section.getString("world");

            if (worldName == null) {
                plugin.getLogger().warning("Arena '" + name + "' has no world configured.");
                continue;
            }

            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                plugin.getLogger().warning("Arena '" + name + "' could not be loaded because world '" + worldName + "' is not loaded.");
                continue;
            }

            Arena arena = new Arena(name);

            arena.setWorld(world);

            arena.setLobbySpawn(readLocation(world, section, "lobby"));
            arena.setSpectatorSpawn(readLocation(world, section, "spectator"));
            arena.setBorderCenter(readLocation(world, section, "border-center"));

            arena.setStartingBorderSize(section.getDouble("border.start", 1000));
            arena.setEndingBorderSize(section.getDouble("border.end", 50));
            arena.setDeathmatchBorderSize(section.getDouble("border.deathmatch", 25));

            arena.setMinPlayers(section.getInt("players.min", 2));
            arena.setMaxPlayers(section.getInt("players.max", 100));

            arena.setCountdown(section.getInt("countdown", 60));
            arena.setGracePeriod(section.getInt("grace-period", 300));
            arena.setBorderDelay(section.getInt("border-delay", 600));
            arena.setBorderShrinkTime(section.getInt("border-shrink-time", 900));

            arena.setAllowNether(section.getBoolean("allow-nether", false));
            arena.setAllowEnd(section.getBoolean("allow-end", false));
            arena.setAllowSpectators(section.getBoolean("allow-spectators", true));

            arena.setCutClean(section.getBoolean("scenarios.cut-clean", false));
            arena.setGoldenHeads(section.getBoolean("scenarios.golden-heads", false));
            arena.setNaturalRegeneration(section.getBoolean("scenarios.natural-regeneration", false));
            arena.setDeathmatchEnabled(section.getBoolean("scenarios.deathmatch", true));

            arenaManager.register(arena);
        }
    }

    public void save() {

        if (config == null || file == null) {
            return;
        }

        config.set("arenas", null);

        for (Arena arena : arenaManager.getArenas()) {

            if (arena.getWorld() == null) {
                continue;
            }

            String path = "arenas." + arena.getName();

            config.set(path + ".world", arena.getWorld().getName());

            writeLocation(path + ".lobby", arena.getLobbySpawn());
            writeLocation(path + ".spectator", arena.getSpectatorSpawn());
            writeLocation(path + ".border-center", arena.getBorderCenter());

            config.set(path + ".border.start", arena.getStartingBorderSize());
            config.set(path + ".border.end", arena.getEndingBorderSize());
            config.set(path + ".border.deathmatch", arena.getDeathmatchBorderSize());

            config.set(path + ".players.min", arena.getMinPlayers());
            config.set(path + ".players.max", arena.getMaxPlayers());

            config.set(path + ".countdown", arena.getCountdown());
            config.set(path + ".grace-period", arena.getGracePeriod());
            config.set(path + ".border-delay", arena.getBorderDelay());
            config.set(path + ".border-shrink-time", arena.getBorderShrinkTime());

            config.set(path + ".allow-nether", arena.isAllowNether());
            config.set(path + ".allow-end", arena.isAllowEnd());
            config.set(path + ".allow-spectators", arena.isAllowSpectators());

            config.set(path + ".scenarios.cut-clean", arena.isCutClean());
            config.set(path + ".scenarios.golden-heads", arena.isGoldenHeads());
            config.set(path + ".scenarios.natural-regeneration", arena.isNaturalRegeneration());
            config.set(path + ".scenarios.deathmatch", arena.isDeathmatchEnabled());
        }

        try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save arenas.yml.", exception);
        }
    }

    private void writeLocation(String path, Location location) {

        if (location == null) {
            return;
        }

        config.set(path + ".x", location.getX());
        config.set(path + ".y", location.getY());
        config.set(path + ".z", location.getZ());

        config.set(path + ".yaw", location.getYaw());
        config.set(path + ".pitch", location.getPitch());
    }

    private Location readLocation(World world, ConfigurationSection section, String path) {

        if (!section.contains(path)) {
            return null;
        }

        return new Location(
                world,
                section.getDouble(path + ".x"),
                section.getDouble(path + ".y"),
                section.getDouble(path + ".z"),
                (float) section.getDouble(path + ".yaw"),
                (float) section.getDouble(path + ".pitch")
        );
    }
}