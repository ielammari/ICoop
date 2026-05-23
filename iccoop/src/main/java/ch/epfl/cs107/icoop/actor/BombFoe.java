package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.math.random.RandomGenerator;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BombFoe extends Foe implements Interactor {

    private enum State { IDLE, ATTACK, PROTECT, VULNERABLE }

    private static final int MAX_HP = 3;
    private static final int ANIMATION_DURATION = 24;
    private static final int ANIMATION_FRAME_DURATION = ANIMATION_DURATION / 3;
    private static final int NB_FRAMES = 4;
    private static final int FRAME_SIZE = 32;

    private static final int NORMAL_SPEED = 2;
    private static final int FAST_SPEED = 5;
    private static final int SLOW_SPEED = 1;

    private static final int MAX_INACTION = 24;
    private static final int PERCEPTION_RANGE = 8;
    private static final float CLOSE_DISTANCE = 3f;
    private static final int MIN_PROTECT_FRAMES = 72;
    private static final int MAX_PROTECT_FRAMES = 120;
    private static final int VULNERABLE_FRAMES = 72;
    private static final double CHANGE_ORIENTATION_PROBABILITY = 0.4;

    private final OrientedAnimation animation;
    private final Animation protectingAnimation;
    private final BombFoeInteractionHandler handler;

    private State state;
    private int inactionTimer;
    private int protectTimer;
    private int vulnerableTimer;
    private ICoopPlayer target;

    public BombFoe(Area owner, DiscreteCoordinates position) {
        super(owner, Orientation.DOWN, position, MAX_HP,
                Arrays.asList(DamageType.PHYSICAL, DamageType.FIRE));
        Vector anchor = new Vector(-0.25f, 0f);
        int[] columnForOrientation = {2, 3, 0, 1};
        this.animation = buildOrientedAnimation("icoop/bombMonster", anchor, columnForOrientation,
                ANIMATION_FRAME_DURATION, true);
        Sprite[] protectFrames = new Sprite[NB_FRAMES];
        for (int f = 0; f < NB_FRAMES; f++) {
            protectFrames[f] = new RPGSprite("icoop/bombMonster.protecting", 1.7f, 1.7f, this,
                    new RegionOfInterest(0, f * FRAME_SIZE, FRAME_SIZE, FRAME_SIZE), anchor);
        }
        this.protectingAnimation = new Animation(ANIMATION_FRAME_DURATION, protectFrames, true);
        this.handler = new BombFoeInteractionHandler();
        this.state = State.IDLE;
        this.inactionTimer = 0;
        this.protectTimer = 0;
        this.vulnerableTimer = 0;
        this.target = null;
    }

    private OrientedAnimation buildOrientedAnimation(String name, Vector anchor, int[] columnForOrientation,
                                                     int duration, boolean repeat) {
        Sprite[][] sprites = new Sprite[4][NB_FRAMES];
        for (Orientation o : Orientation.values()) {
            int column = columnForOrientation[o.ordinal()];
            for (int f = 0; f < NB_FRAMES; f++) {
                sprites[o.ordinal()][f] = new RPGSprite(name, 1.7f, 1.7f, this,
                        new RegionOfInterest(column * FRAME_SIZE, f * FRAME_SIZE, FRAME_SIZE, FRAME_SIZE), anchor);
            }
        }
        return new OrientedAnimation(Animation.createAnimations(duration, sprites, repeat), this);
    }

    @Override
    public void takeDamage(DamageType type, int amount) {
        if (state == State.PROTECT) return;
        super.takeDamage(type, amount);
    }

    @Override
    protected void updateAlive(float deltaTime) {
        if (state == State.PROTECT) protectingAnimation.update(deltaTime);
        else animation.update(deltaTime);

        if (isImmune()) {
            state = State.IDLE;
            inactionTimer = 0;
            target = null;
            return;
        }
        if (inactionTimer > 0) {
            inactionTimer--;
            return;
        }
        if (state == State.IDLE) idleBehavior();
        else if (state == State.ATTACK) attackBehavior();
        else if (state == State.PROTECT) protectBehavior();
        else if (state == State.VULNERABLE) vulnerableBehavior();
    }

    private void idleBehavior() {
        if (!isDisplacementOccurs()) {
            randomMove(NORMAL_SPEED);
            inactionTimer = RandomGenerator.getInstance().nextInt(MAX_INACTION + 1);
        }
    }

    private void attackBehavior() {
        if (target == null) {
            state = State.IDLE;
            return;
        }
        DiscreteCoordinates myCell = getCurrentMainCellCoordinates();
        DiscreteCoordinates targetCell = target.getCurrentMainCellCoordinates();
        if (DiscreteCoordinates.distanceBetween(myCell, targetCell) <= CLOSE_DISTANCE) {
            dropExplosiveAndProtect(myCell);
            return;
        }
        if (!isDisplacementOccurs()) {
            Vector v = target.getPosition().sub(getPosition());
            Orientation desired = (Math.abs(v.x) > Math.abs(v.y))
                    ? Orientation.fromVector(new Vector(v.x, 0f))
                    : Orientation.fromVector(new Vector(0f, v.y));
            if (desired != null && getOrientation() != desired) {
                orientate(desired);
            } else {
                move(ANIMATION_DURATION / FAST_SPEED);
            }
        }
    }

    private void dropExplosiveAndProtect(DiscreteCoordinates myCell) {
        DiscreteCoordinates front = myCell.jump(getOrientation().toVector());
        Explosive explosive = new Explosive(getOwnerArea(), getOrientation(), front);
        if (getOwnerArea().canEnterAreaCells(explosive, Collections.singletonList(front))) {
            getOwnerArea().registerActor(explosive);
            explosive.activate();
        }
        state = State.PROTECT;
        protectTimer = MIN_PROTECT_FRAMES
                + RandomGenerator.getInstance().nextInt(MAX_PROTECT_FRAMES - MIN_PROTECT_FRAMES + 1);
        target = null;
        protectingAnimation.reset();
    }

    private void protectBehavior() {
        protectTimer--;
        if (protectTimer <= 0) {
            state = State.VULNERABLE;
            vulnerableTimer = VULNERABLE_FRAMES;
            return;
        }
        if (!isDisplacementOccurs()) {
            randomMove(SLOW_SPEED);
        }
    }

    private void vulnerableBehavior() {
        vulnerableTimer--;
        if (vulnerableTimer <= 0) {
            state = State.IDLE;
            return;
        }
        if (!isDisplacementOccurs()) {
            randomMove(NORMAL_SPEED);
        }
    }

    private void randomMove(int speedFactor) {
        if (RandomGenerator.getInstance().nextDouble() < CHANGE_ORIENTATION_PROBABILITY) {
            orientate(Orientation.values()[RandomGenerator.getInstance().nextInt(Orientation.values().length)]);
        }
        move(ANIMATION_DURATION / speedFactor);
    }

    @Override
    protected void drawAlive(Canvas canvas) {
        if (state == State.PROTECT) protectingAnimation.draw(canvas);
        else animation.draw(canvas);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        DiscreteCoordinates origin = getCurrentMainCellCoordinates();
        List<DiscreteCoordinates> cells = new ArrayList<>();
        if (state == State.ATTACK) {
            for (int i = 1; i <= 1; i++) {
                cells.add(origin.jump(getOrientation().toVector().mul(i)));
            }
        } else {
            for (Orientation o : Orientation.values()) {
                for (int i = 1; i <= PERCEPTION_RANGE; i++) {
                    cells.add(origin.jump(o.toVector().mul(i)));
                }
            }
        }
        return cells;
    }

    @Override
    public boolean wantsCellInteraction() { return false; }

    @Override
    public boolean wantsViewInteraction() {
        return isAlive() && (state == State.IDLE || state == State.ATTACK);
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    private class BombFoeInteractionHandler implements ICoopInteractionVisitor {
        @Override
        public void interactWith(ICoopPlayer player, boolean isCellInteraction) {
            if (!isCellInteraction && (state == State.IDLE || state == State.ATTACK)) {
                target = player;
                if (state == State.IDLE) {
                    state = State.ATTACK;
                    inactionTimer = 0;
                }
            }
        }
    }
}
