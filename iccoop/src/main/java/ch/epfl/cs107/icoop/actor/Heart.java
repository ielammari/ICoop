package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public class Heart extends ICoopCollectable {

    private static final int FRAME_COUNT = 4;
    private static final int FRAME_DURATION = 6;

    private final Animation animation;

    public Heart(Area owner, Orientation orientation, DiscreteCoordinates position) {
        super(owner, orientation, position);
        Sprite[] sprites = RPGSprite.extractSprites("icoop/heart", FRAME_COUNT, 1f, 1f, this, 16, 16);
        this.animation = new Animation(FRAME_DURATION, sprites, true);
    }

    @Override
    public void update(float deltaTime) { animation.update(deltaTime); }

    @Override
    public void draw(Canvas canvas) { animation.draw(canvas); }
}
