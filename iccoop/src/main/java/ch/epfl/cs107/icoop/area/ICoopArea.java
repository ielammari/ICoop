package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.ICoopBehavior;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

public abstract class ICoopArea extends Area {

    public static final float DEFAULT_SCALE_FACTOR = 13.f;

    protected abstract void createArea();

    public abstract DiscreteCoordinates getRedPlayerSpawnPosition();

    public abstract DiscreteCoordinates getBluePlayerSpawnPosition();

    @Override
    public boolean isViewCentered() {
        return true;
    }

    @Override
    public final float getCameraScaleFactor() {
        return DEFAULT_SCALE_FACTOR;
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            setBehavior(new ICoopBehavior(window, getTitle()));
            createArea();
            return true;
        }
        return false;
    }
}
