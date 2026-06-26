package me.involuting.meetups.command.subcommand;


import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.arena.ArenaManager;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class SetBorderCommand implements SubCommand {

    private final ArenaManager arenaManager;
    private final Meetups plugin;

    @Override
    public String getName() {
        return "setborder";
    }

    @Override
    public String getPermission() {
        return "op";
    }

    @Override
    public String getUsage() {
        return "/meetup setborder (arena) (start) (end) (deathmatch)";
    }

    @Override
    public void execute(Player player, String[] args) {

        if (args.length != 5) {
            player.sendMessage("§cUsage: " + getUsage());
            return;
        }

        Optional<Arena> optional = arenaManager.getArena(args[1]);

        if (optional.isEmpty()) {
            player.sendMessage("§cThat arena does not exist.");
            return;
        }

        double start;
        double end;
        double deathmatch;

        try {
            start = Double.parseDouble(args[2]);
            end = Double.parseDouble(args[3]);
            deathmatch = Double.parseDouble(args[4]);
        } catch (NumberFormatException exception) {
            player.sendMessage("§cBorder sizes must be valid numbers.");
            return;
        }

        if (start <= 0 || end <= 0 || deathmatch <= 0) {
            player.sendMessage("§cBorder sizes must be greater than 0.");
            return;
        }

        if (start <= end) {
            player.sendMessage("§cThe starting border must be larger than the ending border.");
            return;
        }

        if (end <= deathmatch) {
            player.sendMessage("§cThe ending border must be larger than the deathmatch border.");
            return;
        }

        Arena arena = optional.get();

        arena.setStartingBorderSize(start);
        arena.setEndingBorderSize(end);
        arena.setDeathmatchBorderSize(deathmatch);

        plugin.getArenaStorage().save();



        player.sendMessage("§aUpdated border settings for arena §e" + arena.getName() + "§a.");
        player.sendMessage("§7Starting: §f" + start);
        player.sendMessage("§7Ending: §f" + end);
        player.sendMessage("§7Deathmatch: §f" + deathmatch);
    }
}
