/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.clampclient.injection.mixin.protocolhack.viaversion;

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
            if (Via.getManager().isDebug())
                throwable.printStackTrace(); // In case there is some nasty protocol check, we should be notified
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
