package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.screen;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.tarasandedevelopment.tarasande.protocol.platform.ProtocolHackValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatHud.class)
public class MixinChatHud {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHudLine$Visible;indicator()Lnet/minecraft/client/gui/hud/MessageIndicator;"))
    public MessageIndicator removeIndicators(ChatHudLine.Visible instance) {
        if (ProtocolHackValues.INSTANCE.getHideSignatureIndicator().getValue()) {
            return null;
        }
        return instance.indicator();
    }
}
