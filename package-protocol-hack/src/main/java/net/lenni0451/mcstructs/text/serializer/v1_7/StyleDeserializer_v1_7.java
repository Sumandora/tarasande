package net.lenni0451.mcstructs.text.serializer.v1_7;

import com.google.gson.*;
import net.lenni0451.mcstructs.core.TextFormatting;
import net.lenni0451.mcstructs.text.ATextComponent;
import net.lenni0451.mcstructs.text.Style;
import net.lenni0451.mcstructs.text.events.click.ClickEvent;
import net.lenni0451.mcstructs.text.events.click.ClickEventAction;
import net.lenni0451.mcstructs.text.events.hover.HoverEventAction;
import net.lenni0451.mcstructs.text.events.hover.impl.TextHoverEvent;

import java.lang.reflect.Type;

public class StyleDeserializer_v1_7 implements JsonDeserializer<Style> {

    @Override
    public Style deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            JsonObject rawStyle = json.getAsJsonObject();
            Style style = new Style();

            if (rawStyle.has("bold")) style.setBold(rawStyle.get("bold").getAsBoolean());
            if (rawStyle.has("italic")) style.setItalic(rawStyle.get("italic").getAsBoolean());
            if (rawStyle.has("underlined")) style.setUnderlined(rawStyle.get("underlined").getAsBoolean());
            if (rawStyle.has("strikethrough")) style.setStrikethrough(rawStyle.get("strikethrough").getAsBoolean());
            if (rawStyle.has("obfuscated")) style.setObfuscated(rawStyle.get("obfuscated").getAsBoolean());
            if (rawStyle.has("color")) style.setFormatting(TextFormatting.getByName(rawStyle.get("color").getAsString()));
            if (rawStyle.has("clickEvent")) {
                JsonObject rawClickEvent = rawStyle.getAsJsonObject("clickEvent");
                if (rawClickEvent != null) {
                    JsonPrimitive rawAction = rawClickEvent.getAsJsonPrimitive("action");
                    JsonPrimitive rawValue = rawClickEvent.getAsJsonPrimitive("value");

                    ClickEventAction action = null;
                    String value = null;
                    if (rawAction != null) action = ClickEventAction.getByName(rawAction.getAsString());
                    if (rawValue != null) value = rawValue.getAsString();

                    if (action != null && value != null && action.isUserDefinable()) style.setClickEvent(new ClickEvent(action, value));
                }
            }
            if (rawStyle.has("hoverEvent")) {
                JsonObject rawHoverEvent = rawStyle.getAsJsonObject("hoverEvent");
                if (rawHoverEvent != null) {
                    JsonPrimitive rawAction = rawHoverEvent.getAsJsonPrimitive("action");

                    HoverEventAction action = null;
                    ATextComponent value = context.deserialize(rawHoverEvent.get("value"), ATextComponent.class);
                    if (rawAction != null) action = HoverEventAction.getByName(rawAction.getAsString());

                    if (action != null && value != null && action.isUserDefinable()) style.setHoverEvent(new TextHoverEvent(action, value));
                }
            }
            return style;
        }
        return null;
    }

}
