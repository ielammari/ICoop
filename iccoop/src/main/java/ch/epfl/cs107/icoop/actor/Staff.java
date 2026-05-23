package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class Staff extends ElementalItem {

    private static final int ANIMATION_DURATION = 32;
    private static final int NB_FRAMES = 8;
    private static final int FRAME_SIZE = 32;

    private final Animation animation;
    private final ICoopItem inventoryItem;

    public Staff(Area owner, Orientation orientation, DiscreteCoordinates position, Element element) {
        super(owner, orientation, position, element);
        String spriteName = (element == Element.FIRE) ? "icoop/staff_fire" : "icoop/staff_water";
        this.inventoryItem = (element == Element.FIRE) ? ICoopItem.FIRE_STAFF : ICoopItem.WATER_STAFF;
        Sprite[] sprites = new Sprite[NB_FRAMES];
        for (int i = 0; i < NB_FRAMES; i++) {
            sprites[i] = new RPGSprite(spriteName, 2f, 2f, this,
                    new RegionOfInterest(i * FRAME_SIZE, 0, FRAME_SIZE, FRAME_SIZE),
                    new Vector(-0.5f, 0f));
        }
        this.animation = new Animation(ANIMATION_DURATION / NB_FRAMES, sprites, true);
    }

    public ICoopItem getInventoryItem() { return inventoryItem; }

    @Override
    public void update(float deltaTime) {
        animation.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}
