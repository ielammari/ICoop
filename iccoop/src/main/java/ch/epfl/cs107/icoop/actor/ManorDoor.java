package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.DialogHandler;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class ManorDoor extends AreaEntity {

    private final Logic signal;
    private final DialogHandler dialogHandler;
    private boolean contactNow;
    private boolean contactPrev;

    public ManorDoor(Area owner, Orientation orientation, DiscreteCoordinates position,
                     Logic signal, DialogHandler dialogHandler) {
        super(owner, orientation, position);
        this.signal = signal;
        this.dialogHandler = dialogHandler;
    }

    public void onContact() {
        contactNow = true;
        if (!contactPrev) {
            dialogHandler.publish(new Dialog(signal.isOn() ? "victory" : "key_required"));
        }
    }

    @Override
    public void update(float deltaTime) {
        contactPrev = contactNow;
        contactNow = false;
        super.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
    }

    @Override
    public boolean takeCellSpace() { return false; }

    @Override
    public boolean isCellInteractable() { return true; }

    @Override
    public boolean isViewInteractable() { return false; }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}
