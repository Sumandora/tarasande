package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.viaversion;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.BlockEntityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityProvider.class)
public class MixinBlockEntityProvider1_12_2 {

    @Inject(method = "sendBlockChange", at = @At("HEAD"), cancellable = true, remap = false)
    public void injectSendBlockChange(UserConnection user, Position position, int blockId, CallbackInfo ci) {
        if (blockId > 5000)
            ci.cancel();
    }
}
