package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public abstract class ElementalWall extends AreaEntity implements ElementalEntity, Interactor {

    private final Logic signal;
    private final Sprite sprite;
    private final ElementalWallInteractionHandler handler;

    protected ElementalWall(Area owner, Orientation orientation, DiscreteCoordinates position,
                            Logic signal, String spriteName) {
        super(owner, orientation, position);
        this.signal = signal;
        boolean isVertical = (orientation == Orientation.LEFT || orientation == Orientation.RIGHT);
        RegionOfInterest roi = isVertical ? new RegionOfInterest(256, 0, 256, 256)
                : new RegionOfInterest(0, 0, 256, 256);
        this.sprite = new Sprite(spriteName, 1f, 1f, this, roi, Vector.ZERO);
        this.handler = new ElementalWallInteractionHandler();
    }

    public Logic getSignal() { return signal; }

    protected abstract DamageType damageType();

    @Override
    public void update(float deltaTime) { super.update(deltaTime); }

    @Override
    public void draw(Canvas canvas) {
        if (signal.isOn()) sprite.draw(canvas);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() { return Collections.emptyList(); }

    @Override
    public boolean wantsCellInteraction() { return true; }

    @Override
    public boolean wantsViewInteraction() { return false; }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
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

    private class ElementalWallInteractionHandler implements ICoopInteractionVisitor {
        @Override
        public void interactWith(ICoopPlayer player, boolean isCellInteraction) {
            if (signal.isOn()) player.takeDamage(damageType(), 1);
        }
    }
}
