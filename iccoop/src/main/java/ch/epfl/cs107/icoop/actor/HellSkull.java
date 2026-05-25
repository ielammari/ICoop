package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Arrays;
import java.util.Random;

public final class HellSkull extends Foe {

    private static final int ANIMATION_DURATION = 4;
    private static final int MIN_FIRE_INTERVAL = 12;
    private static final int MAX_FIRE_INTERVAL = 48;

    private final OrientedAnimation animation;
    private final Random random;
    private int fireTimer;

    public HellSkull(Area owner, Orientation orientation, DiscreteCoordinates position) {
        super(owner, orientation, position, 1,
                Arrays.asList(DamageType.PHYSICAL, DamageType.WATER));
        this.random = new Random();
        this.animation = new OrientedAnimation("icoop/flameskull", ANIMATION_DURATION, this,
                new Vector(-0.5f, -0.5f),
                new Orientation[]{Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT},
                3, 2, 2, 32, 32, true);
        this.fireTimer = MIN_FIRE_INTERVAL + random.nextInt(MAX_FIRE_INTERVAL - MIN_FIRE_INTERVAL + 1);
    }

    @Override
    public void dealContactDamageTo(ICoopPlayer player) {
        player.takeDamage(DamageType.FIRE, 1);
    }

    @Override
    protected boolean displaysHealthBar() { return false; }

    @Override
    protected void updateAlive(float deltaTime) {
        animation.update(deltaTime);
        fireTimer--;
        if (fireTimer <= 0) {
            Orientation dir = getOrientation();
            DiscreteCoordinates firePos = getCurrentMainCellCoordinates().jump(dir.toVector());
            Fire fire = new Fire(getOwnerArea(), dir, firePos);
            getOwnerArea().registerActor(fire);
            fireTimer = MIN_FIRE_INTERVAL + random.nextInt(MAX_FIRE_INTERVAL - MIN_FIRE_INTERVAL + 1);
        }
    }

    @Override
    protected void drawAlive(Canvas canvas) {
        animation.draw(canvas);
    }
}
