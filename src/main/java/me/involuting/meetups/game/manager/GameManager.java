package me.involuting.meetups.game.manager;

import lombok.Getter;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.state.GameState;
import me.involuting.meetups.player.MeetupPlayer;

@Getter
public class GameManager {

    private Game game;

    public Game create(Arena arena){
        if (game != null){
            throw new IllegalStateException("A game is already active");
        }
        this.game = new Game(arena);

        return game;
    }


    public void start(){
        checkGame();

        if (game.getGameState() != GameState.STARTING) {
            throw new IllegalStateException("Game is not ready to start.");
        }

        game.setGameState(GameState.PLAYING);
    }



    public void stop(){
        checkGame();

        game.setGameState(GameState.ENDING);
    }

    private void checkGame() {
        if (game == null){
            throw new IllegalStateException("No active games");
        }
    }


    public void destroy() {
        checkGame();

        game.reset();
        this.game = null;
    }

    public boolean hasGame(){
        return  game != null;
    }

    public boolean isRunning(){
        return hasGame() && game.isPlaying();
    }

    public boolean isState(GameState state){
        return hasGame() && game.getGameState() == state;
    }

    public void setState(GameState state){
        checkGame();

        game.setGameState(state);
    }

    public Arena getArena(){
        checkGame();
        return game.getArena();
    }

    public int getAlivePlayers(){
        checkGame();
        return game.getAlivePlayers();
    }

    public int getPlayerCount(){
        checkGame();
        return game.getPlayers().size();
    }

    public void addPlayer(MeetupPlayer player) {
        checkGame();
        game.addPlayer(player);
    }

    public void removePlayer(MeetupPlayer player) {
        checkGame();
        game.removePlayer(player);
    }

    public void addSpectator(MeetupPlayer player) {
        checkGame();
        game.addSpectator(player);
    }





}
