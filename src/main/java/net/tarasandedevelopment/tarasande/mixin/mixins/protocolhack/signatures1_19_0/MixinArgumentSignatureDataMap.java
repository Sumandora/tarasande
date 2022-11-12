package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.signatures1_19_0;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.command.argument.DecoratableArgumentList;
import net.minecraft.network.encryption.Signer;
import net.minecraft.network.message.ArgumentSignatureDataMap;
import net.minecraft.network.message.MessageMetadata;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.protocolhack.util.MessageSigner1_19_0;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(ArgumentSignatureDataMap.class)
public class MixinArgumentSignatureDataMap {

    @Inject(method = "sign", at = @At("HEAD"), cancellable = true)
    private static void injectSign(DecoratableArgumentList<?> arguments, ArgumentSignatureDataMap.ArgumentSigner signer, CallbackInfoReturnable<ArgumentSignatureDataMap> cir) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_19)) {
            final List<ArgumentSignatureDataMap.Entry> list = ArgumentSignatureDataMap.toNameValuePairs(arguments).stream().map(entry -> {
                final MessageMetadata metadata = MessageSigner1_19_0.INSTANCE.get();
                final MessageSignatureData messageSignatureData = MessageSigner1_19_0.INSTANCE.sign((Signer) signer, Text.of(entry.getFirst()), metadata.sender(), metadata.timestamp(), metadata.salt());

                return new ArgumentSignatureDataMap.Entry(entry.getFirst(), messageSignatureData);
            }).collect(Collectors.toList());

            cir.setReturnValue(new ArgumentSignatureDataMap(list));
        }
    }
}
