package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.viaversion;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.logging.Level;
import java.util.logging.Logger;

@Mixin(AbstractProtocol.class)
public class MixinAbstractProtocol {

    @Redirect(method = "registerServerbound(Lcom/viaversion/viaversion/api/protocol/packet/State;IILcom/viaversion/viaversion/api/protocol/remapper/PacketRemapper;Z)V",
    at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;log(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V"), remap = false)
    public void redirectRegisterServerbound(Logger instance, Level level, String msg, Throwable thrown) {
    }

    @Redirect(method = "registerClientbound(Lcom/viaversion/viaversion/api/protocol/packet/State;IILcom/viaversion/viaversion/api/protocol/remapper/PacketRemapper;Z)V",
            at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;log(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V"), remap = false)
    public void redirectRegisterClientbound(Logger instance, Level level, String msg, Throwable thrown) {
    }

    @Redirect(method = "register",
            at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;log(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V"), remap = false)
    public void redirectRegister(Logger instance, Level level, String msg, Throwable thrown) {
    }
}
