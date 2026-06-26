package me.involuting.meetups.game;

import lombok.Getter;
import lombok.Setter;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.game.state.GameState;
import me.involuting.meetups.player.MeetupPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class Game {

    private final Arena arena;

    private GameState gameState = GameState.WAITING;

    private final Set<MeetupPlayer> players = new HashSet<>();
    private final Set<MeetupPlayer> spectators = new HashSet<>();

    private int countdown;

    public Game(Arena arena) {
        this.arena = arena;
    }



    public void addPlayer(MeetupPlayer player) {
        if (player == null) return;

        spectators.remove(player);
        players.add(player);
    }

    public void removePlayer(MeetupPlayer player) {
        if (player == null) return;

        players.remove(player);
    }

    public boolean isPlayerAlive(MeetupPlayer player) {
        return players.contains(player);
    }



    public void addSpectator(MeetupPlayer player) {
        if (player == null) return;

        players.remove(player);
        spectators.add(player);
    }

    public void removeSpectator(MeetupPlayer player) {
        if (player == null) return;

        spectators.remove(player);
    }



    public boolean isPlaying() {
        return gameState == GameState.PLAYING
                || gameState == GameState.DEATHMATCH;
    }



    public int getAlivePlayers() {
        return players.size();
    }

    public int getTotalPlayers() {
        return players.size() + spectators.size();
    }



    public void reset() {
        players.clear();
        spectators.clear();
        countdown = 0;
        gameState = GameState.WAITING;
    }

    public MeetupPlayer getMeetupPlayer(UUID uniqueId) {
        return players.stream()
                .filter(p -> p.getPlayer().getUniqueId().equals(uniqueId))
                .findFirst()
                .orElse(null);
    }
}