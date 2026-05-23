package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public class Key extends ElementalItem {

    private final Sprite sprite;
    private final ICoopItem inventoryItem;

    public Key(Area owner, Orientation orientation, DiscreteCoordinates position, Element element) {
        super(owner, orientation, position, element);
        String spriteName = (element == Element.FIRE) ? "icoop/key_red" : "icoop/key_blue";
        this.inventoryItem = (element == Element.FIRE) ? ICoopItem.FIRE_KEY : ICoopItem.WATER_KEY;
        this.sprite = new RPGSprite(spriteName, 1f, 1f, this);
    }

    public ICoopItem getInventoryItem() { return inventoryItem; }

    @Override
    public void draw(Canvas canvas) {
        if (isOff()) sprite.draw(canvas);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}
