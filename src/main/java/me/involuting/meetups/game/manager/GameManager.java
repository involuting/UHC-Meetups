package me.involuting.meetups.game.manager;

import lombok.Getter;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.state.GameState;
import me.involuting.meetups.player.MeetupPlayer;

@Getter
public class GameManager {

    private Game game;

    public Game create(Arena arena) {

        if (hasGame()) {
            throw new IllegalStateException("A game is already active.");
        }

        game = new Game(arena);
        game.setGameState(GameState.WAITING);

        return game;
    }

    public void destroy() {

        checkGame();

        game.reset();
        game = null;
    }


    public boolean hasGame() {
        return game != null;
    }

    public boolean isRunning() {
        return hasGame() && game.isPlaying();
    }

    public boolean isState(GameState state) {
        return hasGame() && game.getGameState() == state;
    }

    public Arena getArena() {
        checkGame();
        return game.getArena();
    }

    public int getAlivePlayers() {
        checkGame();
        return game.getAlivePlayers();
    }

    public int getPlayerCount() {
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

    public void removeSpectator(MeetupPlayer player) {
        checkGame();
        game.removeSpectator(player);
    }

    private void checkGame() {

        if (game == null) {
            throw new IllegalStateException("No active game.");
        }
    }
}