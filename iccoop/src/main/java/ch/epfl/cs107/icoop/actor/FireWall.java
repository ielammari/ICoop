package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;

public final class FireWall extends ElementalWall {

    public FireWall(Area owner, Orientation orientation, DiscreteCoordinates position, Logic signal) {
        super(owner, orientation, position, signal, "fire_wall");
    }

    @Override
    public Element element() { return Element.FIRE; }

    @Override
    protected DamageType damageType() { return DamageType.FIRE; }
}
