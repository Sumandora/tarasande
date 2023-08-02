package su.mandora.tarasande.injection.mixin.core.screen.accountmanager;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.TarasandeMainKt;

@Mixin(value = YggdrasilAuthenticationService.class, remap = false)
public class MixinYggdrasilAuthenticationService {

    @Redirect(method = "<init>(Ljava/net/Proxy;Ljava/lang/String;Lcom/mojang/authlib/Environment;)V", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;)V"))
    private void silentInit(Logger instance, String s) {
        if(!s.endsWith("name='" + TarasandeMainKt.TARASANDE_NAME + "'"))
            instance.info(s);
    }

}
