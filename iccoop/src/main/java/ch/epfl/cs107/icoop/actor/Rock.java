package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public final class Rock extends Obstacle {

    private boolean destroyed;

    public Rock(Area owner, Orientation orientation, DiscreteCoordinates position) {
        super(owner, orientation, position, "rock.1");
    }

    public void destroy() {
        destroyed = true;
    }

    @Override
    public boolean takeCellSpace() {
        return !destroyed;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!destroyed) super.draw(canvas);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}
