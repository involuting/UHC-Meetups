package me.involuting.meetups.command.admin;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.arena.ArenaManager;
import me.involuting.meetups.command.subcommand.SubCommand;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class ScenarioCommand implements SubCommand {

    private final ArenaManager arenaManager;

    private final Meetups plugin;

    @Override
    public String getName() {
        return "scenario";
    }

    @Override
    public String getPermission() {
        return "meetups.admin";
    }

    @Override
    public String getUsage() {
        return "/meetup scenario (arena) (cutclean|goldenheads|naturalregen|deathmatch)";
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

            case "cutclean":
                arena.setCutClean(!arena.isCutClean());

                player.sendMessage("§aCutClean is now "
                        + (arena.isCutClean() ? "§aenabled" : "§cdisabled")
                        + " §afor arena §e" + arena.getName());
                break;

            case "goldenheads":
                arena.setGoldenHeads(!arena.isGoldenHeads());

                player.sendMessage("§aGolden Heads is now "
                        + (arena.isGoldenHeads() ? "§aenabled" : "§cdisabled")
                        + " §afor arena §e" + arena.getName());
                break;

            case "naturalregen":
                arena.setNaturalRegeneration(!arena.isNaturalRegeneration());

                player.sendMessage("§aNatural Regeneration is now "
                        + (arena.isNaturalRegeneration() ? "§aenabled" : "§cdisabled")
                        + " §afor arena §e" + arena.getName());
                break;

            case "deathmatch":
                arena.setDeathmatchEnabled(!arena.isDeathmatchEnabled());

                player.sendMessage("§aDeathmatch is now "
                        + (arena.isDeathmatchEnabled() ? "§aenabled" : "§cdisabled")
                        + " §afor arena §e" + arena.getName());
                break;

            default:
                player.sendMessage("§cUnknown scenario.");
                player.sendMessage("§7Available: cutclean, goldenheads, naturalregen, deathmatch");
                return;

        }
        arenaManager.save();

    }
}