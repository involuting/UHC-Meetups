package me.involuting.meetups.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.game.service.GameService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
@RequiredArgsConstructor
public class JoinGameCommand implements SubCommand {

    private final GameService gameService;


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
        gameService.join(player);

    }
}