package me.involuting.meetups.listener;

import lombok.RequiredArgsConstructor;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.manager.GameManager;
import me.involuting.meetups.game.state.GameState;
import me.involuting.meetups.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

@RequiredArgsConstructor
public class EntityListener implements Listener {

    private final Meetups plugin;

    private final GameManager gameManager;
    private final PlayerManager playerManager;

    @EventHandler
    public void onDamage(EntityDamageEvent event){

        if (!(event.getEntity() instanceof Player)){
            return;
        }

        if (!gameManager.hasGame()){
            return;
        }

        Game game = gameManager.getGame();

        if (game.getGameState() != GameState.PLAYING
        && game.getGameState() != GameState.DEATHMATCH){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event){

        if (!(event.getEntity() instanceof  Player victim)){
            return;
        }

        if (!(event.getEntity() instanceof  Player attacker)){
            return;
        }

        if (!gameManager.isRunning()){
            event.setCancelled(true);
            return;
        }

        playerManager.get(victim).ifPresent(meetupPlayer -> {
            meetupPlayer.setLastAttacker(attacker);
        });




    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event){

        if (!(event.getEntity() instanceof Player)){
            return;
        }

        if (!gameManager.hasGame()){
            return;
        }

        Game game = gameManager.getGame();

        if (game.getGameState() != GameState.PLAYING && game.getGameState() != GameState.DEATHMATCH){
            event.setCancelled(true);
        }
    }





}
