/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 7/9/22, 12:09 AM
 *
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.0--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license.
 */

package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.packet;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(UpdatePlayerAbilitiesC2SPacket.class)
public class MixinUpdatePlayerAbilitiesC2SPacket {

    @Shadow
    @Final
    private boolean flying;

    @Inject(method = "write", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeByte(I)Lio/netty/buffer/ByteBuf;", shift = At.Shift.BEFORE), cancellable = true)
    public void injectWrite(PacketByteBuf buf, CallbackInfo ci) {
        final PlayerAbilities abilities = MinecraftClient.getInstance().player.getAbilities();

        byte b = 0;

        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_15_2)) {
            if (this.flying) b = (byte) (b | 2); // Minecraft

            if (abilities.invulnerable) b |= 1;
            if (abilities.allowFlying) b |= 4;
            if (abilities.creativeMode) b |= 8; // Protocol Hack Fixes

            buf.writeByte(b);
            ci.cancel();
        }
    }
}
