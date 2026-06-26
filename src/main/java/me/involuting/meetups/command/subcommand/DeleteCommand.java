package me.involuting.meetups.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.arena.ArenaManager;
import me.involuting.meetups.arena.ArenaStorage;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class DeleteCommand implements SubCommand{

    private final Meetups plugin;
    private final ArenaManager arenaManager;
    private final ArenaStorage arenaStorage;

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getPermission() {
        return "op";
    }

    @Override
    public String getUsage() {
        return "/meetups delete (arena)";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length != 2){
            player.sendMessage(getUsage());
        }

        String name = args[1];

        Optional<Arena> optional = arenaManager.getArena(name);

        if (optional.isEmpty()){
            player.sendMessage("&cAn arena with that name does not exist");
            return;
        }

        arenaManager.unregister(String.valueOf(optional.get()));
        arenaManager.save();

        player.sendMessage("§aSuccessfully deleted arena §e" + name + "§a.");

    }
}
