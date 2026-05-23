package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public abstract class Foe extends MovableAreaEntity {

    private enum State { ALIVE, DYING, DEAD }

    private static final int IMMUNITY_FRAMES = 24;
    private static final int VANISH_FRAME_COUNT = 7;
    private static final int VANISH_FRAME_DURATION = 2;

    private final Health health;
    private final List<DamageType> vulnerabilities;
    private final Animation vanishAnimation;
    private int immunityCounter;
    private State state;

    protected Foe(Area owner, Orientation orientation, DiscreteCoordinates position,
                  int maxHP, List<DamageType> vulnerabilities) {
        super(owner, orientation, position);
        this.health = new Health(this, Transform.I.translated(0, 1.6f), maxHP, false);
        this.vulnerabilities = vulnerabilities;
        Sprite[] vanishSprites = RPGSprite.extractSprites("icoop/vanish", VANISH_FRAME_COUNT, 2f, 2f, this, new Vector(-0.5f, -0.5f), 32, 32);
        this.vanishAnimation = new Animation(VANISH_FRAME_DURATION, vanishSprites, false);
        this.immunityCounter = 0;
        this.state = State.ALIVE;
    }

    public void takeDamage(DamageType type, int amount) {
        if (state != State.ALIVE) return;
        if (immunityCounter > 0) return;
        if (!vulnerabilities.contains(type)) return;
        health.decrease(amount);
        immunityCounter = IMMUNITY_FRAMES;
        if (health.isOff()) state = State.DYING;
    }

    public void dealContactDamageTo(ICoopPlayer player) {}

    protected boolean isAlive() { return state == State.ALIVE; }

    protected boolean isImmune() { return immunityCounter > 0; }

    protected boolean displaysHealthBar() { return true; }

    @Override
    public void update(float deltaTime) {
        if (state == State.ALIVE) {
            if (immunityCounter > 0) immunityCounter--;
            updateAlive(deltaTime);
            super.update(deltaTime);
        } else if (state == State.DYING) {
            vanishAnimation.update(deltaTime);
            if (vanishAnimation.isCompleted()) {
                state = State.DEAD;
                getOwnerArea().unregisterActor(this);
            }
        }
    }

    protected abstract void updateAlive(float deltaTime);

    @Override
    public void draw(Canvas canvas) {
        if (state == State.DYING) {
            vanishAnimation.draw(canvas);
        } else if (state == State.ALIVE) {
            if (displaysHealthBar()) health.draw(canvas);
            if (immunityCounter == 0 || immunityCounter % 2 == 0) drawAlive(canvas);
        }
    }

    protected abstract void drawAlive(Canvas canvas);

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean takeCellSpace() { return state == State.ALIVE; }

    @Override
    public boolean isCellInteractable() { return state == State.ALIVE; }

    @Override
    public boolean isViewInteractable() { return state == State.ALIVE; }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}
