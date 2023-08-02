package su.mandora.tarasande.injection.mixin.feature.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleNoSwing;

@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {

    @Shadow @Final private MinecraftClient client;

    @Shadow private ItemStack mainHand;

    @Shadow private ItemStack offHand;

    @Inject(method = "updateHeldItems", at = @At("HEAD"))
    public void hookNoSwing(CallbackInfo ci) {
        ModuleNoSwing moduleNoSwing = ManagerModule.INSTANCE.get(ModuleNoSwing.class);
        if (moduleNoSwing.getEnabled().getValue() && moduleNoSwing.getDisableEquipProgress().getValue()) {
            ItemStack newMainHand = client.player.getMainHandStack();
            if(moduleNoSwing.canEquipBeIgnored(mainHand, client.player.getMainHandStack()))
                mainHand = newMainHand;

            ItemStack newOffHand = client.player.getOffHandStack();
            if(moduleNoSwing.canEquipBeIgnored(client.player.getOffHandStack(), newOffHand))
                offHand = newOffHand;
        }
    }

    @Redirect(method = "updateHeldItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F"))
    public float hookNoSwing(ClientPlayerEntity instance, float v) {
        ModuleNoSwing moduleNoSwing = ManagerModule.INSTANCE.get(ModuleNoSwing.class);
        if (moduleNoSwing.getEnabled().getValue() && moduleNoSwing.getDisableEquipProgress().getValue()) {
            return 1F;
        }
        return instance.getAttackCooldownProgress(v);
    }

    @Inject(method = "resetEquipProgress", at = @At("HEAD"), cancellable = true)
    public void hookNoSwing(Hand hand, CallbackInfo ci) {
        ModuleNoSwing moduleNoSwing = ManagerModule.INSTANCE.get(ModuleNoSwing.class);
        if (moduleNoSwing.getEnabled().getValue() && moduleNoSwing.getDisableEquipProgress().getValue()) {
            ci.cancel();
        }
    }
}
