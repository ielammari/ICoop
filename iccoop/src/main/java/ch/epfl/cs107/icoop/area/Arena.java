package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.ICoopBehavior.ICoopCellType;
import ch.epfl.cs107.icoop.actor.Element;
import ch.epfl.cs107.icoop.actor.Key;
import ch.epfl.cs107.icoop.actor.Obstacle;
import ch.epfl.cs107.icoop.actor.Rock;
import ch.epfl.cs107.icoop.actor.Teleporter;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.And;
import ch.epfl.cs107.play.signal.logic.Logic;

public final class Arena extends ICoopArea implements Logic {

    private Key redKey;
    private Key blueKey;

    @Override
    public DiscreteCoordinates getRedPlayerSpawnPosition() {
        return new DiscreteCoordinates(4, 5);
    }

    @Override
    public DiscreteCoordinates getBluePlayerSpawnPosition() {
        return new DiscreteCoordinates(14, 15);
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));

        for (DiscreteCoordinates c : behavior.cellsOfType(ICoopCellType.ROCK)) {
            registerActor(new Rock(this, Orientation.DOWN, c));
        }
        for (DiscreteCoordinates c : behavior.cellsOfType(ICoopCellType.OBSTACLE)) {
            registerActor(new Obstacle(this, Orientation.DOWN, c, "rock.2"));
        }

        redKey = new Key(this, Orientation.DOWN, new DiscreteCoordinates(9, 16), Element.FIRE);
        blueKey = new Key(this, Orientation.DOWN, new DiscreteCoordinates(9, 4), Element.WATER);
        registerActor(redKey);
        registerActor(blueKey);

        registerActor(new Teleporter(this, Orientation.DOWN, new And(redKey, blueKey), "Spawn",
                new DiscreteCoordinates(13, 6), new DiscreteCoordinates(14, 6),
                new DiscreteCoordinates(10, 11)));
    }

    @Override
    public boolean isOn() {
        return redKey != null && blueKey != null && redKey.isOn() && blueKey.isOn();
    }

    @Override
    public boolean isOff() {
        return !isOn();
    }

    @Override
    public String getTitle() {
        return "Arena";
    }
}
