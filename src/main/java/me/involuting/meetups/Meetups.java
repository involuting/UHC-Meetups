package me.involuting.meetups;

import lombok.Getter;
import lombok.Setter;
import me.involuting.meetups.arena.ArenaManager;
import me.involuting.meetups.border.BorderManager;
import me.involuting.meetups.game.manager.GameManager;
import me.involuting.meetups.player.PlayerManager;
import me.involuting.meetups.scatter.ScatterManager;
import org.bukkit.plugin.java.JavaPlugin;
@Getter @Setter
public final class Meetups extends JavaPlugin {
    private ScatterManager scatterManager;
    private PlayerManager playerManager;
    private GameManager gameManager;
    private BorderManager borderManager;
    private ArenaManager arenaManager;


    @Override
    public void onEnable() {
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
