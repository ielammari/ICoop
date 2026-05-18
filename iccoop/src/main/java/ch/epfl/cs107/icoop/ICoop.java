package ch.epfl.cs107.icoop;

import ch.epfl.cs107.icoop.actor.Element;
import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.area.OrbWay;
import ch.epfl.cs107.icoop.area.Spawn;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;

import static ch.epfl.cs107.icoop.KeyBindings.RED_PLAYER_KEY_BINDINGS;

public class ICoop extends AreaGame {

    private ICoopPlayer redPlayer;

    private void createAreas() {
        addArea(new Spawn());
        addArea(new OrbWay());
    }

    private void initArea(String title) {
        ICoopArea area = (ICoopArea) setCurrentArea(title, true);
        redPlayer = new ICoopPlayer(area, Orientation.DOWN, area.getRedPlayerSpawnPosition(),
                RED_PLAYER_KEY_BINDINGS, Element.FIRE);
        redPlayer.enterArea(area, area.getRedPlayerSpawnPosition());
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
