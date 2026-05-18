package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.KeyBindings;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.Collections;
import java.util.List;

/**
 * A ICoopPlayer is a player for the ICoop game.
 */
public class ICoopPlayer extends MovableAreaEntity implements ElementalEntity{

    private static final int ANIMATION_DURATION = 4;

    private final KeyBindings.PlayerKeyBindings keys;
    private final Element element;
    private final OrientedAnimation animation;

    public ICoopPlayer(Area owner, Orientation orientation, DiscreteCoordinates coordinates,
                       KeyBindings.PlayerKeyBindings keys, Element element) {
        super(owner, orientation, coordinates);
        this.keys = keys;
        this.element = element;
        String spriteName = (element == Element.FIRE) ? "icoop/player" : "icoop/player2";
        this.animation = new OrientedAnimation(spriteName, ANIMATION_DURATION, this, Vector.ZERO,
                new Orientation[]{Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT},
                4, 1, 2, 16, 32, true);
        resetMotion();
    }

    @Override
    public void update(float deltaTime) {
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
        animation.draw(canvas);
    }

    private void moveIfPressed(Orientation orientation, Button button) {
        if (button.isDown() && !isDisplacementOccurs()) {
            orientate(orientation);
            move(ANIMATION_DURATION);
        }
    }

    public void enterArea(Area area, DiscreteCoordinates position) {
        area.registerActor(this);
        area.setViewCandidate(this);
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
        resetMotion();
    }

    public void leaveArea() {
        getOwnerArea().unregisterActor(this);
    }

    @Override
    public Element element() {
        return element;
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
    }
}
