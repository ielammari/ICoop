package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.KeyBindings;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.Collections;
import java.util.List;

public class ICoopPlayer extends MovableAreaEntity implements ElementalEntity, Interactor {

    private static final int ANIMATION_DURATION = 4;
    private static final int MAX_LIFE = 5;
    private static final int IMMUNITY_FRAMES = 24;

    private final KeyBindings.PlayerKeyBindings keys;
    private final Element element;
    private final OrientedAnimation animation;
    private final Health health;
    private final ICoopPlayerInteractionHandler interactionHandler;

    private int immunityCounter;
    private Door pendingDoor;
    private boolean pendingAreaReset;

    public ICoopPlayer(Area owner, Orientation orientation, DiscreteCoordinates coordinates,
                       KeyBindings.PlayerKeyBindings keys, Element element) {
        super(owner, orientation, coordinates);
        this.keys = keys;
        this.element = element;
        this.interactionHandler = new ICoopPlayerInteractionHandler();
        String spriteName = (element == Element.FIRE) ? "icoop/player" : "icoop/player2";
        this.animation = new OrientedAnimation(spriteName, ANIMATION_DURATION, this, Vector.ZERO,
                new Orientation[]{Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT},
                4, 1, 2, 16, 32, true);
        this.health = new Health(this, Transform.I.translated(0, 1.75f), MAX_LIFE, true);
        this.immunityCounter = 0;
        this.pendingAreaReset = false;
        resetMotion();
    }

    public void takeDamage(DamageType type, int amount) {
        if (immunityCounter > 0) return;
        health.decrease(amount);
        immunityCounter = IMMUNITY_FRAMES;
        if (health.isOff()) pendingAreaReset = true;
    }

    public boolean hasPendingAreaReset() { return pendingAreaReset; }
    public void consumePendingAreaReset() { pendingAreaReset = false; }

    public void resetStats() {
        health.resetHealth();
        immunityCounter = 0;
        pendingAreaReset = false;
    }

    public boolean hasPendingDoor() { return pendingDoor != null; }

    public Door consumePendingDoor() {
        Door d = pendingDoor;
        pendingDoor = null;
        return d;
    }

    @Override
    public void update(float deltaTime) {
        if (immunityCounter > 0) immunityCounter--;
        Keyboard keyboard = getOwnerArea().getKeyboard();
        moveIfPressed(Orientation.UP, keyboard.get(keys.up()));
        moveIfPressed(Orientation.LEFT, keyboard.get(keys.left()));
        moveIfPressed(Orientation.DOWN, keyboard.get(keys.down()));
        moveIfPressed(Orientation.RIGHT, keyboard.get(keys.right()));
        if (isDisplacementOccurs()) {
            animation.update(deltaTime);
        } else {
            animation.reset();
        }
        super.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
        health.draw(canvas);
        if (immunityCounter == 0 || immunityCounter % 2 == 0) {
            animation.draw(canvas);
        }
    }

    private void moveIfPressed(Orientation orientation, Button button) {
        if (button.isDown() && !isDisplacementOccurs()) {
            orientate(orientation);
            move(ANIMATION_DURATION);
        }
    }

    public void enterArea(Area area, DiscreteCoordinates position) {
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
        resetMotion();
        area.registerActor(this);
    }

    public void leaveArea() {
        getOwnerArea().unregisterActor(this);
    }

    @Override
    public Element element() { return element; }

    @Override
    public boolean takeCellSpace() { return true; }

    @Override
    public boolean isCellInteractable() { return true; }

    @Override
    public boolean isViewInteractable() { return true; }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(
                getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public boolean wantsCellInteraction() { return true; }

    @Override
    public boolean wantsViewInteraction() {
        return getOwnerArea().getKeyboard().get(keys.useItem()).isPressed();
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler, isCellInteraction);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    private class ICoopPlayerInteractionHandler implements ICoopInteractionVisitor {
        @Override
        public void interactWith(Door door, boolean isCellInteraction) {
            if (isCellInteraction && door.getSignal().isOn()) {
                pendingDoor = door;
            }
        }

        @Override
        public void interactWith(Explosive explosive, boolean isCellInteraction) {
            if (isCellInteraction) {
                if (explosive.isInactive()) explosive.collect();
            } else {
                explosive.activate();
            }
        }

        @Override
        public void interactWith(ElementalItem item, boolean isCellInteraction) {
            if (isCellInteraction && item.element() == element) {
                item.collect();
            }
        }
    }
}
