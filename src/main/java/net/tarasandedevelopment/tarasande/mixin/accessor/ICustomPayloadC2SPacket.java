package net.tarasandedevelopment.tarasande.mixin.accessor;

import net.minecraft.network.PacketByteBuf;

public interface ICustomPayloadC2SPacket {

    void setData(final PacketByteBuf byteBuf);
}
