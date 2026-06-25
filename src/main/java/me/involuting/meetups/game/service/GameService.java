package me.involuting.meetups.game.service;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.border.BorderManager;
import me.involuting.meetups.border.task.BorderTask;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.countdown.GameCountdown;
import me.involuting.meetups.game.manager.GameManager;
import me.involuting.meetups.game.state.GameState;
import me.involuting.meetups.player.MeetupPlayer;
import me.involuting.meetups.scatter.ScatterManager;
import me.involuting.meetups.scatter.task.ScatterTask;
import org.bukkit.Bukkit;

import javax.swing.border.Border;

@RequiredArgsConstructor
public class GameService {

    private final Meetups plugin;

    private final GameManager gameManager;
    private final BorderManager borderManager;
    private final ScatterManager scatterManager;

    private GameCountdown countdown;
    private ScatterTask scatterTask;
    private BorderTask borderTask;

    public void create(Arena arena){
        gameManager.create(arena);
    }

    public void startCountdown(){
        Game game = gameManager.getGame();
        game.setGameState(GameState.STARTING);

        GameCountdown gameCountdown = new GameCountdown(
                plugin, game, game.getArena().getCountdown()
        );

        gameCountdown.start();


    }

    public void scatterPlayers(){

        Game game = gameManager.getGame();

        game.setGameState(GameState.SCATTERING);

        ScatterManager scatterManager = plugin.getScatterManager();

        ScatterTask scatterTask = new ScatterTask(
                plugin, game, scatterManager
        );

        scatterTask.start();


    }

    public void startGame(){
        Game game = gameManager.getGame();


        game.setGameState(GameState.PLAYING);

        Bukkit.broadcastMessage("&aThe game has started");

        startBorder();
    }

    public void startBorder(){
        Game game = gameManager.getGame();

        borderManager.setup();

        BorderTask borderTask =
                new BorderTask(plugin, gameManager.getGame(), borderManager);

        borderTask.start(gameManager.getArena().getBorderDelay());


    }

    public void endGame(){
        Game game = gameManager.getGame();
        game.setGameState(GameState.ENDING);

        resetGame();
    }

    public void resetGame(){
        gameManager.getGame().reset();
    }

    public void eliminate(MeetupPlayer player){

        Game game = gameManager.getGame();

        player.addDeath();

        game.removePlayer(player);

        player.setAlive(false);
        player.setSpectating(true);
        game.addSpectator(player);

        checkWinner();
    }

    private void checkWinner() {
        Game game = gameManager.getGame();

        if (game.getAlivePlayers() > 1){
            return;
        }

        MeetupPlayer winner = game.getPlayers()
                .stream().findFirst().orElse(null);

        if (winner == null){
            return;
        }

        Bukkit.broadcastMessage("&6" +
                winner.getPlayer().getName() + " has won the game");
        stopTasks();
        endGame();
    }

    public void stopTasks() {
        if (countdown != null) countdown.cancel();
        if (scatterTask != null) scatterTask.cancel();
        if (borderTask != null) borderTask.cancel();
    }
}
