package net.lenni0451.mcstructs.text;

import net.lenni0451.mcstructs.text.components.StringComponent;

import java.util.ArrayList;
import java.util.List;

public abstract class ATextComponent {

    private final List<ATextComponent> siblings = new ArrayList<>();
    private Style style = new Style();

    public void append(final String s) {
        this.append(new StringComponent(s));
    }

    public void append(final ATextComponent component) {
        component.getStyle().setParent(this.style);
        this.siblings.add(component);
    }

    public List<ATextComponent> getSiblings() {
        return this.siblings;
    }

    public Style getStyle() {
        return this.style;
    }

    public void setStyle(final Style style) {
        this.style = style;
        for (ATextComponent sibling : this.siblings) sibling.getStyle().setParent(this.style);
    }

    public <C extends ATextComponent> C putMetaCopy(final C component) {
        component.setStyle(this.style.copy());
        for (ATextComponent sibling : this.siblings) component.append(sibling.copy());
        return component;
    }

    protected void appendSiblings(final StringBuilder out) {
        for (ATextComponent sibling : this.siblings) out.append(sibling.asString());
    }

    public abstract String asString();

    public abstract ATextComponent copy();

    public abstract boolean equals(final Object o);

    public abstract int hashCode();

    public abstract String toString();

}
