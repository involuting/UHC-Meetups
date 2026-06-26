package me.involuting.meetups.deathmatch;

import lombok.Getter;
import me.involuting.meetups.Meetups;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.service.GameService;
import me.involuting.meetups.game.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class DeathmatchTask extends BukkitRunnable {

    private final Meetups plugin;
    private final Game game;
    private final GameService gameService;

    private int seconds;

    public DeathmatchTask(Meetups plugin, Game game, GameService gameService, int seconds) {
        this.plugin = plugin;
        this.game = game;
        this.gameService = gameService;
        this.seconds = seconds;
    }

    @Override
    public void run() {

        if (game.getGameState() != GameState.PLAYING) {
            cancel();
            return;
        }

        if (seconds <= 0) {
            cancel();
            gameService.startDeathmatch(game);
            return;
        }

        if (seconds == 60
                || seconds == 30
                || seconds == 15
                || seconds == 10
                || seconds <= 5) {

            Bukkit.broadcastMessage(ChatColor.RED +
                    "Deathmatch starts in " +
                    ChatColor.YELLOW + seconds +
                    ChatColor.RED + " seconds!");

            Bukkit.getOnlinePlayers().forEach(player ->
                    player.playSound(
                            player.getLocation(),
                            Sound.BLOCK_NOTE_BLOCK_PLING,
                            1F,
                            1F
                    ));
        }

        seconds--;
    }

    public void start() {
        runTaskTimer(plugin, 20L, 20L);
    }
}