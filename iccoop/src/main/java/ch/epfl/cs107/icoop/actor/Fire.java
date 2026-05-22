package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Fire extends MovableAreaEntity implements Unstoppable, Interactor {

    private static final int ANIMATION_DURATION = 3;
    private static final int FRAME_COUNT = 7;
    private static final int MAX_DISTANCE = 5;

    private final Animation animation;
    private final FireInteractionHandler handler;
    private int distanceTraveled;
    private boolean stopped;

    public Fire(Area owner, Orientation orientation, DiscreteCoordinates position) {
        super(owner, orientation, position);
        Sprite[] sprites = RPGSprite.extractSprites("icoop/fire", FRAME_COUNT, 1f, 1f, this, 16, 16);
        this.animation = new Animation(ANIMATION_DURATION, sprites, true);
        this.handler = new FireInteractionHandler();
        this.distanceTraveled = 0;
        this.stopped = false;
    }

    private void stop() {
        stopped = true;
        getOwnerArea().unregisterActor(this);
    }

    @Override
    public void update(float deltaTime) {
        if (stopped) return;
        animation.update(deltaTime);
        if (!isDisplacementOccurs()) {
            if (distanceTraveled >= MAX_DISTANCE) { stop(); return; }
            if (!move(ANIMATION_DURATION)) { stop(); return; }
            distanceTraveled++;
        }
        super.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
        if (!stopped) animation.draw(canvas);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.emptyList();
    }

    @Override
    public boolean wantsCellInteraction() { return !stopped; }

    @Override
    public boolean wantsViewInteraction() { return false; }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    @Override
    public boolean takeCellSpace() { return false; }

    @Override
    public boolean isCellInteractable() { return false; }

    @Override
    public boolean isViewInteractable() { return false; }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    private class FireInteractionHandler implements ICoopInteractionVisitor {
        @Override
        public void interactWith(ICoopPlayer player, boolean isCellInteraction) {
            player.takeDamage(DamageType.FIRE, 1);
            stop();
        }
    }
}
