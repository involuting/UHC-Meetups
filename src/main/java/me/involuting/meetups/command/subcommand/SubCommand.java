package me.involuting.meetups.command.subcommand;

import org.bukkit.entity.Player;

public interface SubCommand {

    String getName();

    String getPermission();

    String getUsage();

    void execute(Player player, String[] args);
}
