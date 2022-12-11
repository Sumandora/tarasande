package net.lenni0451.mcstructs.text.components.nbt;

import net.lenni0451.mcstructs.text.ATextComponent;
import net.lenni0451.mcstructs.text.components.NbtComponent;

import java.util.Objects;

public class BlockNbtComponent extends NbtComponent {

    private final String pos;

    public BlockNbtComponent(final String rawComponent, final boolean resolve, final ATextComponent separator, final String pos) {
        super(rawComponent, resolve, separator);
        this.pos = pos;
    }

    public String getPos() {
        return this.pos;
    }

    @Override
    public ATextComponent copy() {
        if (this.getSeparator() == null) return this.putMetaCopy(new BlockNbtComponent(this.getComponent(), this.isResolve(), this.getSeparator(), this.pos));
        else return this.putMetaCopy(new BlockNbtComponent(this.getComponent(), this.isResolve(), this.getSeparator().copy(), this.pos));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockNbtComponent that = (BlockNbtComponent) o;
        return Objects.equals(getSiblings(), that.getSiblings()) && Objects.equals(getStyle(), that.getStyle()) && Objects.equals(pos, that.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSiblings(), getStyle(), pos);
    }

    @Override
    public String toString() {
        return "BlockNbtComponent{" +
                "siblings=" + getSiblings() +
                ", style=" + getStyle() +
                ", pos='" + pos + '\'' +
                '}';
    }

}
