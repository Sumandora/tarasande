package net.tarasandedevelopment.tarasande.injection.mixin.feature.screenextension;

import net.minecraft.client.util.ProfileKeys;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.Signer;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.EntrySidebarPanelToggleableNoSignatures;
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.ScreenExtensionSidebarMultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ProfileKeys.class)
public class MixinProfileKeys {

    @Inject(method = "getSigner", at = @At("HEAD"), cancellable = true)
    public void hookNoSignatures_removeSigner(CallbackInfoReturnable<Signer> cir) {
        if (TarasandeMain.Companion.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen.class).getSidebar().get(EntrySidebarPanelToggleableNoSignatures.class).getState().getValue()) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "getPublicKey", at = @At("HEAD"), cancellable = true)
    public void hookNoSignatures_removePublicKey(CallbackInfoReturnable<Optional<PlayerPublicKey>> cir) {
        if (TarasandeMain.Companion.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen.class).getSidebar().get(EntrySidebarPanelToggleableNoSignatures.class).getState().getValue()) {
            cir.setReturnValue(Optional.empty());
        }
    }
}
