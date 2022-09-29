package de.florianmichael.tarasande.mixin.mixins;

import de.florianmichael.tarasande.module.exploit.ModuleNoChatContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import su.mandora.tarasande.TarasandeMain;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @ModifyVariable(method = "acknowledge", at = @At("HEAD"), argsOnly = true, index = 2)
    public boolean bypassMicrosoft(boolean value) {
        if (TarasandeMain.Companion.get().getManagerModule().get(ModuleNoChatContext.class).getEnabled())
            return false;

        return value;
    }
}
