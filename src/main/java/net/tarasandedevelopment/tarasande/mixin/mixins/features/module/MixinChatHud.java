package net.tarasandedevelopment.tarasande.mixin.mixins.features.module;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.render.ModuleNoMessageSignatureIndicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatHud.class)
public class MixinChatHud {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHudLine$Visible;indicator()Lnet/minecraft/client/gui/hud/MessageIndicator;"))
    public MessageIndicator removeIndicators(ChatHudLine.Visible instance) {
        ModuleNoMessageSignatureIndicator moduleNoMessageSignatureIndicator = TarasandeMain.Companion.get().getManagerModule().get(ModuleNoMessageSignatureIndicator.class);
        //TODO THIS IS A PROTOCOL FIX
        if (moduleNoMessageSignatureIndicator.getEnabled() || !moduleNoMessageSignatureIndicator.isEnabled()) {
            return null;
        }
        return instance.indicator();
    }
}
