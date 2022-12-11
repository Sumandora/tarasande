package net.lenni0451.mcstructs.text.serializer.v1_7;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.lenni0451.mcstructs.text.Style;
import net.lenni0451.mcstructs.text.events.hover.impl.TextHoverEvent;

import java.lang.reflect.Type;

public class StyleSerializer_v1_7 implements JsonSerializer<Style> {

    @Override
    public JsonElement serialize(Style src, Type typeOfSrc, JsonSerializationContext context) {
        if (src.isEmpty()) return null;

        JsonObject serializedStyle = new JsonObject();

        if (src.getBold() != null) serializedStyle.addProperty("bold", src.isBold());
        if (src.getItalic() != null) serializedStyle.addProperty("italic", src.isItalic());
        if (src.getUnderlined() != null) serializedStyle.addProperty("underlined", src.isUnderlined());
        if (src.getStrikethrough() != null) serializedStyle.addProperty("strikethrough", src.isStrikethrough());
        if (src.getObfuscated() != null) serializedStyle.addProperty("obfuscated", src.isObfuscated());
        if (src.getColor() != null && !src.getColor().isRGBColor()) serializedStyle.addProperty("color", src.getColor().serialize());
        if (src.getClickEvent() != null) {
            JsonObject clickEvent = new JsonObject();
            clickEvent.addProperty("action", src.getClickEvent().getAction().getName());
            clickEvent.addProperty("value", src.getClickEvent().getValue());
            serializedStyle.add("clickEvent", clickEvent);
        }
        if (src.getHoverEvent() instanceof TextHoverEvent) {
            JsonObject hoverEvent = new JsonObject();
            hoverEvent.addProperty("action", src.getHoverEvent().getAction().getName());
            hoverEvent.add("value", context.serialize(((TextHoverEvent) src.getHoverEvent()).getText()));
            serializedStyle.add("hoverEvent", hoverEvent);
        }

        return serializedStyle;
    }

}
