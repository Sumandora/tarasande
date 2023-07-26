package su.mandora.tarasande_protocol_spoofer.injection.mixin.forgefaker;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande_protocol_spoofer.injection.accessor.IServerMetadata;
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.ForgeCreator;

@Mixin(QueryResponseS2CPacket.class)
public class MixinQueryResponseS2CPacket {

    @Redirect(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;decodeAsJson(Lcom/mojang/serialization/Codec;)Ljava/lang/Object;"))
    private static Object trackForgePayload(PacketByteBuf instance, Codec<ServerMetadata> codec) {
        final var payload = new PacketByteBuf(instance.copy());
        final var jsonObject = JsonHelper.deserialize(PacketByteBuf.GSON, payload.readString(), JsonElement.class);

        final ServerMetadata result = instance.decodeAsJson(codec);
        if (jsonObject.isJsonObject()) {
            final var forgePayload = ForgeCreator.INSTANCE.createPayload(jsonObject.getAsJsonObject());

            if (forgePayload != null) ((IServerMetadata) (Object) result).tarasande_setForgePayload(forgePayload);
        }
        return result;
    }
}
