package ch.epfl.cs107.icoop;

import ch.epfl.cs107.icoop.actor.CenterOfMass;
import ch.epfl.cs107.icoop.actor.Door;
import ch.epfl.cs107.icoop.actor.Element;
import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.area.OrbWay;
import ch.epfl.cs107.icoop.area.Spawn;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;

import static ch.epfl.cs107.icoop.KeyBindings.BLUE_PLAYER_KEY_BINDINGS;
import static ch.epfl.cs107.icoop.KeyBindings.RED_PLAYER_KEY_BINDINGS;

public class ICoop extends AreaGame {

    private ICoopPlayer redPlayer;
    private ICoopPlayer bluePlayer;
    private CenterOfMass centerOfMass;

    private void createAreas() {
        addArea(new Spawn());
        addArea(new OrbWay());
    }

    private void initArea(String title) {
        ICoopArea area = (ICoopArea) setCurrentArea(title, true);
        redPlayer = new ICoopPlayer(area, Orientation.DOWN, area.getRedPlayerSpawnPosition(),
                RED_PLAYER_KEY_BINDINGS, Element.FIRE);
        bluePlayer = new ICoopPlayer(area, Orientation.DOWN, area.getBluePlayerSpawnPosition(),
                BLUE_PLAYER_KEY_BINDINGS, Element.WATER);
        centerOfMass = new CenterOfMass(redPlayer, bluePlayer);
        redPlayer.enterArea(area, area.getRedPlayerSpawnPosition());
        bluePlayer.enterArea(area, area.getBluePlayerSpawnPosition());
        area.setViewCandidate(centerOfMass);
    }

    private void processPendingTransitions() {
        Door door = null;
        if (redPlayer.hasPendingDoor()) {
            door = redPlayer.consumePendingDoor();
            bluePlayer.consumePendingDoor();
        } else if (bluePlayer.hasPendingDoor()) {
            door = bluePlayer.consumePendingDoor();
        }
        if (door != null) {
            redPlayer.leaveArea();
            bluePlayer.leaveArea();
            ICoopArea area = (ICoopArea) setCurrentArea(door.getDestinationArea(), false);
            redPlayer.enterArea(area, door.getDestinationFor(Element.FIRE));
            bluePlayer.enterArea(area, door.getDestinationFor(Element.WATER));
            area.setViewCandidate(centerOfMass);
        }
    }

    private void updateCameraScale() {
        float distance = redPlayer.getPosition().sub(bluePlayer.getPosition()).getLength();
        float scale = Math.max(ICoopArea.DEFAULT_SCALE_FACTOR,
                ICoopArea.DEFAULT_SCALE_FACTOR * 0.75f + distance / 2f);
        ((ICoopArea) getCurrentArea()).setCurrentScaleFactor(scale);
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            initArea("Spawn");
            return true;
        }
        return false;
    }

    @Override
    public void update(float deltaTime) {
        processPendingTransitions();
        updateCameraScale();
        super.update(deltaTime);
    }

    @Override
    public void end() {
    }

    @Override
    public String getTitle() {
        return "ICoop";
    }
}
