package net.tarasandedevelopment.tarasande.injection.mixin.feature.clientvalue;

import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.Request;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.logging.Level;

@Mixin(RealmsClient.class)
public class MixinRealmsClient {

    @Inject(method = "execute", at = @At("HEAD"))
    public void disableRequests(Request<?> r, CallbackInfoReturnable<String> cir) throws RealmsServiceException {
        if (TarasandeMain.Companion.clientValues().getDisableRealmsRequests().getValue()) {
            TarasandeMain.Companion.get().getLogger().log(Level.INFO, "Blocked realms request");
            throw new RealmsServiceException(1337, TarasandeMain.Companion.get().getName() + " cancelled this realms request to guarantee your privacy");
        }
    }

}
