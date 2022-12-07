package net.tarasandedevelopment.tarasande.injection.mixin.feature.screenextension;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {

    // TODO Port;
//    @Inject(method = "signChatMessage", at = @At("HEAD"), cancellable = true)
//    public void hookNoSignatures(MessageMetadata metadata, DecoratedContents content, LastSeenMessageList lastSeenMessages, CallbackInfoReturnable<MessageSignatureData> cir) {
//        if (TarasandeMain.Companion.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen.class).getSidebar().get(EntrySidebarPanelToggleableNoSignatures.class).getState().getValue()) {
//            cir.cancel();
//        }
//    }
//
//    @Inject(method = "signArguments", at = @At("HEAD"), cancellable = true)
//    public void hookNoSignatures(MessageMetadata signer, ParseResults<CommandSource> parseResults, @Nullable Text preview, LastSeenMessageList lastSeenMessages, CallbackInfoReturnable<ArgumentSignatureDataMap> cir) {
//        if (TarasandeMain.Companion.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen.class).getSidebar().get(EntrySidebarPanelToggleableNoSignatures.class).getState().getValue()) {
//            cir.cancel();
//        }
//    }
}
