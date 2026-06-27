package me.involuting.meetups.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@Setter
public class MeetupPlayer {

    private final UUID uniqueID;

    private int kills;
    private int deaths;

    private boolean alive = true;
    private boolean spectating = false;
    private boolean scattered = false;
    private boolean frozen;
    private boolean disconnected;

    private PlayerState state = PlayerState.LOBBY;

    private UUID lastAttacker;
    private long lastDamageTime;



    private int placement = -1;


    public MeetupPlayer(UUID uniqueID) {
        this.uniqueID = uniqueID;
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(uniqueID);
    }

    public boolean isOnline(){
        return getPlayer() != null;
    }

    public void addKill(){
        kills++;
    }

    public void addDeath(){
        deaths++;
        alive = false;
    }


    public void setLastAttacker(Player player){
        if (player == null){
            lastAttacker = null;
            return;
        }

        lastAttacker = player.getUniqueId();
        lastDamageTime = System.currentTimeMillis();
    }

    public Player getLastAttacker(){
        return lastAttacker == null ? null : Bukkit.getPlayer(lastAttacker);
    }

    public boolean isCombatTagged(){
        return System.currentTimeMillis() - lastDamageTime < 10_000L;
    }

    public void reset() {
        kills = 0;
        deaths = 0;

        alive = true;
        spectating = false;
        scattered = false;
        frozen = false;
        disconnected = false;

        placement = -1;

        lastAttacker = null;
        lastDamageTime = 0L;
    }

    public boolean isPlaying() {
        return state == PlayerState.PLAYING;
    }

    public boolean isLobby() {
        return state == PlayerState.LOBBY;
    }

    public boolean isQueued() {
        return state == PlayerState.QUEUE;
    }

    public boolean isSpectator() {
        return spectating;
    }
}
