package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Arrays;
import java.util.List;

public class Explosive extends ICoopCollectable implements Interactor {

    private enum State { INACTIVE, ACTIVE, EXPLODING, DONE }

    private static final int COUNTDOWN_FRAMES = 48;
    private static final int IDLE_FRAME_DURATION = 8;
    private static final int EXPLOSION_FRAME_DURATION = 2;

    private State state;
    private int countdown;
    private final Animation idleAnimation;
    private final Animation explosionAnimation;
    private final ExplosiveInteractionHandler handler;

    public Explosive(Area owner, Orientation orientation, DiscreteCoordinates position) {
        super(owner, orientation, position);
        this.state = State.INACTIVE;
        this.countdown = COUNTDOWN_FRAMES;
        this.idleAnimation = new Animation("icoop/explosive", 2, 1, 1, this, 16, 16, IDLE_FRAME_DURATION, true);
        this.explosionAnimation = new Animation("icoop/explosion", 7, 1, 1, this, 32, 32,
                new Vector(0f, 0f), EXPLOSION_FRAME_DURATION, false);
        this.handler = new ExplosiveInteractionHandler();
    }

    public boolean isInactive() { return state == State.INACTIVE; }

    public void activate() {
        if (state == State.INACTIVE) state = State.ACTIVE;
    }

    @Override
    public void update(float deltaTime) {
        if (state == State.ACTIVE) {
            countdown--;
            int progress = COUNTDOWN_FRAMES - countdown;
            int speedFactor = Math.max(1, (int) Math.ceil((float) 2 * IDLE_FRAME_DURATION * progress / COUNTDOWN_FRAMES));
            idleAnimation.setSpeedFactor(speedFactor / 3);
            idleAnimation.update(deltaTime);
            if (countdown <= 0) state = State.EXPLODING;
        } else if (state == State.EXPLODING) {
            explosionAnimation.update(deltaTime);
            if (explosionAnimation.isCompleted()) {
                state = State.DONE;
                getOwnerArea().unregisterActor(this);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (state == State.EXPLODING) {
            explosionAnimation.draw(canvas);
        } else if (state != State.DONE) {
            idleAnimation.draw(canvas);
        }
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        DiscreteCoordinates pos = getCurrentMainCellCoordinates();
        return Arrays.asList(
                pos.jump(Orientation.UP.toVector()),
                pos.jump(Orientation.DOWN.toVector()),
                pos.jump(Orientation.LEFT.toVector()),
                pos.jump(Orientation.RIGHT.toVector())
        );
    }

    @Override
    public boolean wantsCellInteraction() {
        return state == State.EXPLODING;
    }

    @Override
    public boolean wantsViewInteraction() {
        return state == State.EXPLODING;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    @Override
    public boolean isViewInteractable() { return true; }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    private class ExplosiveInteractionHandler implements ICoopInteractionVisitor {
        @Override
        public void interactWith(ICoopPlayer player, boolean isCellInteraction) {
            player.takeDamage(DamageType.PHYSICAL, 2);
        }

        @Override
        public void interactWith(Rock rock, boolean isCellInteraction) {
            rock.destroy();
        }
    }
}
