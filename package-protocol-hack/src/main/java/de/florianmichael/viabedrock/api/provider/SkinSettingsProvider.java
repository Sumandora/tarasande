package de.florianmichael.viabedrock.api.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.providers.Provider;
import de.florianmichael.viabedrock.api.BedrockProtocols;
import de.florianmichael.viabedrock.protocol.storage.BedrockSessionStorage;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class SkinSettingsProvider implements Provider {

    public static final String SKIN_GEOMETRY_DATA = loadSkinTilePart("default_geometry_data.b64");
    public static final String SKIN_META_DATA = loadSkinTilePart("default_skindata.b64");

    public JsonObject generateSkinTileData(final UserConnection connection) {
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
        skinData.addProperty("GameVersion", BedrockProtocols.CODEC.getMinecraftVersion());
        skinData.addProperty("GuiScale", 0);
        skinData.addProperty("LanguageCode", "en_US");
        skinData.add("PersonaPieces", new JsonArray());
        skinData.addProperty("PersonaSkin", false);
        skinData.add("PieceTintColors", new JsonArray());
        skinData.addProperty("PlatformOfflineId", "");
        skinData.addProperty("PlatformOnlineId", "");
        skinData.addProperty("PremiumSkin", false);
        skinData.addProperty("SelfSignedId", UUID.randomUUID().toString());
        skinData.addProperty("ServerAddress", bedrockSessionStorage.getTargetAddress().getHostString());
        skinData.addProperty("SkinAnimationData", "");
        skinData.addProperty("SkinColor", "#0");
        skinData.addProperty("SkinGeometryData", SKIN_GEOMETRY_DATA);
        skinData.addProperty("SkinData", SKIN_META_DATA);
        skinData.addProperty("SkinId", UUID.randomUUID() + ".Custom" + UUID.randomUUID());
        skinData.addProperty("SkinImageHeight", 64);
        skinData.addProperty("SkinImageWidth", 64);
        skinData.addProperty("SkinResourcePatch", "ewogICAiZ2VvbWV0cnkiIDogewogICAgICAiZGVmYXVsdCIgOiAiZ2VvbWV0cnkuaHVtYW5vaWQuY3VzdG9tIgogICB9Cn0K");
        skinData.addProperty("ThirdPartyName", connection.getProtocolInfo().getUsername());
        skinData.addProperty("ThirdPartyNameOnly", false);
        skinData.addProperty("UIProfile", 0);

        return skinData;
    }

    private static String loadSkinTilePart(final String path) {
        try {
            return new String(IOUtils.toByteArray(Objects.requireNonNull(SkinSettingsProvider.class.getResourceAsStream("/assets/viabedrock/" + path))), StandardCharsets.US_ASCII);
        } catch (IOException e) {
            return null;
        }
    }
}
