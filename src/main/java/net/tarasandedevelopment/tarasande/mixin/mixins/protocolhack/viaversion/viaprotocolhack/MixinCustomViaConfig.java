package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.viaversion.viaprotocolhack;

import com.viaversion.viaversion.configuration.AbstractViaConfig;
import de.florianmichael.viaprotocolhack.platform.viaversion.CustomViaConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(CustomViaConfig.class)
public abstract class MixinCustomViaConfig extends AbstractViaConfig {

    protected MixinCustomViaConfig(File configFile) {
        super(configFile);
    }

    @Inject(method = "isShowShieldWhenSwordInHand", at = @At("HEAD"), cancellable = true)
    public void disableShowShieldInHand(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Override
    public boolean isShieldBlocking() {
        return false;
    }
}
