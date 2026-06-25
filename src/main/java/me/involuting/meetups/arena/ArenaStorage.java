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

@RequiredArgsConstructor
public class ArenaStorage {

    private final Meetups plugin;
    private final ArenaManager arenaManager;

    private File file;
    private YamlConfiguration config;

    public void load(){

        file =  new File(plugin.getDataFolder(), "arenas.yml");

        if (!file.exists()){
            try {
                plugin.getDataFolder().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

        arenaManager.getArenas().clear();

        ConfigurationSection section = config.getConfigurationSection("arenas");

        if (section == null){
            return;
        }

        for (String name : section.getKeys(false)){

            ConfigurationSection arenaSection = section.getConfigurationSection(name);

            if (arenaSection == null){
                continue;

            }

            World world = Bukkit.getWorld(arenaSection.getString("world"));

            if (world == null){
                continue;
            }

            Arena arena = new Arena(name);

            arena.setWorld(world);
            arena.setLobbySpawn(readLocation(world, arenaSection, "lobby"));
            arena.setSpectatorSpawn(readLocation(world, arenaSection, "spectator"));
            arena.setBorderCenter(readLocation(world, arenaSection, "border-center"));
            arena.setStartingBorderSize(arenaSection.getDouble("border.start"));
            arena.setEndingBorderSize(arenaSection.getDouble("border.end"));
            arena.setDeathmatchBorderSize(arenaSection.getDouble("border.deathmatch"));

            arena.setMinPlayers(arenaSection.getInt("players.min"));
            arena.setMaxPlayers(arenaSection.getInt("players.max"));

            arena.setCountdown(arenaSection.getInt("countdown"));
            arena.setGracePeriod(arenaSection.getInt("grace-period"));
            arena.setBorderDelay(arenaSection.getInt("border-delay"));
            arena.setBorderShrinkTime(arenaSection.getInt("border-shrink-time"));

            arena.setAllowNether(arenaSection.getBoolean("allow-nether"));
            arena.setAllowEnd(arenaSection.getBoolean("allow-end"));
            arena.setAllowSpectators(arenaSection.getBoolean("allow-spectators"));

            arena.setCutClean(arenaSection.getBoolean("scenarios.cut-clean"));
            arena.setGoldenHeads(arenaSection.getBoolean("scenarios.golden-heads"));
            arena.setNaturalRegeneration(arenaSection.getBoolean("scenarios.natural-regeneration"));
            arena.setDeathmatchEnabled(arenaSection.getBoolean("scenarios.deathmatch"));
            arenaManager.register(arena);
        }
    }

    public void save() {

        config.set("arenas", null);

        for (Arena arena : arenaManager.getArenas()) {

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
            exception.printStackTrace();
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

