package me.involuting.meetups.border;

import me.involuting.meetups.game.Game;
import org.bukkit.Location;
import org.bukkit.WorldBorder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BorderManager {

    // cache per game to avoid repeated lookups
    private final Map<UUID, WorldBorder> borders = new HashMap<>();

    private WorldBorder border(Game game) {
        return borders.computeIfAbsent(
                game.getArena().getWorld().getUID(),
                id -> game.getArena().getWorld().getWorldBorder()
        );
    }

    public void setup(Game game) {
        WorldBorder border = border(game);

        border.setCenter(game.getArena().getBorderCenter());
        border.setSize(game.getArena().getStartingBorderSize());

        border.setWarningDistance(game.getArena().getWarningDistance());
        border.setWarningTime(game.getArena().getWarningTime());

        border.setDamageAmount(game.getArena().getDamageAmount());
        border.setDamageBuffer(game.getArena().getDamageBuffer());
    }

    public void shrink(Game game) {
        shrinkTo(game,
                game.getArena().getEndingBorderSize(),
                game.getArena().getBorderShrinkTime());
    }

    public void shrink(Game game, double size, long seconds) {
        border(game).setSize(size, seconds);
    }

    public void deathmatch(Game game) {
        shrinkTo(game,
                game.getArena().getDeathmatchBorderSize(),
                game.getArena().getDeathmatchShrinkTime());
    }

    public void shrinkTo(Game game, double size, long seconds) {
        WorldBorder border = border(game);

        border.setSize(size, seconds);

        game.getPlayers().forEach(player -> {
            var p = player.getPlayer();
            if (p != null) {
                p.sendMessage("§cBorder shrinking to §e" + size);
            }
        });
    }

    public void setCenter(Game game, Location location) {
        border(game).setCenter(location);
    }

    public Location getCenter(Game game) {
        return border(game).getCenter();
    }

    public double getSize(Game game) {
        return border(game).getSize();
    }

    public boolean isShrinking(Game game) {
        return border(game).getSize() > game.getArena().getEndingBorderSize();
    }

    public void reset(Game game) {
        setup(game);
    }

    public void clear(Game game) {
        borders.remove(game.getArena().getWorld().getUID());
    }


}