package net.tarasandedevelopment.tarasande.mixin.mixins.module;

import net.minecraft.client.util.ProfileKeys;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.Signer;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.qualityoflife.ModuleNoSignatures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ProfileKeys.class)
public class MixinProfileKeys {

    @Inject(method = "getSigner", at = @At("HEAD"), cancellable = true)
    public void noSignatures_breakSigner(CallbackInfoReturnable<Signer> cir) {
        if (!TarasandeMain.Companion.get().getDisabled() && TarasandeMain.Companion.get().getManagerModule().get(ModuleNoSignatures.class).getEnabled()) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "getPublicKey", at = @At("HEAD"), cancellable = true)
    public void noSignatures_breakPublicKey(CallbackInfoReturnable<Optional<PlayerPublicKey>> cir) {
        if (!TarasandeMain.Companion.get().getDisabled() && TarasandeMain.Companion.get().getManagerModule().get(ModuleNoSignatures.class).getEnabled()) {
            cir.setReturnValue(Optional.empty());
        }
    }

    @Inject(method = "getPublicKey", at = @At("HEAD"), cancellable = true)
    public void noSignatures_breakPublicKey_ASM(CallbackInfoReturnable<Optional<PlayerPublicKey.PublicKeyData>> cir) {
        if (!TarasandeMain.Companion.get().getDisabled() && TarasandeMain.Companion.get().getManagerModule().get(ModuleNoSignatures.class).getEnabled()) {
            cir.setReturnValue(Optional.empty());
        }
    }
}
