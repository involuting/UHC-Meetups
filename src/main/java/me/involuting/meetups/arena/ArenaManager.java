package me.involuting.meetups.arena;

import java.util.*;

public class ArenaManager {

    private final Map<String, Arena> arenas = new HashMap<>();
    private final ArenaStorage arenaStorage;

    public ArenaManager(ArenaStorage arenaStorage) {
        this.arenaStorage = arenaStorage;
    }



    public void register(Arena arena) {
        if (arena == null || arena.getName() == null) return;

        arenas.put(arena.getName().toLowerCase(), arena);
    }

    public void unregister(String name) {
        if (name == null) return;

        arenas.remove(name.toLowerCase());
    }



    public Optional<Arena> getArena(String name) {
        if (name == null) return Optional.empty();

        return Optional.ofNullable(arenas.get(name.toLowerCase()));
    }

    public Collection<Arena> getArenas() {
        return Collections.unmodifiableCollection(arenas.values());
    }

    public boolean exists(String name) {
        return name != null && arenas.containsKey(name.toLowerCase());
    }

    public Optional<Arena> getFirstArena() {
        return arenas.values().stream().findFirst();
    }



    public void clear() {
        arenas.clear();
    }

    public int size() {
        return arenas.size();
    }

    public boolean isEmpty() {
        return arenas.isEmpty();
    }



    public void loadArenas(List<Arena> loaded) {
        arenas.clear();

        if (loaded == null) return;

        for (Arena arena : loaded) {
            if (arena == null || arena.getName() == null) continue;

            arenas.put(arena.getName().toLowerCase(), arena);
        }
    }



    public void save() {
        arenaStorage.save(new ArrayList<>(arenas.values()));
    }


    public Arena getCurrentArena() {
        return getFirstArena().orElse(null);
    }
}