package me.involuting.meetups.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.arena.ArenaManager;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class SetMinPlayersCommand implements SubCommand {

    private final ArenaManager arenaManager;
    private final Meetups plugin;

    @Override
    public String getName() {
        return "setminplayers";
    }

    @Override
    public String getPermission() {
        return "op";
    }

    @Override
    public String getUsage() {
        return "/meetup setminplayers (arena) (amount)";
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
            player.sendMessage("§cMinimum players must be at least 2.");
            return;
        }

        Arena arena = optional.get();

        if (arena.getMaxPlayers() > 0 && amount > arena.getMaxPlayers()) {
            player.sendMessage("§cMinimum players cannot be greater than the maximum players.");
            return;
        }

        arena.setMinPlayers(amount);
        plugin.getArenaStorage().save();




        player.sendMessage("§aMinimum players for arena §e" + arena.getName()
                + " §ahas been set to §e" + amount + "§a.");
    }
}