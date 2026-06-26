package me.involuting.meetups.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.arena.ArenaManager;
import me.involuting.meetups.arena.ArenaStorage;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class SetMaxPlayersCommand implements SubCommand {

    private final Meetups plugin;

    private final ArenaManager arenaManager;
    private final ArenaStorage arenaStorage;

    @Override
    public String getName() {
        return "setmaxplayers";
    }

    @Override
    public String getPermission() {
        return "op";
    }

    @Override
    public String getUsage() {
        return "/meetup setmaxplayers <arena> <amount>";
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

        int amount;

        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException exception) {
            player.sendMessage("§cPlayer amount must be a valid number.");
            return;
        }

        if (amount < 2) {
            player.sendMessage("§cMaximum players must be at least 2.");
            return;
        }

        Arena arena = optional.get();

        if (amount < arena.getMinPlayers()) {
            player.sendMessage("§cMaximum players cannot be less than the minimum players.");
            return;
        }

        arena.setMaxPlayers(amount);
        arenaManager.save();



        player.sendMessage("§aMaximum players for arena §e" + arena.getName()
                + " §ahas been set to §e" + amount + "§a.");
    }
}