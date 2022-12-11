package net.lenni0451.mcstructs.text.serializer.v1_7;

import com.google.gson.*;
import net.lenni0451.mcstructs.text.ATextComponent;
import net.lenni0451.mcstructs.text.Style;
import net.lenni0451.mcstructs.text.components.StringComponent;
import net.lenni0451.mcstructs.text.components.TranslationComponent;

import java.lang.reflect.Type;

public class TextDeserializer_v1_7 implements JsonDeserializer<ATextComponent> {

    @Override
    public ATextComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            return new StringComponent(json.getAsString());
        } else if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            ATextComponent component = null;

            for (JsonElement element : array) {
                ATextComponent serializedElement = this.deserialize(element, element.getClass(), context);
                if (component == null) component = serializedElement;
                else component.append(serializedElement);
            }

            return component;
        } else if (json.isJsonObject()) {
            JsonObject rawComponent = json.getAsJsonObject();
            ATextComponent component;

            if (rawComponent.has("text")) {
                component = new StringComponent(rawComponent.get("text").getAsString());
            } else if (rawComponent.has("translate")) {
                String translate = rawComponent.get("translate").getAsString();
                if (rawComponent.has("with")) {
                    JsonArray with = rawComponent.getAsJsonArray("with");
                    Object[] args = new Object[with.size()];
                    for (int i = 0; i < with.size(); i++) {
                        ATextComponent element = this.deserialize(with.get(i), typeOfT, context);
                        args[i] = element;
                        if (element instanceof StringComponent) {
                            StringComponent stringComponent = (StringComponent) element;
                            if (stringComponent.getStyle().isEmpty() && stringComponent.getSiblings().isEmpty()) args[i] = stringComponent.getText();
                        }
                    }
                    component = new TranslationComponent(translate, args);
                } else {
                    component = new TranslationComponent(translate);
                }
            } else {
                throw new JsonParseException("Don't know how to turn " + json + " into a Component");
            }

            if (rawComponent.has("extra")) {
                JsonArray extra = rawComponent.getAsJsonArray("extra");
                if (extra.isEmpty()) throw new JsonParseException("Unexpected empty array of components");
                for (JsonElement element : extra) component.append(this.deserialize(element, typeOfT, context));
            }

            Style newStyle = context.deserialize(rawComponent, Style.class);
            if (newStyle != null) component.setStyle(newStyle);
            return component;
        } else {
            throw new JsonParseException("Don't know how to turn " + json + " into a Component");
        }
    }

}
