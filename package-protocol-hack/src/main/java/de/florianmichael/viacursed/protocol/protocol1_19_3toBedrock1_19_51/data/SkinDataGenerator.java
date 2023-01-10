package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.storage.BedrockSessionStorage;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class SkinDataGenerator {
    public static final String skinGeometryData = loadSkinPart("default_geometry_data.b64");
    public static final String skinMetaData = loadSkinPart("default_skindata.b64");

    public static JsonObject generateSkinTileData(final UserConnection connection) {
        final BedrockSessionStorage bedrockSessionStorage = connection.get(BedrockSessionStorage.class);
        if (bedrockSessionStorage == null) return new JsonObject();

        final JsonObject skinData = new JsonObject();

        skinData.add("AnimatedImageData", new JsonArray());
        skinData.addProperty("ArmSize", "");
        skinData.addProperty("CapeData", "");
        skinData.addProperty("CapeId", "");
        skinData.addProperty("CapeImageHeight", 0);
        skinData.addProperty("CapeImageWidth", 0);
        skinData.addProperty("CapeOnClassicSkin", false);
        skinData.addProperty("ClientRandomId", new Random().nextLong());
        skinData.addProperty("CurrentInputMode", 1);
        skinData.addProperty("DefaultInputMode", 1);
        skinData.addProperty("DeviceId", UUID.randomUUID().toString());
        skinData.addProperty("DeviceModel", "");
        skinData.addProperty("DeviceOS", 7);//windows 10?
        skinData.addProperty("GameVersion", BedrockSessionStorage.CODEC.getMinecraftVersion());
        skinData.addProperty("GuiScale", 0);
        skinData.addProperty("LanguageCode", "en_US");
        skinData.add("PersonaPieces", new JsonArray());
        skinData.addProperty("PersonaSkin", false);
        skinData.add("PieceTintColors", new JsonArray());
        skinData.addProperty("PlatformOfflineId", "");
        skinData.addProperty("PlatformOnlineId", "");
        skinData.addProperty("PremiumSkin", false);
        skinData.addProperty("SelfSignedId", UUID.randomUUID().toString());
        skinData.addProperty("ServerAddress", bedrockSessionStorage.targetAddress.getHostString());
        skinData.addProperty("SkinAnimationData", "");
        skinData.addProperty("SkinColor", "#0");
        skinData.addProperty("SkinGeometryData", skinGeometryData);
        skinData.addProperty("SkinData", skinMetaData);
        skinData.addProperty("SkinId", UUID.randomUUID() + ".Custom" + UUID.randomUUID());//ok..? :shrug:
        skinData.addProperty("SkinImageHeight", 64);
        skinData.addProperty("SkinImageWidth", 64);
        skinData.addProperty("SkinResourcePatch", "ewogICAiZ2VvbWV0cnkiIDogewogICAgICAiZGVmYXVsdCIgOiAiZ2VvbWV0cnkuaHVtYW5vaWQuY3VzdG9tIgogICB9Cn0K");
        skinData.addProperty("ThirdPartyName", connection.getProtocolInfo().getUsername());
        skinData.addProperty("ThirdPartyNameOnly", false);
        skinData.addProperty("UIProfile", 0);

        return skinData;
    }

    private static String loadSkinPart(final String path) {
        try {
            return new String(IOUtils.toByteArray(Objects.requireNonNull(SkinDataGenerator.class.getResourceAsStream("/assets/viacursed/" + path))), StandardCharsets.US_ASCII);
        } catch (IOException e) {
            return null;
        }
    }
}
