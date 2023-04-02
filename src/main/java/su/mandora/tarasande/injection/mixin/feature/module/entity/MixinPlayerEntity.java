package su.mandora.tarasande.injection.mixin.feature.module.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.movement.ModuleSafeWalk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.movement.ModuleSafeWalk;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

    @Inject(method = "clipAtLedge", at = @At("HEAD"), cancellable = true)
    public void hookSafeWalk(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            ModuleSafeWalk moduleSafeWalk = ManagerModule.INSTANCE.get(ModuleSafeWalk.class);
            if (moduleSafeWalk.getEnabled().getValue() && !moduleSafeWalk.getSneak().getValue())
                cir.setReturnValue(true);
        }
    }

}
