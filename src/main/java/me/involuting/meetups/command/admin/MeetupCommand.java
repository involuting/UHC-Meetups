package me.involuting.meetups.command.admin;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.arena.ArenaStorage;
import me.involuting.meetups.command.subcommand.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class MeetupCommand implements CommandExecutor {

    private final ArenaStorage arenaStorage;

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public void register(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players may execute this command.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());

        if (subCommand == null) {
            player.sendMessage(ChatColor.RED + "Unknown subcommand.");
            sendHelp(player);
            return true;
        }

        if (!subCommand.getPermission().isEmpty() &&
                !player.hasPermission(subCommand.getPermission())) {

            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        subCommand.execute(player, args);
        return true;
    }

    private void sendHelp(Player player) {

        player.sendMessage(ChatColor.GOLD + "Meetups Commands");
        player.sendMessage(ChatColor.YELLOW + "/meetup create (arena)");
        player.sendMessage(ChatColor.YELLOW + "/meetup delete (arena)");
        player.sendMessage(ChatColor.YELLOW + "/meetup list");
        player.sendMessage(ChatColor.YELLOW + "/meetup info (arena)");
        player.sendMessage(ChatColor.YELLOW + "/meetup setworld (arena)");
        player.sendMessage(ChatColor.YELLOW + "/meetup setlobby (arena)");
        player.sendMessage(ChatColor.YELLOW + "/meetup setspectator (arena)");
        player.sendMessage(ChatColor.YELLOW + "/meetup setbordercenter (arena)");
        player.sendMessage(ChatColor.YELLOW + "/meetup setborder (arena) (start) (end) (deathmatch)");
        player.sendMessage(ChatColor.YELLOW + "/meetup setminplayers (arena) (amount)");
        player.sendMessage(ChatColor.YELLOW + "/meetup setmaxplayers (arena) (amount)");
        player.sendMessage(ChatColor.YELLOW + "/meetup setcountdown (arena) (seconds)");
        player.sendMessage(ChatColor.YELLOW + "/meetup setgrace (arena) (seconds)");
        player.sendMessage(ChatColor.YELLOW + "/meetup setborderdelay (arena) (seconds)");
        player.sendMessage(ChatColor.YELLOW + "/meetup setbordershrinktime (arena) (seconds)");
        player.sendMessage(ChatColor.YELLOW + "/meetup toggle (arena) (setting)");
        player.sendMessage(ChatColor.YELLOW + "/meetup scenario (arena) (scenario)");
    }
}