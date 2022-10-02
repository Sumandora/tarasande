package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.exploit.ModuleNoChatContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @ModifyVariable(method = "acknowledge", at = @At("HEAD"), argsOnly = true, index = 2)
    public boolean modifyDisplayed(boolean value) {
        if(!TarasandeMain.Companion.get().getDisabled())
            if(TarasandeMain.Companion.get().getManagerModule().get(ModuleNoChatContext.class).getEnabled())
                return false;
        return value;
    }
}
