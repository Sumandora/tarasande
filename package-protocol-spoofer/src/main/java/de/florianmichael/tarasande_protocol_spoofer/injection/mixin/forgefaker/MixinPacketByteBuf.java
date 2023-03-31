package de.florianmichael.tarasande_protocol_spoofer.injection.mixin.forgefaker;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import de.florianmichael.tarasande_protocol_spoofer.injection.accessor.IServerMetadata;
import de.florianmichael.tarasande_protocol_spoofer.tarasandevalues.forge.ForgeCreator;
import de.florianmichael.tarasande_protocol_spoofer.tarasandevalues.forge.payload.IForgePayload;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.ServerMetadata;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

@Mixin(PacketByteBuf.class)
public abstract class MixinPacketByteBuf {

    @Shadow @Final private static Gson GSON;

    @Shadow public abstract String readString();

    /**
     * @author
     * @reason
     * `// TOOD | Make Code Better
     */
    @Overwrite
    public <T> T decodeAsJson(Codec<T> codec) {
        JsonElement jsonElement = JsonHelper.deserialize(GSON, this.readString(), JsonElement.class);
        DataResult<T> dataResult = codec.parse(JsonOps.INSTANCE, jsonElement);
        if (codec == ServerMetadata.CODEC) {
            final IForgePayload payload = ForgeCreator.INSTANCE.createPayload(jsonElement.getAsJsonObject());
            if (payload != null) {
                ((IServerMetadata) dataResult).tarasande_setForgePayload(payload);
            }
        }
        return Util.getResult(dataResult, (error) -> {
            return new DecoderException("Failed to decode json: " + error);
        });
    }
}
