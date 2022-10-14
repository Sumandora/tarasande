package de.florianmichael.vialegacy.api.minecraft_util;

import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import de.florianmichael.vialegacy.ViaLegacy;

import java.util.logging.Level;

public class ChatUtil {

	public static String jsonToLegacy(String json) {
		if (json == null || json.equals("null") || json.isEmpty()) return "";
		try {
			String legacy = LegacyComponentSerializer.legacySection().serialize(ChatRewriter.HOVER_GSON_SERIALIZER.deserialize(json));
			while (legacy.startsWith("§f")) legacy = legacy.substring(2);
			return legacy;
		} catch (Exception ex) {
			ViaLegacy.getLogger().log(Level.WARNING, "Could not convert component to legacy text: " + json, ex);
		}
		return "";
	}
}
