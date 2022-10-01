package de.florianmichael.tarasande.mixin.mixins;

import de.florianmichael.tarasande.event.EventChatAcknowledge;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import su.mandora.tarasande.TarasandeMain;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @ModifyVariable(method = "acknowledge", at = @At("HEAD"), argsOnly = true, index = 2)
    public boolean bypassMicrosoft(boolean value) {
        final EventChatAcknowledge eventChatAcknowledge = new EventChatAcknowledge(value);
        TarasandeMain.Companion.get().getManagerEvent().call(eventChatAcknowledge);

        return eventChatAcknowledge.getAcknowledged();
    }
}
