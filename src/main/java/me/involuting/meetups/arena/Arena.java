package me.involuting.meetups.arena;

import lombok.Getter;
import lombok.Setter;
import me.involuting.meetups.arena.state.ArenaState;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.EnumSet;

@Getter
@Setter
public class Arena {

    private final String name;
    private World world;

    private  Location lobbySpawn;
    private Location spectatorSpawn;

    private Location borderCenter;
    private double startingBorderSize;
    private double endingBorderSize = 50;
    private double deathmatchBorderSize;
    private int deathmatchShrinkTime = 30;

    private int warningDistance = 5;
    private int warningTime = 15;

    private double damageAmount = 1.0D;
    private double damageBuffer = 0.0D;

    private final EnumSet<ScenarioType> scenarios = EnumSet.noneOf(ScenarioType.class);

    private ArenaState state = ArenaState.SETUP;

    private int minPlayers;
    private int maxPlayers;

    private int countdown = 30;
    private int gracePeriod = 0;
    private int borderDelay = 300;
    private int borderShrinkTime = 900;

    private boolean allowNether = false;
    private boolean allowEnd = false;
    private boolean allowSpectators = true;

    private boolean cutClean = true;
    private boolean goldenHeads = true;
    private boolean naturalRegeneration = false;
    private boolean deathmatchEnabled = true;



    public Arena(String name) {
        this.name = name;

        this.minPlayers = 2;
        this.maxPlayers = 100;


        this.startingBorderSize = 1000;
        this.endingBorderSize = 100;
        this.deathmatchBorderSize = 25;

        this.borderShrinkTime = 600;

    }

    public boolean isReady() {
        return world != null
                && lobbySpawn != null
                && spectatorSpawn != null
                && borderCenter != null;
    }

    public boolean hasLobby() {
        return lobbySpawn != null;
    }

    public boolean hasSpectatorSpawn() {
        return spectatorSpawn != null;
    }

    public boolean hasBorderCenter() {
        return borderCenter != null;
    }
}
