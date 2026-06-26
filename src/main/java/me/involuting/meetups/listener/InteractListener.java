package me.involuting.meetups.listener;

import me.involuting.meetups.queue.QueueManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InteractListener implements Listener {

    private final QueueManager queueManager;

    public InteractListener(QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getItem() == null) return;

        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();

        if (meta == null || !meta.hasDisplayName()) return;

        Player player = event.getPlayer();
        String name = meta.getDisplayName();

        event.setCancelled(true);


        if (item.getType() == Material.NETHER_STAR
                && name.equals("§aJoin Queue")) {

            queueManager.join(player);
            return;
        }


        if (item.getType() == Material.BARRIER
                && name.equals("§cLeave Queue")) {

            queueManager.leave(player);
            return;
        }


        if (item.getType() == Material.COMPASS
                && name.equals("§eArena Selector")) {

            player.sendMessage("§eComing Soon.");
        }
    }
}