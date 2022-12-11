package net.lenni0451.mcstructs.text.events.hover.impl;

import net.lenni0451.mcstructs.text.ATextComponent;
import net.lenni0451.mcstructs.text.events.hover.AHoverEvent;
import net.lenni0451.mcstructs.text.events.hover.HoverEventAction;

import java.util.Objects;

public class TextHoverEvent extends AHoverEvent {

    private final ATextComponent text;

    public TextHoverEvent(final HoverEventAction action, final ATextComponent text) {
        super(action);

        this.text = text;
    }

    public ATextComponent getText() {
        return this.text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextHoverEvent that = (TextHoverEvent) o;
        return getAction() == that.getAction() && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAction(), text);
    }

    @Override
    public String toString() {
        return "TextHoverEvent{action=" + getAction() + ", text=" + text + "}";
    }

}
