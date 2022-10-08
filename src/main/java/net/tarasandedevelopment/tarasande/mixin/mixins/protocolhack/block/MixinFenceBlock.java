package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.block;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.block.FenceBlock;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FenceBlock.class)
public class MixinFenceBlock {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void injectOnUse(CallbackInfoReturnable<ActionResult> ci) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_10))
            ci.setReturnValue(ActionResult.SUCCESS);
    }
}
