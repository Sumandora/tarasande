package su.mandora.tarasande.injection.mixin.feature.tarasandevalue.antirdi;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.feature.tarasandevalue.impl.debug.MinecraftDebugger;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Redirect(method = "onPlayerRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasReducedDebugInfo()Z"))
    public boolean forceHasReducedDebugInfo(ClientPlayerEntity instance) {
        if(MinecraftDebugger.INSTANCE.getIgnoreRDI().getValue() && instance == MinecraftClient.getInstance().player)
            return instance.reducedDebugInfo; // The source of my suffering may be the invalidity of data. Invalidity of data may cause destruction, sadness or massive aggression. May the data be correct this time.
        return instance.hasReducedDebugInfo();
    }

}
