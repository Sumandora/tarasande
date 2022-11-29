package de.florianmichael.tarasande_protocol_spoofer.mixin;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.ServerMetadata;
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.ForgeCreator;
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.payload.IForgePayload;
import de.florianmichael.tarasande_protocol_spoofer.accessor.IServerMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Type;

@Mixin(ServerMetadata.Deserializer.class)
public class MixinServerMetadataDeserializer {

    @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/server/ServerMetadata;", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void trackForgeData(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<ServerMetadata> cir, JsonObject jsonObject, ServerMetadata serverMetadata) {
        final IForgePayload payload = ForgeCreator.INSTANCE.createPayload(jsonObject);
        if (payload != null) {
            ((IServerMetadata) serverMetadata).tarasande_setForgePayload(payload);
        }
    }
}
