package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.icoop.actor.ICoopItem;
import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.play.engine.actor.Graphics;
import ch.epfl.cs107.play.engine.actor.ImageGraphics;
import ch.epfl.cs107.play.io.ResourcePath;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

/**
 * A GUI that shows information about the player on the screen.
 */
public class ICoopPlayerStatusGUI implements Graphics {

    private final static int DEPTH = 2000;
    private final ICoopPlayer player;
    private final boolean flipped;

    public ICoopPlayerStatusGUI(ICoopPlayer player, boolean flipped) {
        this.player = player;
        this.flipped = flipped;
    }

    @Override
    public void draw(Canvas canvas) {
        float width = canvas.getTransform().getX().getX();
        float height = canvas.getTransform().getY().getY();

        float ratio = canvas.getWidth() / (float) canvas.getHeight();
        if (ratio > 1)
            height = width / ratio;
        else
            width = height * ratio;

        Vector anchor = canvas.getTransform().getOrigin().sub(new Vector(flipped ? (-width / 2 + 2) : width / 2, height / 2));

        Vector gearAnchor = anchor.add(new Vector(0, height - 1.75f));
        ImageGraphics gearDisplay = new ImageGraphics(ResourcePath.getSprite("icoop/gearDisplay"), 1.5f, 1.5f,
                new RegionOfInterest(0, 0, 32, 32), gearAnchor, 1, DEPTH);
        gearDisplay.draw(canvas);

        ICoopItem item = player.getCurrentItem();
        if (item != null) {
            Vector iconAnchor = gearAnchor.add(new Vector(0.25f, 0.25f));
            ImageGraphics icon = new ImageGraphics(ResourcePath.getSprite(item.getIconName()), 1f, 1f,
                    new RegionOfInterest(0, 0, item.getIconW(), item.getIconH()), iconAnchor, 1, DEPTH + 1);
            icon.draw(canvas);
        }
    }
}
