package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventChatAcknowledge;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @ModifyVariable(method = "acknowledge", at = @At("HEAD"), argsOnly = true, index = 2)
    public boolean modifyDisplayed(boolean value) {
        final EventChatAcknowledge eventChatAcknowledge = new EventChatAcknowledge(value);
        TarasandeMain.Companion.get().getManagerEvent().call(eventChatAcknowledge);

        return eventChatAcknowledge.getAcknowledged();
    }
}
