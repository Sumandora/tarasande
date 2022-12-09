package net.tarasandedevelopment.tarasande_mod_fixes.mixin.modmenu;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Pseudo
@Mixin(targets = "com.terraformersmc.modmenu.event.ModMenuEventHandler", remap = false)
public class MixinModMenuEventHandler {

    @SuppressWarnings("UnresolvedMixinReference")
    @ModifyConstant(method = "afterGameMenuScreenInit", constant = @Constant(intValue = 0, ordinal = 0))
    private static int incrementButtonIndex(int original) {
        return original + 1;
    }

}
