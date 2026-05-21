package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;

public abstract class ElementalItem extends ICoopCollectable implements ElementalEntity, Logic {

    private final Element element;

    public ElementalItem(Area owner, Orientation orientation, DiscreteCoordinates position, Element element) {
        super(owner, orientation, position);
        this.element = element;
    }

    @Override
    public Element element() { return element; }

    @Override
    public boolean isOn() { return isCollected(); }

    @Override
    public boolean isOff() { return !isCollected(); }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}
