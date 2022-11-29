package net.tarasandedevelopment.tarasande.injection.mixin.feature.module.connection;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.exploit.ModuleNoChatContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @ModifyVariable(method = "acknowledge", at = @At("HEAD"), argsOnly = true, index = 2)
    public boolean hookNoChatContext(boolean value) {
        if (TarasandeMain.Companion.managerModule().get(ModuleNoChatContext.class).getEnabled())
            return false;
        return value;
    }
}
