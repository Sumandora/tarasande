package net.tarasandedevelopment.tarasande.injection.mixin.feature.clientvalue;

import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ClientConnection.class, priority = 1001)
public class MixinClientConnection {

    @Redirect(method = "exceptionCaught", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;disconnect(Lnet/minecraft/text/Text;)V", ordinal = 0))
    public void noNettyTimeout(ClientConnection instance, Text disconnectReason) {
        if (!TarasandeMain.INSTANCE.clientValues.getRemoveNettyExceptionHandling().isSelected(0)) {
            instance.disconnect(disconnectReason);
        }
    }

    @Redirect(method = "exceptionCaught", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;debug(Ljava/lang/String;Ljava/lang/Throwable;)V", ordinal = 2, remap = false))
    public void cancelWrongPacketHandling(Logger instance, String s, Throwable throwable) {
        if (!TarasandeMain.INSTANCE.clientValues.getRemoveNettyExceptionHandling().isSelected(1)) {
            instance.debug(s, throwable);
        }
    }
}
