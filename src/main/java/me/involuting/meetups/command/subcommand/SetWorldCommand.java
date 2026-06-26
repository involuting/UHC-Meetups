package me.involuting.meetups.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.arena.ArenaManager;
import me.involuting.meetups.arena.ArenaStorage;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class SetWorldCommand implements SubCommand{
    private final ArenaManager arenaManager;
    private final Meetups plugin;
    private final ArenaStorage arenaStorage;
    @Override
    public String getName() {
        return "setworld";
    }

    @Override
    public String getPermission() {
        return "op";
    }

    @Override
    public String getUsage() {
        return "/meetups setworld (arena)";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage("§cUsage: " + getUsage());
            return;
        }

        Optional<Arena> optional = arenaManager.getArena(args[1]);

        if (optional.isEmpty()) {
            player.sendMessage("§cThat arena does not exist.");
            return;
        }

        Arena arena = optional.get();

        arena.setWorld(player.getWorld());

        arenaManager.save();

        player.sendMessage("§aWorld for arena §e" + arena.getName()
                + " §ahas been set to §e" + player.getWorld().getName() + "§a.");

    }
}
