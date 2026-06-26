package me.involuting.meetups.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.arena.ArenaManager;
import me.involuting.meetups.arena.ArenaStorage;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class SetCountdownCommand implements SubCommand {

    private final ArenaManager arenaManager;
    private final Meetups plugin;
    private final ArenaStorage arenaStorage;
    @Override
    public String getName() {
        return "setcountdown";
    }

    @Override
    public String getPermission() {
        return "op";
    }

    @Override
    public String getUsage() {
        return "/meetup setcountdown (arena) (seconds)";
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
            player.sendMessage("§cCountdown must be a valid number.");
            return;
        }

        if (seconds < 0) {
            player.sendMessage("§cCountdown cannot be negative.");
            return;
        }

        Arena arena = optional.get();

        arena.setCountdown(seconds);

        arenaManager.save();

        player.sendMessage("§aCountdown for arena §e" + arena.getName()
                + " §ahas been set to §e" + seconds + " §aseconds.");
    }
}