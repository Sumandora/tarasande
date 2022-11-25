package net.tarasandedevelopment.tarasande.injection.mixin.core.connection;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatMessageC2SPacket.class)
public class MixinChatMessageC2SPacket {

    @Redirect(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/message/MessageSignatureData;write(Lnet/minecraft/network/PacketByteBuf;)V"))
    public void preventNullPointerException(MessageSignatureData instance, PacketByteBuf buf) {
        // The account manager allows this to be null (session login)
        if(instance == null)
            buf.writeByteArray(new byte[0]);
        else
            instance.write(buf);
    }

}
