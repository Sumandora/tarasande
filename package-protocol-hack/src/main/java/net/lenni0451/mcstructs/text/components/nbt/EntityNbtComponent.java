package net.lenni0451.mcstructs.text.components.nbt;

import net.lenni0451.mcstructs.text.ATextComponent;
import net.lenni0451.mcstructs.text.components.NbtComponent;

import java.util.Objects;

public class EntityNbtComponent extends NbtComponent {

    private final String selector;

    public EntityNbtComponent(final String component, final boolean resolve, final ATextComponent separator, final String selector) {
        super(component, resolve, separator);
        this.selector = selector;
    }

    public String getSelector() {
        return this.selector;
    }

    @Override
    public ATextComponent copy() {
        if (this.getSeparator() == null) return this.putMetaCopy(new EntityNbtComponent(this.getComponent(), this.isResolve(), null, this.selector));
        else return this.putMetaCopy(new EntityNbtComponent(this.getComponent(), this.isResolve(), this.getSeparator(), this.selector));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityNbtComponent that = (EntityNbtComponent) o;
        return Objects.equals(getSiblings(), that.getSiblings()) && Objects.equals(getStyle(), that.getStyle()) && Objects.equals(selector, that.selector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSiblings(), getStyle(), selector);
    }

    @Override
    public String toString() {
        return "EntityNbtComponent{" +
                "siblings=" + getSiblings() +
                ", style=" + getStyle() +
                ", selector='" + selector + '\'' +
                '}';
    }

}
