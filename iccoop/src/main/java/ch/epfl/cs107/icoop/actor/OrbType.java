package ch.epfl.cs107.icoop.actor;

public enum OrbType {
    FIRE("orb_fire_msg", 64, Element.FIRE),
    WATER("orb_water_msg", 0, Element.WATER);

    private final String dialogName;
    private final int spriteYDelta;
    private final Element element;

    OrbType(String dialogName, int spriteYDelta, Element element) {
        this.dialogName = dialogName;
        this.spriteYDelta = spriteYDelta;
        this.element = element;
    }

    public String dialogName() { return dialogName; }
    public int spriteYDelta() { return spriteYDelta; }
    public Element element() { return element; }
}
