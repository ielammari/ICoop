package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.actor.Door;
import ch.epfl.cs107.icoop.actor.FireWall;
import ch.epfl.cs107.icoop.actor.Heart;
import ch.epfl.cs107.icoop.actor.Orb;
import ch.epfl.cs107.icoop.actor.OrbType;
import ch.epfl.cs107.icoop.actor.PressurePlate;
import ch.epfl.cs107.icoop.actor.WaterWall;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.signal.logic.Not;

public final class OrbWay extends ICoopArea {

    @Override
    public DiscreteCoordinates getRedPlayerSpawnPosition() {
        return new DiscreteCoordinates(1, 12);
    }

    @Override
    public DiscreteCoordinates getBluePlayerSpawnPosition() {
        return new DiscreteCoordinates(1, 5);
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));
        registerActor(new Door(this, Orientation.DOWN, Logic.TRUE, "Spawn",
                new DiscreteCoordinates(18, 15), new DiscreteCoordinates(18, 16),
                new DiscreteCoordinates(0, 14), new DiscreteCoordinates(0, 13),
                new DiscreteCoordinates(0, 12), new DiscreteCoordinates(0, 11),
                new DiscreteCoordinates(0, 10)));
        registerActor(new Door(this, Orientation.DOWN, Logic.TRUE, "Spawn",
                new DiscreteCoordinates(18, 15), new DiscreteCoordinates(18, 16),
                new DiscreteCoordinates(0, 8), new DiscreteCoordinates(0, 7),
                new DiscreteCoordinates(0, 6), new DiscreteCoordinates(0, 5),
                new DiscreteCoordinates(0, 4)));
        registerActor(new Orb(this, Orientation.DOWN, new DiscreteCoordinates(17, 12), OrbType.FIRE, dialogHandler));
        registerActor(new Orb(this, Orientation.DOWN, new DiscreteCoordinates(17, 6), OrbType.WATER, dialogHandler));

        PressurePlate firePlate = new PressurePlate(this, Orientation.DOWN, new DiscreteCoordinates(5, 7));
        PressurePlate waterPlate = new PressurePlate(this, Orientation.DOWN, new DiscreteCoordinates(5, 10));
        registerActor(firePlate);
        registerActor(waterPlate);

        for (int i = 0; i < 5; i++) {
            registerActor(new FireWall(this, Orientation.LEFT, new DiscreteCoordinates(12, 10 + i), new Not(firePlate)));
            registerActor(new WaterWall(this, Orientation.LEFT, new DiscreteCoordinates(12, 4 + i), new Not(waterPlate)));
        }
        registerActor(new WaterWall(this, Orientation.LEFT, new DiscreteCoordinates(7, 12), Logic.TRUE));
        registerActor(new FireWall(this, Orientation.LEFT, new DiscreteCoordinates(7, 6), Logic.TRUE));

        registerActor(new Heart(this, Orientation.DOWN, new DiscreteCoordinates(8, 4)));
        registerActor(new Heart(this, Orientation.DOWN, new DiscreteCoordinates(10, 6)));
        registerActor(new Heart(this, Orientation.DOWN, new DiscreteCoordinates(5, 13)));
        registerActor(new Heart(this, Orientation.DOWN, new DiscreteCoordinates(10, 11)));
    }

    @Override
    public String getTitle() {
        return "OrbWay";
    }
}
