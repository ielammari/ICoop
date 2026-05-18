package ch.epfl.cs107.icoop;

import ch.epfl.cs107.icoop.area.OrbWay;
import ch.epfl.cs107.icoop.area.Spawn;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.window.Window;

public class ICoop extends AreaGame {

    private void createAreas() {
        addArea(new Spawn());
        addArea(new OrbWay());
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            setCurrentArea("Spawn", true);
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
