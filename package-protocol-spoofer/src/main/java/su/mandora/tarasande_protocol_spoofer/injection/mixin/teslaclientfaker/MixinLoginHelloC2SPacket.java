package su.mandora.tarasande_protocol_spoofer.injection.mixin.teslaclientfaker;

import su.mandora.tarasande_protocol_spoofer.TarasandeProtocolSpoofer;
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.TeslaClientSpoofer;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LoginHelloC2SPacket.class)
public class MixinLoginHelloC2SPacket {

    @ModifyConstant(method = "write", constant = @Constant(intValue = 16))
    public int increaseMaxNameLength(int constant) {
        if (!TarasandeProtocolSpoofer.Companion.getViaFabricPlusLoaded()) return constant;
        if (TeslaClientSpoofer.INSTANCE.getEnabled().getValue()) {
            return 32767;
        }
        return constant;
    }
}
