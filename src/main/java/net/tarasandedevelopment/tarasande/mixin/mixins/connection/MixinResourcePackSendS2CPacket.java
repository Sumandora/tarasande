package net.tarasandedevelopment.tarasande.mixin.mixins.connection;

import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.tarasandedevelopment.tarasande.mixin.accessor.IResourcePackSendS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ResourcePackSendS2CPacket.class)
public class MixinResourcePackSendS2CPacket implements IResourcePackSendS2CPacket {
    @Mutable
    @Shadow
    @Final
    private String hash;

    @Override
    public void setSHA1(String sha1) {
        this.hash = sha1;
    }
}
