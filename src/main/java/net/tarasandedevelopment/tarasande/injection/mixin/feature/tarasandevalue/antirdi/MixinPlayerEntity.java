package net.tarasandedevelopment.tarasande.injection.mixin.feature.tarasandevalue.antirdi;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.impl.debug.MinecraftDebugger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

    @Inject(method = "hasReducedDebugInfo", at = @At("HEAD"), cancellable = true)
    public void disableReducedDebugInfo(CallbackInfoReturnable<Boolean> cir) {
        if(MinecraftDebugger.INSTANCE.getIgnoreRDI().getValue() && (Object) this == MinecraftClient.getInstance().player)
            cir.setReturnValue(false);
    }

}
