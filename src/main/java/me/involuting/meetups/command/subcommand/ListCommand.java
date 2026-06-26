package me.involuting.meetups.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.arena.ArenaManager;
import me.involuting.meetups.arena.ArenaStorage;
import org.bukkit.entity.Player;

import java.util.Collection;

@RequiredArgsConstructor
public class ListCommand implements SubCommand {
    private final ArenaManager arenaManager;
    private final ArenaStorage arenaStorage;
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getPermission() {
        return "op";
    }

    @Override
    public String getUsage() {
        return "/meetups list";
    }

    @Override
    public void execute(Player player, String[] args) {
        Collection<Arena> arenas = arenaManager.getArenas();

        if (arenas.isEmpty()){
            player.sendMessage("&cThere are no arenas");
            return;
        }

        player.sendMessage("§6Arenas §7(" + arenas.size() + ")");

        for (Arena arena : arenas) {
            player.sendMessage(status(arena) + " §f" + arena.getName());
        }

    }

    private String status(Arena arena) {
        return arena.getWorld() != null
                && arena.getLobbySpawn() != null
                && arena.getSpectatorSpawn() != null
                && arena.getBorderCenter() != null
                ? "§a✔"
                : "§c✘";
    }
}
