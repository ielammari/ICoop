package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.actor.Door;
import ch.epfl.cs107.icoop.actor.Explosive;
import ch.epfl.cs107.icoop.actor.ManorDoor;
import ch.epfl.cs107.icoop.actor.Rock;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.And;
import ch.epfl.cs107.play.signal.logic.Logic;

public final class Spawn extends ICoopArea {

    private boolean welcomeShown = false;
    private Logic mazeChallenge = Logic.FALSE;
    private Logic arenaChallenge = Logic.FALSE;

    public void setChallenges(Logic mazeChallenge, Logic arenaChallenge) {
        this.mazeChallenge = mazeChallenge;
        this.arenaChallenge = arenaChallenge;
    }

    @Override
    public void update(float deltaTime) {
        if (!welcomeShown && dialogHandler != null) {
            dialogHandler.publish(new Dialog("welcome"));
            welcomeShown = true;
        }
        super.update(deltaTime);
    }

    @Override
    public DiscreteCoordinates getRedPlayerSpawnPosition() {
        return new DiscreteCoordinates(13, 6);
    }

    @Override
    public DiscreteCoordinates getBluePlayerSpawnPosition() {
        return new DiscreteCoordinates(14, 6);
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));
        registerActor(new Door(this, Orientation.DOWN, Logic.TRUE, "OrbWay",
                new DiscreteCoordinates(1, 12), new DiscreteCoordinates(1, 5),
                new DiscreteCoordinates(19, 15), new DiscreteCoordinates(19, 16)));
        registerActor(new Door(this, Orientation.DOWN, Logic.TRUE, "Maze",
                new DiscreteCoordinates(2, 39), new DiscreteCoordinates(3, 39),
                new DiscreteCoordinates(4, 0), new DiscreteCoordinates(5, 0)));
        registerActor(new Rock(this, Orientation.DOWN, new DiscreteCoordinates(10, 10)));
        registerActor(new Explosive(this, Orientation.DOWN, new DiscreteCoordinates(11, 10)));
        registerActor(new ManorDoor(this, Orientation.DOWN, new DiscreteCoordinates(6, 11),
                new And(mazeChallenge, arenaChallenge), dialogHandler));
    }

    @Override
    public String getTitle() {
        return "Spawn";
    }
}
