package me.involuting.meetups.scoreboard;

import fr.mrmicky.fastboard.FastBoard;
import me.involuting.meetups.Meetups;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager {

    private final Meetups plugin;
    private final ScoreboardAdapter adapter;

    private final Map<Player, FastBoard> boards = new HashMap<>();

    public ScoreboardManager(Meetups plugin) {
        this.plugin = plugin;
        this.adapter = new ScoreboardAdapter(plugin);
    }

    public void create(Player player) {
        FastBoard board = new FastBoard(player);
        boards.put(player, board);

        update(player);
    }

    public void remove(Player player) {
        FastBoard board = boards.remove(player);

        if (board != null) {
            board.delete();
        }
    }

    public void update(Player player) {
        FastBoard board = boards.get(player);

        if (board == null) {
            return;
        }

        board.updateTitle(adapter.getTitle(player));
        board.updateLines(adapter.getLines(player));
    }

    public void start() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                update(player);
            }
        }, 20L, 20L);
    }

    public void updateAll() {
        for (Player player : boards.keySet()) {
            update(player);
        }
    }
}