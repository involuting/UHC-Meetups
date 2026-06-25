package me.involuting.meetups.listener;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.game.manager.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

@RequiredArgsConstructor
public class BlockListener implements Listener {
    private final Meetups plugin;

    private final GameManager gameManager;


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){

        if (!gameManager.isRunning()){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){

        if (!gameManager.isRunning()){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event){

        if (!gameManager.isRunning()){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event){
        if (!gameManager.isRunning()){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void  onBlockIgnite(BlockIgniteEvent event){

        if (!gameManager.isRunning()){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event){
        event.setCancelled(true);
    }



}
