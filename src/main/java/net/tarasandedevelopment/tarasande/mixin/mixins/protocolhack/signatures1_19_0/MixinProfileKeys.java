package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.signatures1_19_0;

import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import net.minecraft.client.util.ProfileKeys;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IPublicKeyData_Protocol;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProfileKeys.class)
public class MixinProfileKeys {

    @Inject(method = "decodeKeyPairResponse", at = @At("RETURN"))
    private static void trackLegacyKey(KeyPairResponse keyPairResponse, CallbackInfoReturnable<PlayerPublicKey.PublicKeyData> cir) {
        ((IPublicKeyData_Protocol) (Object) cir.getReturnValue()).set1_19_0Key(keyPairResponse.getLegacyPublicKeySignature());
    }
}
