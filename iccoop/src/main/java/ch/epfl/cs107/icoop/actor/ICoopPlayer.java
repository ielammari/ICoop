package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.KeyBindings;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.icoop.handler.ICoopInventory;
import ch.epfl.cs107.icoop.handler.ICoopPlayerStatusGUI;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.areagame.handler.Inventory;
import ch.epfl.cs107.play.areagame.handler.InventoryItem;
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

public class ICoopPlayer extends MovableAreaEntity implements ElementalEntity, Interactor, Inventory.Holder {

    private enum PlayerState { IDLE, SWORD_ATTACK, STAFF_ATTACK }

    private static final int ANIMATION_DURATION = 4;
    private static final int SWORD_ANIMATION_DURATION = 2;
    private static final int STAFF_ANIMATION_DURATION = 2;
    private static final int MAX_LIFE = 5;
    private static final int IMMUNITY_FRAMES = 24;
    private static final ICoopItem[] ITEM_ORDER = ICoopItem.values();

    private final KeyBindings.PlayerKeyBindings keys;
    private final Element element;
    private final OrientedAnimation animation;
    private final OrientedAnimation swordAnimation;
    private final OrientedAnimation staffAnimation;
    private final Health health;
    private final ICoopPlayerInteractionHandler interactionHandler;
    private final ICoopInventory inventory;
    private final ICoopPlayerStatusGUI gui;

    private int immunityCounter;
    private boolean immuneToFire;
    private boolean immuneToWater;
    private Door pendingDoor;
    private boolean pendingAreaReset;
    private ICoopItem currentItem;
    private PlayerState playerState;

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
        this.swordAnimation = new OrientedAnimation(spriteName + ".sword", SWORD_ANIMATION_DURATION, this,
                new Vector(-0.5f, 0f),
                new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT},
                4, 2, 2, 32, 32);
        String staffSprite = (element == Element.FIRE) ? "icoop/player.staff_fire" : "icoop/player2.staff_water";
        this.staffAnimation = new OrientedAnimation(staffSprite, STAFF_ANIMATION_DURATION, this,
                new Vector(-0.5f, -0.20f),
                new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT},
                4, 2, 2, 32, 32);
        this.health = new Health(this, Transform.I.translated(0, 1.75f), MAX_LIFE, true);
        this.immunityCounter = 0;
        this.immuneToFire = false;
        this.immuneToWater = false;
        this.pendingAreaReset = false;
        this.inventory = new ICoopInventory();
        inventory.addPocketItem(ICoopItem.SWORD, 1);
        inventory.addPocketItem(ICoopItem.EXPLOSIVE, 3);
        this.currentItem = ICoopItem.SWORD;
        this.gui = new ICoopPlayerStatusGUI(this, element == Element.WATER);
        this.playerState = PlayerState.IDLE;
        resetMotion();
    }

    public void takeDamage(DamageType type, int amount) {
        if (immunityCounter > 0) return;
        if (type == DamageType.FIRE && immuneToFire) return;
        if (type == DamageType.WATER && immuneToWater) return;
        health.decrease(amount);
        immunityCounter = IMMUNITY_FRAMES;
        if (health.isOff()) pendingAreaReset = true;
    }

    private void grantImmunity(Element e) {
        if (e == Element.FIRE) immuneToFire = true;
        else if (e == Element.WATER) immuneToWater = true;
    }

    public ICoopItem getCurrentItem() { return currentItem; }

    @Override
    public boolean possess(InventoryItem item) {
        return inventory.contains(item);
    }

    public boolean hasPendingAreaReset() { return pendingAreaReset; }
    public void consumePendingAreaReset() { pendingAreaReset = false; }

    public void resetStats() {
        health.resetHealth();
        immunityCounter = 0;
        pendingAreaReset = false;
        playerState = PlayerState.IDLE;
        swordAnimation.reset();
        staffAnimation.reset();
    }

    public boolean hasPendingDoor() { return pendingDoor != null; }

    public Door consumePendingDoor() {
        Door d = pendingDoor;
        pendingDoor = null;
        return d;
    }

    private void cycleToNextItem() {
        int idx = 0;
        for (int i = 0; i < ITEM_ORDER.length; i++) {
            if (ITEM_ORDER[i] == currentItem) { idx = i; break; }
        }
        for (int i = 1; i <= ITEM_ORDER.length; i++) {
            ICoopItem candidate = ITEM_ORDER[(idx + i) % ITEM_ORDER.length];
            if (inventory.contains(candidate)) {
                currentItem = candidate;
                return;
            }
        }
        currentItem = null;
    }

    private void updateCurrentItem() {
        if (currentItem != null && inventory.contains(currentItem)) return;
        for (ICoopItem item : ITEM_ORDER) {
            if (inventory.contains(item)) { currentItem = item; return; }
        }
        currentItem = null;
    }

    @Override
    public void update(float deltaTime) {
        if (immunityCounter > 0) immunityCounter--;
        Keyboard keyboard = getOwnerArea().getKeyboard();

        if (playerState == PlayerState.IDLE) {
            moveIfPressed(Orientation.UP, keyboard.get(keys.up()));
            moveIfPressed(Orientation.LEFT, keyboard.get(keys.left()));
            moveIfPressed(Orientation.DOWN, keyboard.get(keys.down()));
            moveIfPressed(Orientation.RIGHT, keyboard.get(keys.right()));
            if (isDisplacementOccurs()) {
                animation.update(deltaTime);
            } else {
                animation.reset();
            }
            updateCurrentItem();
            if (keyboard.get(keys.switchItem()).isPressed()) {
                cycleToNextItem();
            }
            if (keyboard.get(keys.useItem()).isPressed()) {
                if (currentItem == ICoopItem.SWORD) {
                    playerState = PlayerState.SWORD_ATTACK;
                    swordAnimation.reset();
                } else if (currentItem == ICoopItem.FIRE_STAFF || currentItem == ICoopItem.WATER_STAFF) {
                    playerState = PlayerState.STAFF_ATTACK;
                    staffAnimation.reset();
                } else if (currentItem == ICoopItem.EXPLOSIVE) {
                    Orientation cur = getOrientation();
                    Orientation[] ALL = Orientation.values();
                    Orientation[] tryOrder = {
                            cur,
                            ALL[(cur.ordinal() + 1) % 4],
                            ALL[(cur.ordinal() + 3) % 4],
                            ALL[(cur.ordinal() + 2) % 4]
                    };
                    DiscreteCoordinates myCell = getCurrentMainCellCoordinates();
                    for (Orientation dir : tryOrder) {
                        DiscreteCoordinates target = myCell.jump(dir.toVector());
                        Explosive exp = new Explosive(getOwnerArea(), cur, target);
                        if (getOwnerArea().canEnterAreaCells(exp, Collections.singletonList(target))) {
                            getOwnerArea().registerActor(exp);
                            exp.activate();
                            inventory.removePocketItem(ICoopItem.EXPLOSIVE, 1);
                            updateCurrentItem();
                            break;
                        }
                    }
                }
            }
        } else if (playerState == PlayerState.SWORD_ATTACK) {
            swordAnimation.update(deltaTime);
            if (swordAnimation.isCompleted()) {
                playerState = PlayerState.IDLE;
                swordAnimation.reset();
            }
        } else if (playerState == PlayerState.STAFF_ATTACK) {
            staffAnimation.update(deltaTime);
            if (staffAnimation.isCompleted()) {
                DiscreteCoordinates ahead = getCurrentMainCellCoordinates().jump(getOrientation().toVector());
                MagicProjectile ball = new MagicProjectile(getOwnerArea(), getOrientation(), ahead, element);
                getOwnerArea().registerActor(ball);
                playerState = PlayerState.IDLE;
                staffAnimation.reset();
            }
        }

        super.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
        health.draw(canvas);
        if (immunityCounter == 0 || immunityCounter % 2 == 0) {
            if (playerState == PlayerState.SWORD_ATTACK) {
                swordAnimation.draw(canvas);
            } else if (playerState == PlayerState.STAFF_ATTACK) {
                staffAnimation.draw(canvas);
            } else {
                animation.draw(canvas);
            }
        }
        gui.draw(canvas);
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
        if (playerState == PlayerState.SWORD_ATTACK) return true;
        if (playerState == PlayerState.IDLE) {
            return currentItem != ICoopItem.EXPLOSIVE
                    && currentItem != ICoopItem.SWORD
                    && currentItem != ICoopItem.FIRE_STAFF
                    && currentItem != ICoopItem.WATER_STAFF
                    && getOwnerArea().getKeyboard().get(keys.useItem()).isPressed();
        }
        return false;
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
            if (isCellInteraction && explosive.isInactive()) {
                explosive.collect();
                inventory.addPocketItem(ICoopItem.EXPLOSIVE, 1);
                updateCurrentItem();
            } else if (!isCellInteraction && explosive.isInactive()) {
                explosive.activate();
            }
        }

        @Override
        public void interactWith(ElementalItem item, boolean isCellInteraction) {
            if (isCellInteraction && item.element() == element) {
                item.collect();
                grantImmunity(item.element());
            }
        }

        @Override
        public void interactWith(Staff staff, boolean isCellInteraction) {
            if (isCellInteraction && staff.element() == element) {
                staff.collect();
                inventory.addPocketItem(staff.getInventoryItem(), 1);
                updateCurrentItem();
            }
        }

        @Override
        public void interactWith(ICoopCollectable collectable, boolean isCellInteraction) {
            if (isCellInteraction) {
                collectable.collect();
                health.increase(1);
            }
        }

        @Override
        public void interactWith(PressurePlate plate, boolean isCellInteraction) {
            if (isCellInteraction) plate.activate();
        }

        @Override
        public void interactWith(Foe foe, boolean isCellInteraction) {
            if (isCellInteraction) {
                foe.dealContactDamageTo(ICoopPlayer.this);
            } else if (playerState == PlayerState.SWORD_ATTACK) {
                foe.takeDamage(DamageType.PHYSICAL, 1);
            }
        }

        @Override
        public void interactWith(Key key, boolean isCellInteraction) {
            if (isCellInteraction && key.element() == element) {
                key.collect();
                inventory.addPocketItem(key.getInventoryItem(), 1);
                updateCurrentItem();
            }
        }

        @Override
        public void interactWith(ManorDoor manorDoor, boolean isCellInteraction) {
            if (isCellInteraction) manorDoor.onContact();
        }
    }
}
