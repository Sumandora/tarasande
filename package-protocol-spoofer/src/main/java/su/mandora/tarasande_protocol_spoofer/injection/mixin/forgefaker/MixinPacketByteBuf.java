package su.mandora.tarasande_protocol_spoofer.injection.mixin.forgefaker;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.ServerMetadata;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import su.mandora.tarasande_protocol_spoofer.injection.accessor.IServerMetadata;
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.ForgeCreator;
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload.IForgePayload;

@Mixin(PacketByteBuf.class)
public abstract class MixinPacketByteBuf {

    @Shadow
    @Final
    private static Gson GSON;

    @Shadow
    public abstract String readString();

    /**
     * @author Mojang, Johannes
     * @reason Hook Forge Faker
     */
    @Overwrite
    public <T> T decodeAsJson(Codec<T> codec) {
        JsonElement jsonElement = JsonHelper.deserialize(GSON, this.readString(), JsonElement.class);
        DataResult<T> dataResult = codec.parse(JsonOps.INSTANCE, jsonElement);
        final T result = Util.getResult(dataResult, (error) -> new DecoderException("Failed to decode json: " + error));
        if (codec == ServerMetadata.CODEC) {
            final IForgePayload payload = ForgeCreator.INSTANCE.createPayload(jsonElement.getAsJsonObject());
            if (payload != null) ((IServerMetadata) result).tarasande_setForgePayload(payload);
        }
        return result;
    }
}
