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

        if (game != null) {
            throw new IllegalStateException("A game is already running.");
        }

        if (arena == null) {
            throw new IllegalArgumentException("Arena cannot be null.");
        }

        game = new Game(arena);
        game.setGameState(GameState.WAITING);

        return game;
    }

    public void destroy() {

        ensureGame();

        game.reset();
        game = null;
    }



    public boolean hasGame() {
        return game != null;
    }

    public boolean isRunning() {
        return game != null && game.isPlaying();
    }

    public boolean isState(GameState state) {
        return game != null && game.getGameState() == state;
    }


    public Arena getArena() {
        ensureGame();
        return game.getArena();
    }

    public Game getGame() {
        ensureGame();
        return game;
    }



    public void addPlayer(MeetupPlayer player) {
        ensureGame();
        if (player == null) return;

        game.addPlayer(player);
    }

    public void removePlayer(MeetupPlayer player) {
        ensureGame();
        if (player == null) return;

        game.removePlayer(player);
    }

    public void addSpectator(MeetupPlayer player) {
        ensureGame();
        if (player == null) return;

        game.addSpectator(player);
    }

    public void removeSpectator(MeetupPlayer player) {
        ensureGame();
        if (player == null) return;

        game.removeSpectator(player);
    }



    public int getAlivePlayers() {
        ensureGame();
        return game.getAlivePlayers();
    }

    public int getPlayerCount() {
        ensureGame();
        return game.getPlayers().size();
    }



    private void ensureGame() {
        if (game == null) {
            throw new IllegalStateException("No active game.");
        }
    }
}