package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.play.areagame.handler.InventoryItem;

public enum ICoopItem implements InventoryItem {
    SWORD("icoop/sword.icon", 16, 16),
    FIRE_KEY("icoop/key_red", 16, 16),
    WATER_KEY("icoop/key_blue", 16, 16),
    FIRE_STAFF("icoop/staff_fire.icon", 16, 16),
    WATER_STAFF("icoop/staff_water.icon", 16, 16),
    EXPLOSIVE("icoop/explosive", 16, 16);

    private final String iconName;
    private final int iconW;
    private final int iconH;

    ICoopItem(String iconName, int iconW, int iconH) {
        this.iconName = iconName;
        this.iconW = iconW;
        this.iconH = iconH;
    }

    public String getIconName() { return iconName; }
    public int getIconW() { return iconW; }
    public int getIconH() { return iconH; }

    @Override
    public int getPocketId() { return 0; }

    @Override
    public String getName() { return name(); }
}
