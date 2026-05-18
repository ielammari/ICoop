package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Arrays;
import java.util.List;

public class Door extends AreaEntity {

    private final Logic signal;
    private final String destinationArea;
    private final DiscreteCoordinates redDestination;
    private final DiscreteCoordinates blueDestination;
    private final List<DiscreteCoordinates> cells;

    public Door(Area owner, Orientation orientation, Logic signal,
                String destinationArea,
                DiscreteCoordinates redDestination,
                DiscreteCoordinates blueDestination,
                DiscreteCoordinates... cells) {
        super(owner, orientation, cells[0]);
        this.signal = signal;
        this.destinationArea = destinationArea;
        this.redDestination = redDestination;
        this.blueDestination = blueDestination;
        this.cells = Arrays.asList(cells);
    }

    public Logic getSignal() {
        return signal;
    }

    public String getDestinationArea() {
        return destinationArea;
    }

    public DiscreteCoordinates getDestinationFor(Element element) {
        return (element == Element.FIRE) ? redDestination : blueDestination;
    }

    @Override
    public void draw(Canvas canvas) {
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return cells;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}
