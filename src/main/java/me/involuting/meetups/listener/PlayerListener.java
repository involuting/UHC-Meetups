package me.involuting.meetups.listener;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.manager.GameManager;
import me.involuting.meetups.game.service.GameService;
import me.involuting.meetups.game.state.GameState;
import me.involuting.meetups.player.MeetupPlayer;
import me.involuting.meetups.player.PlayerManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final PlayerManager playerManager;
    private final GameManager gameManager;
    private final GameService gameService;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        MeetupPlayer meetupPlayer = playerManager.getOrCreate(event.getPlayer());

        event.getPlayer().setGameMode(GameMode.ADVENTURE);

        if (!gameManager.hasGame()) {
            return;
        }

        Game game = gameManager.getGame();

        switch (game.getGameState()) {

            case WAITING:
            case STARTING:
                meetupPlayer.setAlive(true);
                meetupPlayer.setSpectating(false);

                game.addPlayer(meetupPlayer);
                break;

            default:
                meetupPlayer.setAlive(false);
                meetupPlayer.setSpectating(true);

                game.addSpectator(meetupPlayer);

                event.getPlayer().setGameMode(GameMode.SPECTATOR);

                if (game.getArena().getSpectatorSpawn() != null) {
                    event.getPlayer().teleport(game.getArena().getSpectatorSpawn());
                }

                break;
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

        if (!gameManager.hasGame()) {
            return;
        }

        event.setDeathMessage(null);

        playerManager.get(event.getEntity()).ifPresent(gameService::eliminate);

        if (event.getEntity().getKiller() != null) {
            playerManager.get(event.getEntity().getKiller())
                    .ifPresent(MeetupPlayer::addKill);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {

        if (!gameManager.hasGame()) {
            return;
        }

        Game game = gameManager.getGame();

        if (gameManager.isState(GameState.PLAYING)
                || gameManager.isState(GameState.DEATHMATCH)
                || gameManager.isState(GameState.ENDING)) {

            event.getPlayer().setGameMode(GameMode.SPECTATOR);

            Location spectatorSpawn = game.getArena().getSpectatorSpawn();

            if (spectatorSpawn != null) {
                event.setRespawnLocation(spectatorSpawn);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        playerManager.get(event.getPlayer()).ifPresent(player -> {

            if (gameManager.hasGame()) {

                Game game = gameManager.getGame();

                if (player.isAlive()) {
                    gameService.eliminate(player);
                } else {
                    game.removePlayer(player);
                    game.removeSpectator(player);
                }
            }

            playerManager.remove(event.getPlayer().getUniqueId());
        });
    }
}