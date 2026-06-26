package me.involuting.meetups.command.admin;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.arena.ArenaManager;
import me.involuting.meetups.command.subcommand.SubCommand;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class ToggleCommand implements SubCommand {

    private final ArenaManager arenaManager;
    private final Meetups plugin;

    @Override
    public String getName() {
        return "toggle";
    }

    @Override
    public String getPermission() {
        return "op";
    }

    @Override
    public String getUsage() {
        return "/meetup toggle (arena) (nether|end|spectators)";
    }

    @Override
    public void execute(Player player, String[] args) {

        if (args.length != 3) {
            player.sendMessage("§cUsage: " + getUsage());
            return;
        }

        Optional<Arena> optional = arenaManager.getArena(args[1]);

        if (optional.isEmpty()) {
            player.sendMessage("§cThat arena does not exist.");
            return;
        }

        Arena arena = optional.get();

        switch (args[2].toLowerCase()) {

            case "nether":
                arena.setAllowNether(!arena.isAllowNether());

                player.sendMessage("§aNether is now "
                        + (arena.isAllowNether() ? "§aenabled" : "§cdisabled")
                        + " §afor arena §e" + arena.getName());
                break;

            case "end":
                arena.setAllowEnd(!arena.isAllowEnd());

                player.sendMessage("§aThe End is now "
                        + (arena.isAllowEnd() ? "§aenabled" : "§cdisabled")
                        + " §afor arena §e" + arena.getName());
                break;

            case "spectators":
                arena.setAllowSpectators(!arena.isAllowSpectators());

                player.sendMessage("§aSpectators are now "
                        + (arena.isAllowSpectators() ? "§aenabled" : "§cdisabled")
                        + " §afor arena §e" + arena.getName());
                break;

            default:
                player.sendMessage("§cUnknown setting.");
                player.sendMessage("§7Available: nether, end, spectators");
                break;
        }


    }
}