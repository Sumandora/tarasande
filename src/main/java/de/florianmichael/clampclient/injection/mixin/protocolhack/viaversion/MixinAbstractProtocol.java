/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.clampclient.injection.mixin.protocolhack.viaversion;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.logging.Level;
import java.util.logging.Logger;

@Mixin(value = AbstractProtocol.class, remap = false)
public class MixinAbstractProtocol {

    @Redirect(method = "registerServerbound(Lcom/viaversion/viaversion/api/protocol/packet/State;IILcom/viaversion/viaversion/api/protocol/remapper/PacketRemapper;Z)V",
            at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;log(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V"))
    public void redirectRegisterServerbound(Logger instance, Level level, String msg, Throwable thrown) {
    }

    @Redirect(method = "registerClientbound(Lcom/viaversion/viaversion/api/protocol/packet/State;IILcom/viaversion/viaversion/api/protocol/remapper/PacketRemapper;Z)V",
            at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;log(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V"))
    public void redirectRegisterClientbound(Logger instance, Level level, String msg, Throwable thrown) {
    }

    @Redirect(method = "register",
            at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;log(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V"))
    public void redirectRegister(Logger instance, Level level, String msg, Throwable thrown) {
    }
}
