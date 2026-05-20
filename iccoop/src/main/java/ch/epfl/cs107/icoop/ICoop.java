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

import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.window.Keyboard;

import static ch.epfl.cs107.icoop.KeyBindings.BLUE_PLAYER_KEY_BINDINGS;
import static ch.epfl.cs107.icoop.KeyBindings.NEXT_DIALOG;
import static ch.epfl.cs107.icoop.KeyBindings.RED_PLAYER_KEY_BINDINGS;
import static ch.epfl.cs107.icoop.KeyBindings.RESET_AREA;
import static ch.epfl.cs107.icoop.KeyBindings.RESET_GAME;

public class ICoop extends AreaGame implements DialogHandler {

    private Dialog activeDialog;

    private ICoopPlayer redPlayer;
    private ICoopPlayer bluePlayer;
    private CenterOfMass centerOfMass;

    private void createAreas() {
        Spawn spawn = new Spawn();
        OrbWay orbWay = new OrbWay();
        spawn.setDialogHandler(this);
        orbWay.setDialogHandler(this);
        addArea(spawn);
        addArea(orbWay);
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

    private void resetGame() {
        activeDialog = null;
        redPlayer.leaveArea();
        bluePlayer.leaveArea();
        createAreas();
        initArea("Spawn");
    }

    private void resetArea() {
        activeDialog = null;
        String title = getCurrentArea().getTitle();
        redPlayer.leaveArea();
        bluePlayer.leaveArea();
        ICoopArea area = (ICoopArea) setCurrentArea(title, true);
        redPlayer.resetStats();
        bluePlayer.resetStats();
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
        if (redPlayer.hasPendingAreaReset() || bluePlayer.hasPendingAreaReset()) {
            redPlayer.consumePendingAreaReset();
            bluePlayer.consumePendingAreaReset();
            resetArea();
            return;
        }
        Keyboard keyboard = getCurrentArea().getKeyboard();
        if (keyboard.get(RESET_GAME).isPressed()) {
            resetGame();
            return;
        }
        if (keyboard.get(RESET_AREA).isPressed()) {
            resetArea();
            return;
        }
        if (activeDialog != null) {
            if (keyboard.get(NEXT_DIALOG).isPressed()) {
                activeDialog.update(0);
            }
            if (activeDialog.isCompleted()) {
                activeDialog = null;
            } else {
                return;
            }
        }
        processPendingTransitions();
        updateCameraScale();
        super.update(deltaTime);
    }

    @Override
    public void publish(Dialog dialog) {
        activeDialog = dialog;
    }

    @Override
    public void draw() {
        super.draw();
        if (activeDialog != null) {
            activeDialog.draw(getWindow());
        }
    }

    @Override
    public void end() {
    }

    @Override
    public String getTitle() {
        return "ICoop";
    }
}
