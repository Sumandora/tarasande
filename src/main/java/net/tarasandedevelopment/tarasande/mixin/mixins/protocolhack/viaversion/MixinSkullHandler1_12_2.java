package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.viaversion;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.blockentities.SkullHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.logging.Logger;

@Mixin(SkullHandler.class)
public class MixinSkullHandler1_12_2 {

    @Redirect(method = "transform", at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;warning(Ljava/lang/String;)V"), remap = false)
    public void redirectTransform(Logger instance, String msg) {
    }

    @Inject(method = "transform", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void injectTransform(UserConnection user, CompoundTag tag, CallbackInfoReturnable<Integer> cir) {
        if (tag == null) cir.setReturnValue(-1);
    }

    @Inject(method = "getLong", at = @At("HEAD"), cancellable = true, remap = false)
    public void injectGetLong(NumberTag tag, CallbackInfoReturnable<Long> cir) {
        if (tag == null)
            cir.setReturnValue(0L);
    }
}
