package me.involuting.meetups.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.arena.ArenaManager;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class SetBorderShrinkTimeCommand implements SubCommand {

    private final ArenaManager arenaManager;

    private final Meetups plugin;

    @Override
    public String getName() {
        return "setbordershrinktime";
    }

    @Override
    public String getPermission() {
        return "op";
    }

    @Override
    public String getUsage() {
        return "/meetup setbordershrinktime (arena) (seconds)";
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

        int seconds;

        try {
            seconds = Integer.parseInt(args[2]);
        } catch (NumberFormatException exception) {
            player.sendMessage("§cBorder shrink time must be a valid number.");
            return;
        }

        if (seconds < 0) {
            player.sendMessage("§cBorder shrink time cannot be negative.");
            return;
        }

        Arena arena = optional.get();

        arena.setBorderShrinkTime(seconds);

        plugin.getArenaStorage().save();



        player.sendMessage("§aBorder shrink time for arena §e" + arena.getName()
                + " §ahas been set to §e" + seconds + " §aseconds.");
    }
}