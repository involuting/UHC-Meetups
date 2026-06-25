package me.involuting.meetups.game;

import lombok.Getter;
import lombok.Setter;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.game.state.GameState;
import me.involuting.meetups.player.MeetupPlayer;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
public class Game {

    private final Arena arena;

    private GameState gameState = GameState.WAITING;

    private final Set<MeetupPlayer> players = new HashSet<>();
    private final Set<MeetupPlayer> spectators = new HashSet<>();

    private int countdown;

    private int alivePlayers;

    public Game(Arena arena) {
        this.arena = arena;
    }

    public void addPlayer(MeetupPlayer player){
        players.add(player);
        alivePlayers++;
    }

    public void removePlayer(MeetupPlayer player){
        if (players.remove(player)){
            alivePlayers--;
        }
    }

    public void addSpectator(MeetupPlayer player){
        spectators.add(player);
    }

    public boolean isPlaying(){
        return gameState == GameState.PLAYING | gameState == GameState.DEATHMATCH;
    }

    public void reset(){


        getPlayers().clear();
        getSpectators().clear();

        setAlivePlayers(0);
        setCountdown(0);
        setGameState(GameState.WAITING);
    }


}
