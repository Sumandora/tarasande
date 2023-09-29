package su.mandora.tarasande.injection.mixin.feature.tarasandevalue;

import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsError;
import net.minecraft.client.realms.Request;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.TarasandeMainKt;
import su.mandora.tarasande.feature.tarasandevalue.impl.PrivacyValues;

import java.util.logging.Level;

@Mixin(RealmsClient.class)
public class MixinRealmsClient {

    @Inject(method = "execute", at = @At("HEAD"))
    public void disableRequests(Request<?> r, CallbackInfoReturnable<String> cir) throws RealmsServiceException {
        if (PrivacyValues.INSTANCE.getDisableRealmsRequests().getValue()) {
            TarasandeMainKt.getLogger().log(Level.INFO, "Blocked realms request");
            throw new RealmsServiceException(new RealmsError() {
                @Override
                public int getErrorCode() {
                    return 1337;
                }

                @Override
                public Text getText() {
                    return Text.literal(TarasandeMainKt.TARASANDE_NAME + " cancelled this realms request to guarantee your privacy");
                }

                @Override
                public String getErrorMessage() {
                    return TarasandeMainKt.TARASANDE_NAME + " cancelled this realms request to guarantee your privacy";
                }
            });
        }
    }

}
