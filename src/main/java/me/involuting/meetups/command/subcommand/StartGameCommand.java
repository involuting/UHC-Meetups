package me.involuting.meetups.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.arena.ArenaManager;
import me.involuting.meetups.game.manager.GameManager;
import me.involuting.meetups.game.service.GameService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class StartGameCommand implements SubCommand {

    private final ArenaManager arenaManager;
    private final GameManager gameManager;
    private final GameService gameService;

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getPermission() {
        return "op";
    }

    @Override
    public String getUsage() {
        return "/meetup start (arena)";
    }

    @Override
    public void execute(Player player, String[] args) {

        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return;
        }

        Arena arena = arenaManager.getArena(args[1]).orElse(null);

        if (arena == null) {
            player.sendMessage(ChatColor.RED + "That arena does not exist.");
            return;
        }

        if (gameManager.hasGame()) {
            player.sendMessage(ChatColor.RED + "A game is already running.");
            return;
        }

        gameService.start(arena);

        player.sendMessage(ChatColor.GREEN + "Starting game in arena "
                + ChatColor.YELLOW + arena.getName()
                + ChatColor.GREEN + "...");
    }
}