package net.lenni0451.mcstructs.text.serializer.v1_16;

import com.google.gson.*;
import net.lenni0451.mcstructs.text.ATextComponent;
import net.lenni0451.mcstructs.text.components.*;
import net.lenni0451.mcstructs.text.components.nbt.BlockNbtComponent;
import net.lenni0451.mcstructs.text.components.nbt.EntityNbtComponent;
import net.lenni0451.mcstructs.text.components.nbt.StorageNbtComponent;

import java.lang.reflect.Type;
import java.util.Map;

public class TextSerializer_v1_16 implements JsonSerializer<ATextComponent> {

    @Override
    public JsonElement serialize(ATextComponent src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject serializedComponent = new JsonObject();

        if (!src.getStyle().isEmpty()) {
            JsonElement serializedStyle = context.serialize(src.getStyle());
            if (serializedStyle.isJsonObject()) {
                JsonObject serializedStyleObject = serializedStyle.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : serializedStyleObject.entrySet()) serializedComponent.add(entry.getKey(), entry.getValue());
            }
        }
        if (!src.getSiblings().isEmpty()) {
            JsonArray siblings = new JsonArray();
            for (ATextComponent sibling : src.getSiblings()) siblings.add(this.serialize(sibling, sibling.getClass(), context));
            serializedComponent.add("extra", siblings);
        }

        if (src instanceof StringComponent) {
            serializedComponent.addProperty("text", ((StringComponent) src).getText());
        } else if (src instanceof TranslationComponent) {
            TranslationComponent translationComponent = (TranslationComponent) src;
            serializedComponent.addProperty("translate", translationComponent.getKey());
            if (translationComponent.getArgs().length > 0) {
                JsonArray with = new JsonArray();
                Object[] args = translationComponent.getArgs();
                for (Object arg : args) {
                    if (arg instanceof ATextComponent) with.add(this.serialize((ATextComponent) arg, arg.getClass(), context));
                    else with.add(new JsonPrimitive(String.valueOf(arg)));
                }
                serializedComponent.add("with", with);
            }
        } else if (src instanceof ScoreComponent) {
            ScoreComponent scoreComponent = (ScoreComponent) src;
            JsonObject serializedScore = new JsonObject();
            serializedScore.addProperty("name", scoreComponent.getName());
            serializedScore.addProperty("objective", scoreComponent.getObjective());
            serializedComponent.add("score", serializedScore);
        } else if (src instanceof SelectorComponent) {
            serializedComponent.addProperty("selector", ((SelectorComponent) src).getSelector());
        } else if (src instanceof KeybindComponent) {
            serializedComponent.addProperty("keybind", ((KeybindComponent) src).getKeybind());
        } else if (src instanceof NbtComponent) {
            NbtComponent nbtComponent = (NbtComponent) src;
            serializedComponent.addProperty("nbt", nbtComponent.getComponent());
            serializedComponent.addProperty("resolve", nbtComponent.isResolve());
            if (nbtComponent instanceof BlockNbtComponent) serializedComponent.addProperty("block", ((BlockNbtComponent) nbtComponent).getPos());
            else if (nbtComponent instanceof EntityNbtComponent) serializedComponent.addProperty("entity", ((EntityNbtComponent) nbtComponent).getSelector());
            else if (nbtComponent instanceof StorageNbtComponent) serializedComponent.addProperty("storage", ((StorageNbtComponent) nbtComponent).getId().toString());
            else throw new JsonParseException("Don't know how to serialize " + src + " as a Component");
        } else {
            throw new JsonParseException("Don't know how to serialize " + src + " as a Component");
        }

        return serializedComponent;
    }

}
