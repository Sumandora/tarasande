package de.florianmichael.clampclient.injection.mixin.protocolhack.entity;

import de.florianmichael.vialoadingbase.ViaLoadingBase;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.entity.passive.AllayEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AllayEntity.class)
public class MixinAllayEntity {
    
    @Inject(method = "getHeightOffset", at = @At("HEAD"), cancellable = true)
    public void changeHeightOffset(CallbackInfoReturnable<Double> cir) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_19_1)) {
            cir.setReturnValue(0.0);
        }
    }
}
