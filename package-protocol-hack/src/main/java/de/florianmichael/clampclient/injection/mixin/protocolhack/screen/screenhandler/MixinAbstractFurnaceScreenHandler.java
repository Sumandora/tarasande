package de.florianmichael.clampclient.injection.mixin.protocolhack.screen.screenhandler;

import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceScreenHandler.class)
public class MixinAbstractFurnaceScreenHandler {

    @Shadow @Final private PropertyDelegate propertyDelegate;

    @Inject(method = "getCookProgress", at = @At("HEAD"), cancellable = true)
    public void oldCookLogic(CallbackInfoReturnable<Integer> cir) {
        if (VersionList.isOlderOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            cir.setReturnValue(propertyDelegate.get(0) * 24 / 200);
        }
    }

    @Inject(method = "getFuelProgress", at = @At("HEAD"), cancellable = true)
    public void oldFuelLogic(CallbackInfoReturnable<Integer> cir) {
        if (VersionList.isOlderOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            int currentItemBurnTime = propertyDelegate.get(2);
            if (currentItemBurnTime == 0) {
                currentItemBurnTime = 200;
            }
            cir.setReturnValue(propertyDelegate.get(1) * 12 / currentItemBurnTime);
        }
    }
}
