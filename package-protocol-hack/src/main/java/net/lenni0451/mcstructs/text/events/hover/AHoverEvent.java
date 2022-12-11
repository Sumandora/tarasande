package net.lenni0451.mcstructs.text.events.hover;

public abstract class AHoverEvent {

    private final HoverEventAction action;

    public AHoverEvent(final HoverEventAction action) {
        this.action = action;
    }

    public HoverEventAction getAction() {
        return this.action;
    }

    @Override
    public abstract boolean equals(final Object o);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();

}
