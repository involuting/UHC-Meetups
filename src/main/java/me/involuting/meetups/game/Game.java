package me.involuting.meetups.game;

import lombok.Getter;
import lombok.Setter;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.game.state.GameState;
import me.involuting.meetups.player.MeetupPlayer;

import java.util.Collections;
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

    private long startTime;

    public Game(Arena arena) {
        this.arena = arena;
    }

    public boolean addPlayer(MeetupPlayer player) {

        if (player == null) {
            return false;
        }

        spectators.remove(player);
        return players.add(player);
    }

    public boolean removePlayer(MeetupPlayer player) {

        if (player == null) {
            return false;
        }

        return players.remove(player);
    }

    public boolean addSpectator(MeetupPlayer player) {

        if (player == null) {
            return false;
        }

        players.remove(player);
        return spectators.add(player);
    }

    public boolean removeSpectator(MeetupPlayer player) {

        if (player == null) {
            return false;
        }

        return spectators.remove(player);
    }

    public boolean isPlayer(MeetupPlayer player) {
        return player != null && players.contains(player);
    }

    public boolean isSpectator(MeetupPlayer player) {
        return player != null && spectators.contains(player);
    }

    public MeetupPlayer getPlayer(UUID uniqueId) {

        if (uniqueId == null) {
            return null;
        }

        for (MeetupPlayer player : players) {
            if (player.getPlayer() != null &&
                    player.getPlayer().getUniqueId().equals(uniqueId)) {
                return player;
            }
        }

        for (MeetupPlayer spectator : spectators) {
            if (spectator.getPlayer() != null &&
                    spectator.getPlayer().getUniqueId().equals(uniqueId)) {
                return spectator;
            }
        }

        return null;
    }

    public int getAlivePlayers() {
        return (int) players.stream()
                .filter(MeetupPlayer::isAlive)
                .count();
    }

    public int getTotalPlayers() {
        return players.size() + spectators.size();
    }

    public boolean isPlaying() {
        return gameState == GameState.PLAYING
                || gameState == GameState.DEATHMATCH;
    }

    public void reset() {
        players.clear();
        spectators.clear();
        countdown = 0;
        gameState = GameState.WAITING;
    }

    public long getCurrentTimeSeconds() {
        if (startTime == 0) {
            return 0;
        }

        return (System.currentTimeMillis() - startTime) / 1000;
    }

    public Set<MeetupPlayer> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    public Set<MeetupPlayer> getSpectators() {
        return Collections.unmodifiableSet(spectators);
    }
}