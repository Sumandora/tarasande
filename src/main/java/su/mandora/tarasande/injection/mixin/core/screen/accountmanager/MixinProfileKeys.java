package su.mandora.tarasande.injection.mixin.core.screen.accountmanager;

import net.minecraft.client.util.ProfileKeys;
import net.minecraft.client.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ProfileKeys.class)
public interface MixinProfileKeys {

    @Redirect(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Session;getAccountType()Lnet/minecraft/client/util/Session$AccountType;"))
    private static Session.AccountType forceKeyCreation(Session instance) {
        return Session.AccountType.MSA;
    }

}
