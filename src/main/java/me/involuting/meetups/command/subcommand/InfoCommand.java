package me.involuting.meetups.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.arena.ArenaManager;
import me.involuting.meetups.arena.ArenaStorage;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class InfoCommand implements SubCommand {
    private final ArenaManager arenaManager;
    private final ArenaStorage arenaStorage;

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getPermission() {
        return "op";
    }

    @Override
    public String getUsage() {
        return "/meetups info (arena)";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(getUsage());
            return;
        }

        Optional<Arena> optional = arenaManager.getArena(args[1]);

        Arena arena = optional.get();

        player.sendMessage("§7§m----------------------------------------");
        player.sendMessage("§6Arena: §e" + arena.getName());

        player.sendMessage("§7World: §f" +
                (arena.getWorld() == null ? "Not Set" : arena.getWorld().getName()));

        player.sendMessage("§7Lobby: §f" + formatLocation(arena.getLobbySpawn()));
        player.sendMessage("§7Spectator: §f" + formatLocation(arena.getSpectatorSpawn()));
        player.sendMessage("§7Border Center: §f" + formatLocation(arena.getBorderCenter()));

        player.sendMessage("");

        player.sendMessage("§6Border");
        player.sendMessage(" §7Starting: §f" + arena.getStartingBorderSize());
        player.sendMessage(" §7Ending: §f" + arena.getEndingBorderSize());
        player.sendMessage(" §7Deathmatch: §f" + arena.getDeathmatchBorderSize());

        player.sendMessage("");

        player.sendMessage("§6Players");
        player.sendMessage(" §7Minimum: §f" + arena.getMinPlayers());
        player.sendMessage(" §7Maximum: §f" + arena.getMaxPlayers());

        player.sendMessage("");

        player.sendMessage("§6Timers");
        player.sendMessage(" §7Countdown: §f" + arena.getCountdown());
        player.sendMessage(" §7Grace Period: §f" + arena.getGracePeriod());
        player.sendMessage(" §7Border Delay: §f" + arena.getBorderDelay());
        player.sendMessage(" §7Border Shrink: §f" + arena.getBorderShrinkTime());

        player.sendMessage("");

        player.sendMessage("§6Settings");
        player.sendMessage(" §7Allow Nether: " + bool(arena.isAllowNether()));
        player.sendMessage(" §7Allow End: " + bool(arena.isAllowEnd()));
        player.sendMessage(" §7Allow Spectators: " + bool(arena.isAllowSpectators()));

        player.sendMessage("");

        player.sendMessage("§6Scenarios");
        player.sendMessage(" §7Cut Clean: " + bool(arena.isCutClean()));
        player.sendMessage(" §7Golden Heads: " + bool(arena.isGoldenHeads()));
        player.sendMessage(" §7Natural Regeneration: " + bool(arena.isNaturalRegeneration()));
        player.sendMessage(" §7Deathmatch: " + bool(arena.isDeathmatchEnabled()));

        player.sendMessage("§7§m----------------------------------------");


    }

    private String formatLocation(Location location) {
        if (location == null) {
            return "§cNot Set";
        }

        return "§a"
                + location.getBlockX() + ", "
                + location.getBlockY() + ", "
                + location.getBlockZ();
    }

    private String bool(boolean value) {
        return value ? "§aEnabled" : "§cDisabled";
    }
}

