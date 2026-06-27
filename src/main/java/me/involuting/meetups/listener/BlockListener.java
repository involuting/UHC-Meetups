package me.involuting.meetups.listener;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.game.manager.GameManager;
import me.involuting.meetups.game.state.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

@RequiredArgsConstructor
public class BlockListener implements Listener {

    private final GameManager gameManager;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!canModifyWorld()) {
            event.setCancelled(true);
        }

        if (canModifyWorld()){
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!canModifyWorld()) {
            event.setCancelled(true);
        }

        if (canModifyWorld()){
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!canModifyWorld()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        if (!canModifyWorld()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (!canModifyWorld()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    private boolean canModifyWorld() {
        return gameManager.hasGame()
                && (
                gameManager.isState(GameState.GRACE_PERIOD)
                        || gameManager.isState(GameState.PLAYING)
                        || gameManager.isState(GameState.DEATHMATCH)
        );
    }
}