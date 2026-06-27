package me.involuting.meetups.listener;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.manager.GameManager;
import me.involuting.meetups.game.service.GameService;
import me.involuting.meetups.game.state.GameState;
import me.involuting.meetups.player.MeetupPlayer;
import me.involuting.meetups.player.PlayerManager;
import me.involuting.meetups.queue.QueueManager;
import me.involuting.meetups.scoreboard.ScoreboardManager;
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
    private final ScoreboardManager scoreboardManager;

    public PlayerListener(PlayerManager playerManager, GameManager gameManager, GameService gameService, QueueManager queueManager, ScoreboardManager scoreboardManager) {
        this.playerManager = playerManager;
        this.gameManager = gameManager;
        this.gameService = gameService;
        this.queueManager = queueManager;
        this.scoreboardManager = scoreboardManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        MeetupPlayer mp = playerManager.getOrCreate(player);

        player.setGameMode(GameMode.ADVENTURE);

        event.setJoinMessage(null);


        scoreboardManager.create(player);

        if (!gameManager.hasGame()) {
            queueManager.giveLobbyItems(player);
            return;
        }

        Game game = gameManager.getGame();

        switch (game.getGameState()) {

            case WAITING:
            case STARTING:
                queueManager.giveLobbyItems(player);
                player.sendMessage("§eA game is starting soon! Use §6/meetups join §eto play.");
                break;

            default:

                player.getInventory().clear();

                mp.setAlive(false);
                mp.setSpectating(true);

                game.addSpectator(mp);

                player.setGameMode(GameMode.SPECTATOR);

                Location spawn = game.getArena().getSpectatorSpawn();

                if (spawn != null) {
                    player.teleport(spawn);
                }

                player.sendMessage("§cYou joined as a spectator.");
                break;
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

        if (!gameManager.hasGame()) {
            return;
        }

        event.setDeathMessage(null);

        MeetupPlayer victim = playerManager.get(event.getEntity());

        if (victim != null) {
            gameService.eliminate(victim);
        }

        Player killer = event.getEntity().getKiller();

        if (killer != null) {
            MeetupPlayer killerPlayer = playerManager.get(killer);

            if (killerPlayer != null) {
                killerPlayer.addKill();
            }
        }

        scoreboardManager.updateAll();
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {

        if (!gameManager.hasGame()) return;

        Game game = gameManager.getCurrentGame();

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

        scoreboardManager.remove(player);

        event.setQuitMessage(null);

        MeetupPlayer mp = playerManager.get(player);

        if (mp == null) {
            return;
        }

        if (gameManager.hasGame()) {

            Game game = gameManager.getCurrentGame();

            if (mp.isAlive()) {
                gameService.eliminate(mp);
            } else {
                game.removePlayer(mp);
                game.removeSpectator(mp);
            }
        }

        if (queueManager.isQueued(player.getUniqueId())) {
            queueManager.leave(player);
        }

        playerManager.remove(player.getUniqueId());
    }
}