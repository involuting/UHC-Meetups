package me.involuting.meetups.arena;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ArenaManager {

    private final Map<String, Arena> arenas = new HashMap<>();

    public void register(Arena arena){
        arenas.put(arena.getName().toLowerCase(), arena);
    }

    public void unregister(String name){
        arenas.remove(name.toLowerCase());
    }

    public Optional<Arena> getArena(String name){
        return Optional.of(arenas.get(name.toLowerCase()));
    }



    public Collection<Arena> getArenas(){
        return arenas.values();
    }

    public boolean exists(String name){
        return arenas.containsKey(name.toLowerCase());
    }

    public void clear(){
        arenas.clear();
    }

    public int size(){
        return arenas.size();
    }

    public boolean isEmpty(){
        return arenas.isEmpty();
    }

    public Optional<Arena> getFirstArena() {
        return arenas.values().stream().findFirst();
    }


}
