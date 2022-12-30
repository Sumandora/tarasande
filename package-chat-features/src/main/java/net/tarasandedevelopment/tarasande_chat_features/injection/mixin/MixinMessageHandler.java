package net.tarasandedevelopment.tarasande_chat_features.injection.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande_chat_features.module.ModulePrivateMsgDetector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MessageHandler.class)
public class MixinMessageHandler {

    @Inject(method = "onChatMessage", at = @At("HEAD"))
    public void trackChatMessages(SignedMessage message, GameProfile sender, MessageType.Parameters params, CallbackInfo ci) {
        final ModulePrivateMsgDetector modulePrivateMsgDetector = TarasandeMain.Companion.managerModule().get(ModulePrivateMsgDetector.class);

        if (modulePrivateMsgDetector.getEnabled()) {
            if (message.signature() == null) {
                return;
            }
            modulePrivateMsgDetector.handleInput(message.signature().toByteBuffer());
            for (MessageSignatureData entry : message.signedBody().lastSeenMessages().entries()) {
                modulePrivateMsgDetector.trackHistoryPart(sender.getId(), entry.toByteBuffer());
            }
        }
    }
}
