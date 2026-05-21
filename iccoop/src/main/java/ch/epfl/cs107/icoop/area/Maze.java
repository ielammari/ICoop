package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.actor.Explosive;
import ch.epfl.cs107.icoop.actor.FireWall;
import ch.epfl.cs107.icoop.actor.Heart;
import ch.epfl.cs107.icoop.actor.PressurePlate;
import ch.epfl.cs107.icoop.actor.WaterWall;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.signal.logic.Not;

public final class Maze extends ICoopArea {

    @Override
    public DiscreteCoordinates getRedPlayerSpawnPosition() {
        return new DiscreteCoordinates(2, 39);
    }

    @Override
    public DiscreteCoordinates getBluePlayerSpawnPosition() {
        return new DiscreteCoordinates(3, 39);
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));

        registerActor(new WaterWall(this, Orientation.LEFT, new DiscreteCoordinates(4, 35), Logic.TRUE));
        registerActor(new WaterWall(this, Orientation.LEFT, new DiscreteCoordinates(4, 36), Logic.TRUE));

        PressurePlate plate1 = new PressurePlate(this, Orientation.DOWN, new DiscreteCoordinates(6, 33));
        registerActor(plate1);
        registerActor(new FireWall(this, Orientation.LEFT, new DiscreteCoordinates(6, 35), new Not(plate1)));
        registerActor(new FireWall(this, Orientation.LEFT, new DiscreteCoordinates(6, 36), new Not(plate1)));

        registerActor(new FireWall(this, Orientation.DOWN, new DiscreteCoordinates(2, 34), Logic.TRUE));
        registerActor(new FireWall(this, Orientation.DOWN, new DiscreteCoordinates(3, 34), Logic.TRUE));

        registerActor(new Explosive(this, Orientation.DOWN, new DiscreteCoordinates(6, 25)));

        registerActor(new WaterWall(this, Orientation.DOWN, new DiscreteCoordinates(5, 24), Logic.TRUE));
        registerActor(new WaterWall(this, Orientation.DOWN, new DiscreteCoordinates(6, 24), Logic.TRUE));

        PressurePlate plate2 = new PressurePlate(this, Orientation.DOWN, new DiscreteCoordinates(9, 25));
        registerActor(plate2);
        registerActor(new FireWall(this, Orientation.DOWN, new DiscreteCoordinates(8, 21), new Not(plate2)));

        registerActor(new Heart(this, Orientation.DOWN, new DiscreteCoordinates(15, 18)));
        registerActor(new Heart(this, Orientation.DOWN, new DiscreteCoordinates(16, 19)));
        registerActor(new Heart(this, Orientation.DOWN, new DiscreteCoordinates(14, 19)));
        registerActor(new Heart(this, Orientation.DOWN, new DiscreteCoordinates(14, 17)));

        registerActor(new WaterWall(this, Orientation.DOWN, new DiscreteCoordinates(8, 4), Logic.TRUE));
        registerActor(new FireWall(this, Orientation.DOWN, new DiscreteCoordinates(13, 4), Logic.TRUE));
    }

    @Override
    public String getTitle() {
        return "Maze";
    }
}
