package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.actor.*;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.signal.logic.Not;
import ch.epfl.cs107.play.signal.logic.Or;

public final class Maze extends ICoopArea implements Logic {

    private Staff fireStaff;
    private Staff waterStaff;

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

        int[][] skullPositions = {
                {12, 33}, {12, 31}, {12, 29}, {12, 27}, {12, 25},
                {10, 32}, {10, 30}, {10, 28}, {10, 26}
        };
        for (int[] pos : skullPositions) {
            registerActor(new HellSkull(this, Orientation.RIGHT,
                    new DiscreteCoordinates(pos[0], pos[1])));
        }

        registerActor(new Rock(this, Orientation.DOWN, new DiscreteCoordinates(17, 7)));
        registerActor(new Rock(this, Orientation.DOWN, new DiscreteCoordinates(17, 6)));

        fireStaff = new Staff(this, Orientation.DOWN, new DiscreteCoordinates(13, 2), Element.FIRE);
        waterStaff = new Staff(this, Orientation.DOWN, new DiscreteCoordinates(8, 2), Element.WATER);
        registerActor(fireStaff);
        registerActor(waterStaff);

        registerActor(new Door(this, Orientation.DOWN, new Or(fireStaff, waterStaff), "Arena",
                new DiscreteCoordinates(4, 5), new DiscreteCoordinates(14, 15),
                new DiscreteCoordinates(19, 6), new DiscreteCoordinates(19, 7)));

        int[][] bombFoePositions = {
                {5, 15}, {4, 10}, {5, 12}, {10, 17}, {5, 14}
        };
        for (int[] pos : bombFoePositions) {
            registerActor(new BombFoe(this, new DiscreteCoordinates(pos[0], pos[1])));
        }
    }

    @Override
    public boolean isOn() {
        return fireStaff != null && waterStaff != null && fireStaff.isOn() && waterStaff.isOn();
    }

    @Override
    public boolean isOff() {
        return !isOn();
    }

    @Override
    public String getTitle() {
        return "Maze";
    }
}
