package su.mandora.tarasande.injection.mixin.core.screen.accountmanager;

import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ProfileKeys.class)
public interface MixinProfileKeys {

    @Redirect(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/session/Session;getAccountType()Lnet/minecraft/client/session/Session$AccountType;"))
    private static Session.AccountType forceKeyCreation(Session instance) {
        return Session.AccountType.MSA;
    }

}
