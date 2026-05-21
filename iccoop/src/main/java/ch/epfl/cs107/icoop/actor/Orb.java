package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.DialogHandler;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class Orb extends ElementalItem {

    private static final int FRAME_COUNT = 6;
    private static final int FRAME_DURATION = 4;

    private final OrbType orbType;
    private final Animation animation;
    private final DialogHandler dialogHandler;

    public Orb(Area owner, Orientation orientation, DiscreteCoordinates position,
               OrbType orbType, DialogHandler dialogHandler) {
        super(owner, orientation, position, orbType.element());
        this.orbType = orbType;
        this.dialogHandler = dialogHandler;
        Sprite[] sprites = new Sprite[FRAME_COUNT];
        for (int i = 0; i < FRAME_COUNT; i++) {
            sprites[i] = new Sprite("icoop/orb", 1f, 1f, this,
                    new RegionOfInterest(i * 32, orbType.spriteYDelta(), 32, 32), Vector.ZERO);
        }
        this.animation = new Animation(FRAME_DURATION, sprites, true);
    }

    @Override
    public void collect() {
        dialogHandler.publish(new Dialog(orbType.dialogName()));
        super.collect();
    }

    @Override
    public void update(float deltaTime) {
        animation.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }
}
