package net.tarasandedevelopment.tarasande.injection.mixin.feature.screenextension;

import net.minecraft.client.util.ProfileKeys;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ProfileKeys.class)
public class MixinProfileKeys { // TODO Register this again; This is a interface now tf

    // TODO Port;
//    @Inject(method = "getSigner", at = @At("HEAD"), cancellable = true)
//    public void hookNoSignatures_removeSigner(CallbackInfoReturnable<Signer> cir) {
//        if (TarasandeMain.Companion.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen.class).getSidebar().get(EntrySidebarPanelToggleableNoSignatures.class).getState().getValue()) {
//            cir.setReturnValue(null);
//        }
//    }
//
//    @Inject(method = "getPublicKey", at = @At("HEAD"), cancellable = true)
//    public void hookNoSignatures_removePublicKey(CallbackInfoReturnable<Optional<PlayerPublicKey>> cir) {
//        if (TarasandeMain.Companion.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen.class).getSidebar().get(EntrySidebarPanelToggleableNoSignatures.class).getState().getValue()) {
//            cir.setReturnValue(Optional.empty());
//        }
//    }
}
