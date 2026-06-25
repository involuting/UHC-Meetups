package me.involuting.meetups.listener;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.manager.GameManager;
import me.involuting.meetups.game.state.GameState;
import me.involuting.meetups.player.MeetupPlayer;
import me.involuting.meetups.player.PlayerManager;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final Meetups plugin;

    private final PlayerManager playerManager;

    private final GameManager gameManager;

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        playerManager.getOrCreate(event.getPlayer());

        event.getPlayer().setGameMode(GameMode.ADVENTURE);

        if (gameManager.hasGame()){

            Game game = gameManager.getGame();

            switch (game.getGameState()){
                case WAITING:
                case STARTING:
                    game.addPlayer(playerManager.getOrCreate(event.getPlayer()));
                    break;

                default:
                    MeetupPlayer meetupPlayer = playerManager.getOrCreate(event.getPlayer());
                    meetupPlayer.setSpectating(true);
                    game.addSpectator(meetupPlayer);
                    event.getPlayer().setGameMode(GameMode.SPECTATOR);
                    break;
            }
        }

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){

        if (!gameManager.isRunning()){
            return;
        }

        playerManager.get(event.getEntity()).ifPresent(player -> {
            player.addDeath();
            gameManager.getGame().removePlayer(player);
            player.setSpectating(true);

            gameManager.getGame().addSpectator(player);

            if (event.getEntity().getKiller() != null){
                playerManager.get(event.getEntity().getKiller())
                        .ifPresent(MeetupPlayer::addKill);
            }
        });
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){

        if (!gameManager.hasGame()){
            return;
        }

        if (gameManager.isState(GameState.PLAYING) ||
            gameManager.isState(GameState.DEATHMATCH)){

            event.getPlayer().setGameMode(GameMode.SPECTATOR);

        }
    }


}
