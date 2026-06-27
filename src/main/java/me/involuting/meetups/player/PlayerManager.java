package me.involuting.meetups.player;

import org.bukkit.entity.Player;

import java.util.*;

public class PlayerManager {

    private final Map<UUID, MeetupPlayer> players = new HashMap<>();

    public MeetupPlayer create(Player player){
        return players.computeIfAbsent(
                player.getUniqueId(), MeetupPlayer::new
        );
    }

    public void remove(UUID uuid){
        players.remove(uuid);
    }

    public MeetupPlayer get(Player player){
        return get(player.getUniqueId());
    }

    public MeetupPlayer get(UUID uuid){
        return players.get(uuid);
    }

    public boolean has(Player player){
        return players.containsKey(player.getUniqueId());
    }

    public Collection<MeetupPlayer> getPlayers(){
        return Collections.unmodifiableCollection(players.values());
    }

    public int size(){
        return players.size();
    }

    public boolean isEmpty(){
        return players.isEmpty();
    }

    public void clear(){
        players.clear();
    }

    public MeetupPlayer getOrCreate(Player player) {
        return players.computeIfAbsent(
                player.getUniqueId(),
                MeetupPlayer::new
        );
    }

    public void resetAll() {
        players.values().forEach(MeetupPlayer::reset);
    }
}
