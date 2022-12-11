package net.lenni0451.mcstructs.text.serializer.v1_16;

import com.google.gson.*;
import net.lenni0451.mcstructs.core.Identifier;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.exceptions.SNbtDeserializeException;
import net.lenni0451.mcstructs.nbt.snbt.SNbtSerializer;
import net.lenni0451.mcstructs.nbt.tags.CompoundNbt;
import net.lenni0451.mcstructs.text.ATextComponent;
import net.lenni0451.mcstructs.text.events.hover.AHoverEvent;
import net.lenni0451.mcstructs.text.events.hover.HoverEventAction;
import net.lenni0451.mcstructs.text.events.hover.impl.EntityHoverEvent;
import net.lenni0451.mcstructs.text.events.hover.impl.ItemHoverEvent;
import net.lenni0451.mcstructs.text.events.hover.impl.TextHoverEvent;
import net.lenni0451.mcstructs.text.serializer.TextComponentJsonUtils;
import net.lenni0451.mcstructs.text.serializer.TextComponentSerializer;

import java.lang.reflect.Type;
import java.util.UUID;

import static net.lenni0451.mcstructs.text.serializer.TextComponentJsonUtils.getString;

public class HoverEventDeserializer_v1_16 implements JsonDeserializer<AHoverEvent> {

    protected final TextComponentSerializer textComponentSerializer;
    protected final SNbtSerializer<?> sNbtSerializer;

    public HoverEventDeserializer_v1_16(final TextComponentSerializer textComponentSerializer, final SNbtSerializer<?> sNbtSerializer) {
        this.textComponentSerializer = textComponentSerializer;
        this.sNbtSerializer = sNbtSerializer;
    }

    @Override
    public AHoverEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) return null;
        JsonObject rawHoverEvent = json.getAsJsonObject();
        if (rawHoverEvent == null) return null;

        String rawAction = getString(rawHoverEvent, "action", null);
        if (rawAction == null) return null;
        HoverEventAction action = HoverEventAction.getByName(rawAction);
        if (action == null) return null;
        JsonElement rawContents = rawHoverEvent.get("contents");
        if (rawContents != null) return this.deserialize(action, rawContents);
        ATextComponent text = this.textComponentSerializer.deserialize(rawHoverEvent.get("value"));
        if (text == null) return null;
        return this.deserializeLegacy(action, text);
    }

    protected AHoverEvent deserialize(final HoverEventAction action, final JsonElement contents) {
        switch (action) {
            case SHOW_TEXT:
                return new TextHoverEvent(action, this.textComponentSerializer.deserialize(contents));
            case SHOW_ITEM:
                if (contents.isJsonPrimitive()) return new ItemHoverEvent(action, Identifier.of(contents.getAsString()), 1, null);
                JsonObject rawItem = TextComponentJsonUtils.getJsonObject(contents, "item");
                Identifier item = Identifier.of(getString(rawItem, "id"));
                int count = TextComponentJsonUtils.getInt(rawItem, "count", 1);
                if (rawItem.has("tag")) {
                    String rawTag = getString(rawItem, "tag");
                    return new ItemHoverEvent(action, item, count, (CompoundNbt) this.sNbtSerializer.tryDeserialize(rawTag));
                }
                return new ItemHoverEvent(action, item, count, null);
            case SHOW_ENTITY:
                if (!contents.isJsonObject()) return null;
                JsonObject rawEntity = contents.getAsJsonObject();
                Identifier entityType = Identifier.of(getString(rawEntity, "type"));
                UUID uuid = UUID.fromString(getString(rawEntity, "id"));
                ATextComponent name = this.textComponentSerializer.deserialize(rawEntity.get("name"));
                return new EntityHoverEvent(action, entityType, uuid, name);
            default:
                return null;
        }
    }

    protected AHoverEvent deserializeLegacy(final HoverEventAction action, final ATextComponent text) {
        switch (action) {
            case SHOW_TEXT:
                return new TextHoverEvent(action, text);
            case SHOW_ITEM:
                CompoundNbt rawTag = (CompoundNbt) this.sNbtSerializer.tryDeserialize(text.asString());
                if (rawTag == null) return null;
                Identifier id = Identifier.of(rawTag.getString("id"));
                int count = rawTag.getByte("count");
                CompoundNbt tag = null;
                if (rawTag.contains("tag", NbtType.COMPOUND)) tag = rawTag.getCompound("tag");
                return new ItemHoverEvent(action, id, count, tag);
            case SHOW_ENTITY:
                try {
                    CompoundNbt rawEntity = (CompoundNbt) this.sNbtSerializer.deserialize(text.asString());
                    ATextComponent name = this.textComponentSerializer.deserialize(rawEntity.getString("name"));
                    Identifier entityType = Identifier.of(rawEntity.getString("type"));
                    UUID uuid = UUID.fromString(rawEntity.getString("id"));
                    return new EntityHoverEvent(action, entityType, uuid, name);
                } catch (SNbtDeserializeException | JsonSyntaxException ignored) {
                    return null;
                }
            default:
                return null;
        }
    }

}
