package de.florianmichael.tarasande_protocol_hack.injection.mixin.tarasande.account;

import com.google.gson.JsonObject;
import de.florianmichael.tarasande_protocol_hack.tarasande.account.AccountBedrock;
import de.florianmichael.tarasande_protocol_hack.xbox.XboxLiveSession;
import net.minecraft.client.util.Session;
import net.tarasandedevelopment.tarasande.TarasandeMainKt;
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.impl.microsoft.AccountMicrosoft;
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.azureapp.AzureAppPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.sql.Timestamp;
import java.time.Instant;

@Mixin(value = AccountMicrosoft.class, remap = false)
public class MixinAccountMicrosoft {

    @Shadow
    private AzureAppPreset azureApp;

    @Redirect(method = "logIn", at = @At(value = "INVOKE", target = "Lnet/tarasandedevelopment/tarasande/system/screen/accountmanager/account/impl/microsoft/AccountMicrosoft$MSAuthProfile;asSession()Lnet/minecraft/client/util/Session;"))
    public Session rewriteToBedrock(AccountMicrosoft.MSAuthProfile instance) {
        if((Object) this instanceof AccountBedrock) {
            return XboxLiveSession.Companion.create(instance.getOAuthToken().getAccessToken());
        }
        return instance.asSession();
    }

    @Inject(method = "buildFromOAuthToken", at = @At("HEAD"), cancellable = true)
    public void cancelRemainingProcess(JsonObject oAuthToken, CallbackInfoReturnable<AccountMicrosoft.MSAuthProfile> cir) {
        if((Object) this instanceof AccountBedrock) {
            AccountMicrosoft.MSAuthProfile.OAuthToken oAuthTokenObj = TarasandeMainKt.getGson().fromJson(oAuthToken, AccountMicrosoft.MSAuthProfile.OAuthToken.class);
            cir.setReturnValue(new AccountMicrosoft.MSAuthProfile(
                    oAuthTokenObj,
                    new AccountMicrosoft.MSAuthProfile.XboxLiveAuth(
                            Timestamp.from(Instant.now()),
                            Timestamp.from(Instant.now()),
                            "",
                            new AccountMicrosoft.MSAuthProfile.DisplayClaim(new AccountMicrosoft.MSAuthProfile.Xui[0])
                    ),
                    new AccountMicrosoft.MSAuthProfile.XboxLiveSecurityTokens(
                            Timestamp.from(Instant.now()),
                            Timestamp.from(Instant.now()),
                            "",
                            new AccountMicrosoft.MSAuthProfile.DisplayClaim(new AccountMicrosoft.MSAuthProfile.Xui[0])
                    ),
                    new AccountMicrosoft.MSAuthProfile.MinecraftLogin(
                            "",
                            new Object[0],
                            "",
                            "",
                            0
                    ),
                    new AccountMicrosoft.MSAuthProfile.MinecraftProfile("",
                            "",
                            new AccountMicrosoft.MSAuthProfile.Skin[0],
                            new AccountMicrosoft.MSAuthProfile.Cape[0]
                    ),
                    azureApp
            ));
        }
    }

}
