package me.involuting.meetups.border;

import lombok.Getter;
import me.involuting.meetups.game.Game;
import org.bukkit.Location;
import org.bukkit.WorldBorder;

@Getter
public class BorderManager {

    private final Game game;
    private final WorldBorder border;

    public BorderManager(Game game) {
        this.game = game;
        this.border = game.getArena().getWorld().getWorldBorder();
    }



    public void setup() {

        border.setCenter(game.getArena().getBorderCenter());
        border.setSize(game.getArena().getStartingBorderSize());

        border.setWarningDistance(5);
        border.setWarningTime(15);

        border.setDamageAmount(1.0D);
        border.setDamageBuffer(0.0D);
    }



    public void shrink() {

        border.setSize(
                game.getArena().getEndingBorderSize(),
                game.getArena().getBorderShrinkTime()
        );
    }

    public void shrink(double size, long seconds) {
        border.setSize(size, seconds);
    }

    public void deathmatch() {

        border.setSize(
                game.getArena().getDeathmatchBorderSize(),
                game.getArena().getDeathmatchShrinkTime()
        );
    }

    public void shrinkTo(double size, int seconds) {

        border.setSize(size, seconds);

        game.getPlayers().forEach(p ->
                p.getPlayer().sendMessage("§cBorder shrinking to §e" + size)
        );
    }



    public void setCenter(Location location) {
        border.setCenter(location);
    }

    public Location getCenter() {
        return border.getCenter();
    }

    public double getSize() {
        return border.getSize();
    }

    public boolean isShrinking() {
        return border.getSize() > game.getArena().getEndingBorderSize();
    }

    public void reset() {
        setup();
    }
}