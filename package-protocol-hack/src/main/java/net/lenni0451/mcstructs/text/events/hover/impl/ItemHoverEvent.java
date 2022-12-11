package net.lenni0451.mcstructs.text.events.hover.impl;

import net.lenni0451.mcstructs.core.Identifier;
import net.lenni0451.mcstructs.nbt.tags.CompoundNbt;
import net.lenni0451.mcstructs.text.events.hover.AHoverEvent;
import net.lenni0451.mcstructs.text.events.hover.HoverEventAction;

import java.util.Objects;

public class ItemHoverEvent extends AHoverEvent {

    private final Identifier item;
    private final int count;
    private final CompoundNbt nbt;

    public ItemHoverEvent(final HoverEventAction action, final Identifier item, final int count, final CompoundNbt nbt) {
        super(action);

        this.item = item;
        this.count = count;
        this.nbt = nbt;
    }

    public Identifier getItem() {
        return this.item;
    }

    public int getCount() {
        return this.count;
    }

    public CompoundNbt getNbt() {
        return this.nbt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemHoverEvent that = (ItemHoverEvent) o;
        return count == that.count && getAction() == that.getAction() && Objects.equals(item, that.item) && Objects.equals(nbt, that.nbt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAction(), item, count, nbt);
    }

    @Override
    public String toString() {
        return "ItemHoverEvent{action=" + getAction() + ", item=" + item + ", count=" + count + ", nbt=" + nbt + "}";
    }

}
