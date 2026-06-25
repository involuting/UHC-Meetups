package me.involuting.meetups.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.arena.ArenaManager;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class CreateCommand implements SubCommand{

    private final Meetups plugin;

    private final ArenaManager arenaManager;

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "";
    }

    public void execute(Player player, String[] args){

        if (args.length < 2){
            player.sendMessage("&c/meetup create (name)");
            return;
        }

        String name = args[1];

        if (arenaManager.exists(name)){
            player.sendMessage("&cAn arena already exists with that name");
            return;
        }

        Arena arena = new Arena(name);

        arena.setWorld(player.getWorld());
        arena.setLobbySpawn(player.getLocation());

        arenaManager.register(arena);

        player.sendMessage("&cCreated Arena &e(" + name + " )");
    }
}
