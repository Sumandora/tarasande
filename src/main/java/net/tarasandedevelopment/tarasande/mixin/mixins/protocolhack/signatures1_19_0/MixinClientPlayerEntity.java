package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.signatures1_19_0;

import com.mojang.brigadier.ParseResults;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.network.encryption.Signer;
import net.minecraft.network.message.*;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.protocolhack.util.MessageSigner1_19_0;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {

    @Shadow
    @Final
    protected MinecraftClient client;

    @Inject(method = "signChatMessage", at = @At("HEAD"), cancellable = true)
    public void injectSignChatMessage(MessageMetadata metadata, DecoratedContents content, LastSeenMessageList lastSeenMessages, CallbackInfoReturnable<MessageSignatureData> cir) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_19)) {
            try {
                final Signer signer = this.client.getProfileKeys().getSigner();

                if (signer != null)
                    cir.setReturnValue(MessageSigner1_19_0.INSTANCE.sign(signer, content.decorated(), metadata.sender(), metadata.timestamp(), metadata.salt()));
            } catch (Exception ignored) {
            }
            cir.cancel();
        }
    }

    @Inject(method = "signArguments", at = @At(value = "HEAD"))
    public void injectSignArguments(MessageMetadata signer, ParseResults<CommandSource> parseResults, @Nullable Text preview, LastSeenMessageList lastSeenMessages, CallbackInfoReturnable<ArgumentSignatureDataMap> cir) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_19)) {
            MessageSigner1_19_0.INSTANCE.track(signer);
        }
    }
}
