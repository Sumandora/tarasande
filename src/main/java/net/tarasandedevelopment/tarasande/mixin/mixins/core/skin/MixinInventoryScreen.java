package net.tarasandedevelopment.tarasande.mixin.mixins.core.skin;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import net.tarasandedevelopment.tarasande.util.dummy.AbstractClientPlayerEntityDummy;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InventoryScreen.class)
public class MixinInventoryScreen {

    @Redirect(method = "drawEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;bodyYaw:F", ordinal = 1))
    private static void modifyBodyYaw(LivingEntity instance, float value) {
        if (instance instanceof AbstractClientPlayerEntityDummy) {
            instance.bodyYaw = (float) (GLFW.glfwGetTime() * 50);
            return;
        }
        instance.bodyYaw = value;
    }

    @Redirect(method = "drawEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setYaw(F)V", ordinal = 0))
    private static void modifyHeadYaw(LivingEntity instance, float v) {
        if (instance instanceof AbstractClientPlayerEntityDummy) {
            instance.setYaw((float) (GLFW.glfwGetTime() * 50));
            return;
        }
        instance.setYaw(v);
    }
}
