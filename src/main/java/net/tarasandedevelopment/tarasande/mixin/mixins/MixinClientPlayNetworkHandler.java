package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventChatAcknowledge;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @ModifyVariable(method = "acknowledge", at = @At("HEAD"), argsOnly = true, index = 2)
    public boolean modifyDisplayed(boolean value) {
        final EventChatAcknowledge eventChatAcknowledge = new EventChatAcknowledge(value);
        TarasandeMain.Companion.get().getManagerEvent().call(eventChatAcknowledge);

        return eventChatAcknowledge.getAcknowledged();
    }
}
