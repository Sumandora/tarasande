package net.lenni0451.mcstructs.text.components.nbt;

import net.lenni0451.mcstructs.core.Identifier;
import net.lenni0451.mcstructs.text.ATextComponent;
import net.lenni0451.mcstructs.text.components.NbtComponent;

import java.util.Objects;

public class StorageNbtComponent extends NbtComponent {

    private final Identifier id;

    public StorageNbtComponent(final String component, final boolean resolve, final ATextComponent separator, final Identifier id) {
        super(component, resolve, separator);
        this.id = id;
    }

    public Identifier getId() {
        return this.id;
    }

    @Override
    public ATextComponent copy() {
        if (this.getSeparator() == null) return this.putMetaCopy(new StorageNbtComponent(this.getComponent(), this.isResolve(), null, this.id));
        else return this.putMetaCopy(new StorageNbtComponent(this.getComponent(), this.isResolve(), this.getSeparator(), this.id));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StorageNbtComponent that = (StorageNbtComponent) o;
        return Objects.equals(getSiblings(), that.getSiblings()) && Objects.equals(getStyle(), that.getStyle()) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSiblings(), getStyle(), id);
    }

    @Override
    public String toString() {
        return "StorageNbtComponent{" +
                "siblings=" + getSiblings() +
                ", style=" + getStyle() +
                ", id=" + id +
                '}';
    }

}
