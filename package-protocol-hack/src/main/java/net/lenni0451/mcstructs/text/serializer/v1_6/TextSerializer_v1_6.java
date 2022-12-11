package net.lenni0451.mcstructs.text.serializer.v1_6;

import com.google.gson.*;
import net.lenni0451.mcstructs.text.ATextComponent;
import net.lenni0451.mcstructs.text.Style;
import net.lenni0451.mcstructs.text.components.StringComponent;
import net.lenni0451.mcstructs.text.components.TranslationComponent;

import java.lang.reflect.Type;

public class TextSerializer_v1_6 implements JsonSerializer<ATextComponent> {

    @Override
    public JsonElement serialize(ATextComponent src, Type typeOfSrc, JsonSerializationContext context) {
        Style style = src.getStyle();
        JsonObject component = new JsonObject();

        if (style != null) {
            if (style.getColor() != null && !style.getColor().isRGBColor()) component.addProperty("color", style.getColor().serialize());
            if (style.getBold() != null) component.addProperty("bold", style.isBold());
            if (style.getItalic() != null) component.addProperty("italic", style.isItalic());
            if (style.getUnderlined() != null) component.addProperty("underlined", style.isUnderlined());
            if (style.getObfuscated() != null) component.addProperty("obfuscated", style.isObfuscated());
        }

        if (src instanceof StringComponent) {
            StringComponent stringComponent = (StringComponent) src;
            if (stringComponent.getSiblings().isEmpty()) {
                component.addProperty("text", stringComponent.getText());
            } else {
                JsonArray text = new JsonArray();
                text.add(stringComponent.getText());
                for (ATextComponent sibling : stringComponent.getSiblings()) {
                    if (sibling instanceof StringComponent && sibling.getStyle().isEmpty() && sibling.getSiblings().isEmpty()) text.add(((StringComponent) sibling).getText());
                    else text.add(this.serialize(sibling, typeOfSrc, context));
                }
                component.add("text", text);
            }
        } else if (src instanceof TranslationComponent) {
            TranslationComponent translationComponent = (TranslationComponent) src;
            component.addProperty("translate", translationComponent.getKey());

            Object[] args = translationComponent.getArgs();
            if (args != null && args.length > 0) {
                if (args.length == 1 && args[0] instanceof String) {
                    component.addProperty("using", (String) args[0]);
                } else {
                    JsonArray using = new JsonArray();
                    for (Object arg : args) {
                        if (arg instanceof String) {
                            using.add((String) arg);
                        } else if (arg instanceof Boolean) {
                            using.add((Boolean) arg);
                        } else if (arg instanceof Character) {
                            using.add((Character) arg);
                        } else if (arg instanceof Number) {
                            using.add((Number) arg);
                        } else if (arg instanceof StringComponent) {
                            StringComponent stringComponent = (StringComponent) arg;
                            if ((stringComponent.getStyle() == null || stringComponent.getStyle().isEmpty()) && stringComponent.getSiblings().isEmpty()) {
                                using.add(stringComponent.getText());
                            } else {
                                using.add(this.serialize(stringComponent, typeOfSrc, context));
                            }
                        } else if (arg instanceof TranslationComponent) {
                            using.add(this.serialize((TranslationComponent) arg, typeOfSrc, context));
                        } else {
                            throw new IllegalArgumentException("Minecraft 1.9 did not support translation arguments of type " + arg.getClass().getName());
                        }
                    }
                    component.add("using", using);
                }
            }
        }

        return component;
    }

}
