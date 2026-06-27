package me.involuting.meetups.queue;

import me.involuting.meetups.Meetups;
import me.involuting.meetups.arena.Arena;
import me.involuting.meetups.game.service.GameService;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class QueueManager {

    private final Set<UUID> queue = new HashSet<>();

    private final Meetups plugin;
    private final GameService gameService;

    private boolean starting = false;

    public QueueManager(Meetups plugin, GameService gameService) {
        this.plugin = plugin;
        this.gameService = gameService;
    }

    public void join(Player player) {

        if (queue.contains(player.getUniqueId())) return;

        queue.add(player.getUniqueId());

        player.sendMessage("§aYou have joined the Meetups Queue");

        checkStart();
    }

    public void leave(Player player) {

        queue.remove(player.getUniqueId());

        player.sendMessage("§cYou left the queue");

        checkCancel();
    }

    private void checkStart() {

        if (starting) return;

        if (queue.size() < 2) return;

        Optional<Arena> optionalArena = plugin.getArenaManager().getFirstArena();

        Arena arena = optionalArena.orElse(null);

        if (arena == null) {
            Bukkit.broadcastMessage("§cNo arena available. Cannot start game.");
            return;
        }

        try {
            starting = true;

            gameService.startFromQueue(
                    arena,
                    new HashSet<>(queue)
            );

        } catch (Exception e) {
            starting = false;
            Bukkit.getLogger().severe("Failed to start game from queue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkCancel() {

        if (queue.size() >= 2) return;

        starting = false;
    }

    public void clearQueue() {
        queue.clear();
        starting = false;
    }

    public Set<UUID> getQueue() {
        return queue;
    }

    public void giveLobbyItems(Player player) {

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20f);

        player.setFireTicks(0);
        player.setExp(0f);
        player.setLevel(0);

        player.setGameMode(GameMode.ADVENTURE);

        player.getInventory().setItem(0, createItem(
                Material.NETHER_STAR,
                "§aJoin Queue",
                "§7Click to join a match"
        ));

        player.getInventory().setItem(4, createItem(
                Material.COMPASS,
                "§eArena Selector",
                "§7Choose a map"
        ));

        player.getInventory().setItem(8, createItem(
                Material.BARRIER,
                "§cLeave Queue",
                "§7Click to leave queue"
        ));

        player.updateInventory();
    }

    private ItemStack createItem(Material material, String name, String lore) {

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(Collections.singletonList(lore));

        item.setItemMeta(meta);

        return item;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int getQueueSize() {
        return queue.size();
    }



    public int getPlayersNeeded(int minimumPlayers) {
        return Math.max(0, minimumPlayers - queue.size());
    }

    public boolean isQueued(UUID player) {
        return player != null && queue.contains(player);
    }
}