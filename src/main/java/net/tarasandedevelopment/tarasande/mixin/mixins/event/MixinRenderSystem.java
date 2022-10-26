package net.tarasandedevelopment.tarasande.mixin.mixins.event;

import com.mojang.blaze3d.systems.RenderSystem;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventScreenInput;
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderSystem.class, remap = false)
public class MixinRenderSystem {

    @Inject(method = "flipFrame", at = @At("HEAD"))
    private static void hookEventScreenInputAndPollEvents(long window, CallbackInfo ci) {
        if (VersionList.isNewerOrEqualTo(VersionList.R1_13)) {
            EventScreenInput eventScreenInput = new EventScreenInput(false);
            TarasandeMain.Companion.get().getManagerEvent().call(eventScreenInput);
        }

        RotationUtil.INSTANCE.updateFakeRotation(false);
    }
}
