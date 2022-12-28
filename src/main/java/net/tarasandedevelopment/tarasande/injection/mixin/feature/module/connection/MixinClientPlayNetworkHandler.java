package net.tarasandedevelopment.tarasande.injection.mixin.feature.module.connection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.message.SignedMessage;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.exploit.ModuleNoChatContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @ModifyVariable(method = "acknowledge", at = @At("HEAD"), argsOnly = true, index = 2)
    public boolean hookNoChatContext(boolean value, SignedMessage message) {
        if (TarasandeMain.Companion.managerModule().get(ModuleNoChatContext.class).getEnabled()) {
            //noinspection DataFlowIssue
            return message.getSender().equals(MinecraftClient.getInstance().player.getUuid());
        }

        return value;
    }
}
