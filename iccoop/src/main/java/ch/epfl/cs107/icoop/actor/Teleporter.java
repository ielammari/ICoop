package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

public class Teleporter extends Door {

    private final Sprite sprite;

    public Teleporter(Area owner, Orientation orientation, Logic signal,
                      String destinationArea,
                      DiscreteCoordinates redDestination,
                      DiscreteCoordinates blueDestination,
                      DiscreteCoordinates... cells) {
        super(owner, orientation, signal, destinationArea, redDestination, blueDestination, cells);
        this.sprite = new RPGSprite("shadow", 1f, 1f, this);
    }

    @Override
    public void draw(Canvas canvas) {
        if (getSignal().isOn()) sprite.draw(canvas);
    }
}
