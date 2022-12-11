package net.lenni0451.mcstructs.text.events.hover.impl;

import net.lenni0451.mcstructs.core.Identifier;
import net.lenni0451.mcstructs.text.ATextComponent;
import net.lenni0451.mcstructs.text.events.hover.AHoverEvent;
import net.lenni0451.mcstructs.text.events.hover.HoverEventAction;

import java.util.Objects;
import java.util.UUID;

public class EntityHoverEvent extends AHoverEvent {

    private final Identifier entityType;
    private final UUID uuid;
    private final ATextComponent name;

    public EntityHoverEvent(final HoverEventAction action, final Identifier entityType, final UUID uuid, final ATextComponent name) {
        super(action);

        this.entityType = entityType;
        this.uuid = uuid;
        this.name = name;
    }

    public Identifier getEntityType() {
        return this.entityType;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public ATextComponent getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityHoverEvent that = (EntityHoverEvent) o;
        return getAction() == that.getAction() && Objects.equals(entityType, that.entityType) && Objects.equals(uuid, that.uuid) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAction(), entityType, uuid, name);
    }

    @Override
    public String toString() {
        return "EntityHoverEvent{action=" + getAction() + ", entityType=" + entityType + ", uuid=" + uuid + ", name=" + name + "}";
    }

}
