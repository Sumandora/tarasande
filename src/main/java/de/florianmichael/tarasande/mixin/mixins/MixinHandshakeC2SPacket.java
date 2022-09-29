package de.florianmichael.tarasande.mixin.mixins;

import de.florianmichael.tarasande.mixin.accessor.IHandshakeC2SPacket;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HandshakeC2SPacket.class)
public class MixinHandshakeC2SPacket implements IHandshakeC2SPacket {
    @Mutable
    @Shadow @Final private String address;

    @Override
    public void florianMichael_extendAddress(String address) {
        this.address += address;
    }
}
