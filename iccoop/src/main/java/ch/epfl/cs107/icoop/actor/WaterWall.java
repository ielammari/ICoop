package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;

public class WaterWall extends ElementalWall {

    public WaterWall(Area owner, Orientation orientation, DiscreteCoordinates position, Logic signal) {
        super(owner, orientation, position, signal, "water_wall");
    }

    @Override
    public Element element() { return Element.WATER; }

    @Override
    protected DamageType damageType() { return DamageType.WATER; }
}
