package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PacketByteBuf.class, remap = false)
public class MixinPacketByteBuf {

    @Inject(method = "readText", at = @At(value = "INVOKE", target = "Lio/netty/handler/codec/DecoderException;<init>(Ljava/lang/String;)V", shift = At.Shift.BEFORE), cancellable = true)
    public void injectReadText(CallbackInfoReturnable<Text> cir) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_18_1)) {
            cir.setReturnValue(null);
        }
    }
}
