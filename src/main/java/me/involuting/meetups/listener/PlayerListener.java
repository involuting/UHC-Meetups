package me.involuting.meetups.listener;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.manager.GameManager;
import me.involuting.meetups.game.service.GameService;
import me.involuting.meetups.game.state.GameState;
import me.involuting.meetups.player.MeetupPlayer;
import me.involuting.meetups.player.PlayerManager;
import me.involuting.meetups.queue.QueueManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;


public class PlayerListener implements Listener {

    private final PlayerManager playerManager;
    private final GameManager gameManager;
    private final GameService gameService;
    private final QueueManager queueManager;

    public PlayerListener(PlayerManager playerManager, GameManager gameManager, GameService gameService, QueueManager queueManager) {
        this.playerManager = playerManager;
        this.gameManager = gameManager;
        this.gameService = gameService;
        this.queueManager = queueManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        MeetupPlayer mp = playerManager.getOrCreate(player);

        player.setGameMode(GameMode.ADVENTURE);

        if (!gameManager.hasGame()) {
            queueManager.giveLobbyItems(player);
            return;
        }

        Game game = gameManager.getGame();

        switch (game.getGameState()) {

            case WAITING:
            case STARTING:

                player.sendMessage("§eA game is starting soon! Use §6/meetups join §eto play.");
                queueManager.giveLobbyItems(player);
                break;


            default:

                mp.setAlive(false);
                mp.setSpectating(true);

                game.addSpectator(mp);

                player.setGameMode(GameMode.SPECTATOR);

                Location spawn = game.getArena().getSpectatorSpawn();
                if (spawn != null) player.teleport(spawn);

                player.sendMessage("§cYou joined as a spectator.");
                break;
        }

        if (gameManager.isRunning()){
            player.getInventory().clear();
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

        if (!gameManager.hasGame()) return;

        Game game = gameManager.getGame();

        if (game.getGameState() == GameState.PLAYING
                || game.getGameState() == GameState.DEATHMATCH) {

            Location spawn = game.getArena().getSpectatorSpawn();

            if (spawn != null) {
                event.setRespawnLocation(spawn);
            }

            event.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        playerManager.get(player).ifPresent(mp -> {

            if (gameManager.hasGame()) {

                Game game = gameManager.getGame();

                if (mp.isAlive()) {
                    gameService.eliminate(mp);
                } else {
                    game.removeSpectator(mp);
                    game.removePlayer(mp);
                }
            }

            playerManager.remove(player.getUniqueId());
            queueManager.leave(player); // IMPORTANT FIX
        });
    }
}