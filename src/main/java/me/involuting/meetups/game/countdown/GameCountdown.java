package me.involuting.meetups.game.countdown;

import lombok.Getter;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class GameCountdown extends BukkitRunnable {

    private final Meetups plugin;
    private final Game game;

    private int seconds;

    public GameCountdown(Meetups plugin, Game game, int seconds) {
        this.plugin = plugin;
        this.game = game;
        this.seconds = seconds;

    }

    @Override
    public void run() {

        if (game.getPlayers().size() < game.getArena().getMinPlayers()){
            Bukkit.broadcastMessage("&l&cNot enough players to start yet.");
            game.setGameState(GameState.WAITING);

            cancel();
            return;
        }

        if (seconds == 0){
            Bukkit.broadcastMessage("&aThe game has started");
            game.setGameState(GameState.SCATTERING);

            cancel();
            return;
        }

        if (seconds <= 5 || seconds == 10 || seconds == 15 || seconds == 30){
            Bukkit.broadcastMessage("&eUHC Meetups will start in &7" + seconds + "&eseconds");

            Bukkit.getOnlinePlayers().forEach(player -> {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
            });

            game.setCountdown(seconds);

            seconds--;
        }


    }
    public void start(){
        game.setGameState(GameState.STARTING);

        runTaskTimer(plugin, 20L, 20L);
    }

    public abstract void onFinish();
}
