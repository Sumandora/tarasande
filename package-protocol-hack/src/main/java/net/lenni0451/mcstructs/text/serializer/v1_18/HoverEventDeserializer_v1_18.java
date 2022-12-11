package net.lenni0451.mcstructs.text.serializer.v1_18;

import net.lenni0451.mcstructs.core.Identifier;
import net.lenni0451.mcstructs.nbt.snbt.SNbtSerializer;
import net.lenni0451.mcstructs.nbt.tags.CompoundNbt;
import net.lenni0451.mcstructs.text.ATextComponent;
import net.lenni0451.mcstructs.text.events.hover.AHoverEvent;
import net.lenni0451.mcstructs.text.events.hover.HoverEventAction;
import net.lenni0451.mcstructs.text.events.hover.impl.EntityHoverEvent;
import net.lenni0451.mcstructs.text.serializer.TextComponentSerializer;
import net.lenni0451.mcstructs.text.serializer.v1_16.HoverEventDeserializer_v1_16;

import java.util.UUID;

public class HoverEventDeserializer_v1_18 extends HoverEventDeserializer_v1_16 {

    public HoverEventDeserializer_v1_18(final TextComponentSerializer textComponentSerializer, final SNbtSerializer<?> sNbtSerializer) {
        super(textComponentSerializer, sNbtSerializer);
    }

    protected AHoverEvent deserializeLegacy(final HoverEventAction action, final ATextComponent text) {
        if (action == HoverEventAction.SHOW_ENTITY) {
            try {
                CompoundNbt rawEntity = (CompoundNbt) this.sNbtSerializer.deserialize(text.asString());
                ATextComponent name = this.textComponentSerializer.deserialize(rawEntity.getString("name"));
                Identifier entityType = Identifier.of(rawEntity.getString("type"));
                UUID uuid = UUID.fromString(rawEntity.getString("id"));
                return new EntityHoverEvent(action, entityType, uuid, name);
            } catch (Exception ignored) {
                return null;
            }
        }
        return super.deserializeLegacy(action, text);
    }

}
