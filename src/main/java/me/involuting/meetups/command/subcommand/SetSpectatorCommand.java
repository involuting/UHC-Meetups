package me.involuting.meetups.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.arena.ArenaManager;
import me.involuting.meetups.arena.ArenaStorage;
import org.bukkit.entity.Player;

import javax.swing.text.html.Option;
import java.util.Optional;

@RequiredArgsConstructor
public class SetSpectatorCommand implements SubCommand {
    private final ArenaManager arenaManager;
    private final Meetups plugin;
    private final ArenaStorage arenaStorage;
    @Override
    public String getName() {
        return "setspectator";
    }

    @Override
    public String getPermission() {
        return "op";
    }

    @Override
    public String getUsage() {
        return "/meetups setspectator (arena)";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage("§cUsage: " + getUsage());
            return;
        }

        Optional<Arena> optional = arenaManager.getArena(args[1]);

        if (optional.isEmpty()){
            player.sendMessage("&cThat arena does not exist.");
        }
        Arena arena = optional.get();

        arena.setSpectatorSpawn(player.getLocation());

        arenaManager.save();
        player.sendMessage("§aSpectator spawn for arena §e" + arena.getName() + " §ahas been updated.");
    }
}
