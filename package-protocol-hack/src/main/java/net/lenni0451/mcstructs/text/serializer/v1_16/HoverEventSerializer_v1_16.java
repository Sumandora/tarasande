package net.lenni0451.mcstructs.text.serializer.v1_16;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.lenni0451.mcstructs.text.events.hover.AHoverEvent;
import net.lenni0451.mcstructs.text.events.hover.impl.EntityHoverEvent;
import net.lenni0451.mcstructs.text.events.hover.impl.ItemHoverEvent;
import net.lenni0451.mcstructs.text.events.hover.impl.TextHoverEvent;
import net.lenni0451.mcstructs.text.serializer.TextComponentSerializer;

import java.lang.reflect.Type;

public class HoverEventSerializer_v1_16 implements JsonSerializer<AHoverEvent> {

    private final TextComponentSerializer textComponentSerializer;

    public HoverEventSerializer_v1_16(final TextComponentSerializer textComponentSerializer) {
        this.textComponentSerializer = textComponentSerializer;
    }

    @Override
    public JsonElement serialize(AHoverEvent src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject serializedHoverEvent = new JsonObject();

        serializedHoverEvent.addProperty("action", src.getAction().getName());
        if (src instanceof TextHoverEvent) {
            TextHoverEvent textHoverEvent = (TextHoverEvent) src;
            serializedHoverEvent.add("contents", this.textComponentSerializer.serializeJson(textHoverEvent.getText()));
        } else if (src instanceof ItemHoverEvent) {
            ItemHoverEvent itemHoverEvent = (ItemHoverEvent) src;
            JsonObject serializedItem = new JsonObject();
            serializedItem.addProperty("id", itemHoverEvent.getItem().toString());
            if (itemHoverEvent.getCount() != 1) serializedItem.addProperty("count", itemHoverEvent.getCount());
            if (itemHoverEvent.getNbt() != null) serializedItem.addProperty("tag", itemHoverEvent.getNbt().toString());
            serializedHoverEvent.add("contents", serializedItem);
        } else if (src instanceof EntityHoverEvent) {
            EntityHoverEvent entityHoverEvent = (EntityHoverEvent) src;
            JsonObject serializedEntity = new JsonObject();
            serializedEntity.addProperty("type", entityHoverEvent.getEntityType().toString());
            serializedEntity.addProperty("id", entityHoverEvent.getUuid().toString());
            if (entityHoverEvent.getName() != null) serializedEntity.add("name", this.textComponentSerializer.serializeJson(entityHoverEvent.getName()));
            serializedHoverEvent.add("contents", serializedEntity);
        }

        return serializedHoverEvent;
    }

}
