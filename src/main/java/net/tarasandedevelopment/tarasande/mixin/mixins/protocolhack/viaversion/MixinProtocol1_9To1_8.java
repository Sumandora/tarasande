package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.viaversion;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonNull;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.util.GsonUtil;
import net.md_5.bungee.chat.ComponentSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.lang.reflect.Type;

@Mixin(value = Protocol1_9To1_8.class, remap = false)
public class MixinProtocol1_9To1_8 {

    @Shadow
    private static JsonElement constructJson(String par1) {
        return null;
    }

    /**
     * @author ViaVersion Team, FlorianMichael as EnZaXD
     * @reason fix json components
     */
    @Overwrite
    public static JsonElement fixJson(String line) {
        try {
            line = ComponentSerializer.toString(ComponentSerializer.parse(line));
        } catch (Throwable throwable) { // this doesn't look like its valid
            return constructJson(line);
        }

        if (line == null || line.equalsIgnoreCase("null")) {
            return JsonNull.INSTANCE;
        } else {
            if ((!line.startsWith("\"") || !line.endsWith("\"")) && (!line.startsWith("{") || !line.endsWith("}"))) {
                return constructJson(line);
            }
            if (line.startsWith("\"") && line.endsWith("\"")) {
                line = "{\"text\":" + line + "}";
            }
        }
        try {
            return GsonUtil.getGson().fromJson(line, (Type) JsonObject.class);
        } catch (Exception e) {
            if (Via.getConfig().isForceJsonTransform()) {
                return constructJson(line);
            } else {
                Via.getPlatform().getLogger().warning("Invalid JSON String: \"" + line + "\" Please report this issue to the ViaVersion Github: " + e.getMessage());
                return GsonUtil.getGson().fromJson("{\"text\":\"\"}", (Type) JsonObject.class);
            }
        }
    }
}
