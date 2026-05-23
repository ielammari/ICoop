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

public class MagicProjectile extends MovableAreaEntity implements Unstoppable, Interactor {

    private static final int ANIMATION_DURATION = 12;
    private static final int NB_FRAMES = 4;
    private static final int FRAME_SIZE = 32;
    private static final int MAX_DISTANCE = 5;

    private final Animation animation;
    private final DamageType damageType;
    private final MagicInteractionHandler handler;
    private int distanceTraveled;
    private boolean stopped;

    public MagicProjectile(Area owner, Orientation orientation, DiscreteCoordinates position, Element element) {
        super(owner, orientation, position);
        this.damageType = (element == Element.FIRE) ? DamageType.FIRE : DamageType.WATER;
        Sprite[] sprites = (element == Element.FIRE) ? RPGSprite.extractSprites("icoop/magicFireProjectile",
                NB_FRAMES, 1f, 1f, this, FRAME_SIZE, FRAME_SIZE) : RPGSprite.extractSprites("icoop/magicWaterProjectile",
                NB_FRAMES, 1f, 1f, this, FRAME_SIZE, FRAME_SIZE);
        this.animation = new Animation(ANIMATION_DURATION / NB_FRAMES, sprites, true);
        this.handler = new MagicInteractionHandler();
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
            if (!move(ANIMATION_DURATION / NB_FRAMES)) { stop(); return; }
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

    private class MagicInteractionHandler implements ICoopInteractionVisitor {
        @Override
        public void interactWith(Foe foe, boolean isCellInteraction) {
            foe.takeDamage(damageType, 1);
            stop();
        }

        @Override
        public void interactWith(Rock rock, boolean isCellInteraction) {
            rock.destroy();
        }

        @Override
        public void interactWith(Explosive explosive, boolean isCellInteraction) {
            if (explosive.isInactive()) explosive.activate();
            stop();
        }
    }
}
