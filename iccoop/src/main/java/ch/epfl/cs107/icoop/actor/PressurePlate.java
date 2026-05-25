package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public final class PressurePlate extends AreaEntity implements Logic {

    private final Sprite sprite;
    private int activeTimer;

    public PressurePlate(Area owner, Orientation orientation, DiscreteCoordinates position) {
        super(owner, orientation, position);
        this.sprite = new Sprite("GroundPlateOff", 1f, 1f, this);
        this.activeTimer = 0;
    }

    public void activate() { activeTimer = 2; }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (activeTimer > 0) activeTimer--;
    }

    @Override
    public boolean isOn() { return activeTimer > 0; }

    @Override
    public boolean isOff() { return !isOn(); }

    @Override
    public void draw(Canvas canvas) { sprite.draw(canvas); }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean takeCellSpace() { return false; }

    @Override
    public boolean isCellInteractable() { return true; }

    @Override
    public boolean isViewInteractable() { return false; }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}
