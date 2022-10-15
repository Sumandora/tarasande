package net.tarasandedevelopment.tarasande.mixin.mixins.connection;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.tarasandedevelopment.tarasande.mixin.accessor.ICustomPayloadC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CustomPayloadC2SPacket.class)
public class MixinCustomPayloadC2SPacket implements ICustomPayloadC2SPacket {
    @Mutable
    @Shadow @Final private PacketByteBuf data;

    @Override
    public void setData(PacketByteBuf byteBuf) {
        this.data = byteBuf;
    }
}
