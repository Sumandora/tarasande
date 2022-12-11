package net.lenni0451.mcstructs.text.events.hover;

public enum HoverEventAction {

    SHOW_TEXT("show_text", true),
    SHOW_ACHIEVEMENT("show_achievement", true),
    SHOW_ITEM("show_item", true),
    SHOW_ENTITY("show_entity", true);

    public static HoverEventAction getByName(final String name) {
        for (HoverEventAction hoverEventAction : values()) {
            if (hoverEventAction.getName().equalsIgnoreCase(name)) return hoverEventAction;
        }
        return null;
    }


    private final String name;
    private final boolean userDefinable;

    HoverEventAction(final String name, final boolean userDefinable) {
        this.name = name;
        this.userDefinable = userDefinable;
    }

    public String getName() {
        return this.name;
    }

    public boolean isUserDefinable() {
        return this.userDefinable;
    }

}