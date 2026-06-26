package me.involuting.meetups.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.arena.ArenaManager;
import me.involuting.meetups.game.service.GameService;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class JoinGameCommand implements SubCommand {

    private final GameService gameService;
    private final ArenaManager arenaManager;

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getPermission() {
        return "op";
    }

    @Override
    public String getUsage() {
        return "/meetups join";
    }

    @Override
    public void execute(Player player, String[] args) {

        Arena arena = arenaManager.getCurrentArena();

        if (arena == null) {
            player.sendMessage("§cThere is no active arena.");
            return;
        }

        gameService.join(player, arena);
    }
}