package me.involuting.meetups.scoreboard;

import me.involuting.meetups.Meetups;
import me.involuting.meetups.game.Game;
import me.involuting.meetups.game.state.GameState;
import me.involuting.meetups.player.MeetupPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardAdapter {

    private static final String FOOTER = "§7example.net";
    public final static String MENU_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------";

    private final Meetups plugin;

    public ScoreboardAdapter(Meetups plugin) {
        this.plugin = plugin;
    }

    public String getTitle(Player player) {
        return "§6§lMEETUPS";
    }

    public List<String> getLines(Player player) {

        List<String> lines = new ArrayList<>();

        MeetupPlayer meetupPlayer = plugin.getPlayerManager().get(player);
        Game game = plugin.getGameManager().getCurrentGame();

        if (game == null) {
            lines.add(MENU_BAR);
            lines.add("§fOnline: §e" + Bukkit.getOnlinePlayers().size());
            lines.add("§fQueue: §e" + plugin.getQueueManager().getQueueSize());
            lines.add("§fCoins: §60");
            lines.add("");
            lines.add(FOOTER);
            lines.add(MENU_BAR);
            return lines;
        }

        if (meetupPlayer != null && game.isSpectator(meetupPlayer)) {
            lines.add(MENU_BAR);
            lines.add("§cSpectating");
            lines.add("");
            lines.add("§fAlive: §a" + game.getAlivePlayers());
            lines.add("§fBorder: §c" + plugin.getBorderManager().getCurrentSize(game));
            lines.add("");
            lines.add(FOOTER);
            lines.add(MENU_BAR);
            return lines;
        }

        switch (game.getGameState()) {

            case WAITING:
                lines.add(MENU_BAR);
                lines.add("§fPlayers: §e" + game.getPlayers().size() + "/100");
                lines.add("§fNeeded: §c" + Math.max(0, 2 - game.getPlayers().size()));
                lines.add("");
                lines.add(FOOTER);
                lines.add(MENU_BAR);
                break;

            case STARTING:
                lines.add(MENU_BAR);
                lines.add("§fPlayers: §e" + game.getPlayers().size());
                lines.add("§fStarting: §a" + game.getCountdown() + "s");
                lines.add("");
                lines.add(FOOTER);
                lines.add(MENU_BAR);
                break;

            case SCATTERING:
                lines.add(MENU_BAR);
                lines.add("§fPlayers: §e" + game.getPlayers().size());
                lines.add("§fStatus: §bScattering");
                lines.add("");
                lines.add(MENU_BAR);
                break;

            case PLAYING:
                lines.add(MENU_BAR);
                lines.add("§fAlive: §a" + game.getAlivePlayers());
                lines.add("§fKills: §c" + meetupPlayer.getKills());
                lines.add("§fBorder: §e" + plugin.getBorderManager().getCurrentSize(game));
                lines.add("§fTime: §e" + game.getCurrentTimeSeconds());
                lines.add("§fCombat: " + (meetupPlayer.isCombatTagged() ? "§cYes" : "§aNo"));
                lines.add("");
                lines.add(FOOTER);
                lines.add(MENU_BAR);
                break;

            case DEATHMATCH:
                lines.add(MENU_BAR);
                lines.add("§4Deathmatch");
                lines.add("");
                lines.add("§fAlive: §a" + game.getAlivePlayers());
                lines.add("§fKills: §c" + meetupPlayer.getKills());
                lines.add("");
                lines.add(FOOTER);
                lines.add(MENU_BAR);
                break;

            case ENDING:
                lines.add(MENU_BAR);
                lines.add("§6Game Finished");
                lines.add("");
                lines.add("§fReturning...");
                lines.add("");
                lines.add(FOOTER);
                lines.add(MENU_BAR);
                break;
        }

        return lines;
    }
}