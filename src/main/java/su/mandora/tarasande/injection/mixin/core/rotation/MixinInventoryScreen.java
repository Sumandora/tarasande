package su.mandora.tarasande.injection.mixin.core.rotation;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.injection.accessor.ILivingEntity;

@Mixin(InventoryScreen.class)
public class MixinInventoryScreen {

    @Unique
    private static boolean tarasande_wasInInventory; // Technically not needed, but I keep it in case some weird stuff happens

    @Inject(method = "drawEntity(Lnet/minecraft/client/gui/DrawContext;IIIIIFFFLnet/minecraft/entity/LivingEntity;)V", at = @At("HEAD"))
    private static void clearHeadRotation(DrawContext context, int x1, int y1, int x2, int y2, int size, float f, float mouseX, float mouseY, LivingEntity entity, CallbackInfo ci) {
        ILivingEntity accessor = (ILivingEntity) entity;

        tarasande_wasInInventory = accessor.tarasande_isInInventory();
        accessor.tarasande_setInInventory(true);
    }

    @Inject(method = "drawEntity(Lnet/minecraft/client/gui/DrawContext;IIIIIFFFLnet/minecraft/entity/LivingEntity;)V", at = @At("TAIL"))
    private static void resetHeadRotation(DrawContext context, int x1, int y1, int x2, int y2, int size, float f, float mouseX, float mouseY, LivingEntity entity, CallbackInfo ci) {
        ILivingEntity accessor = (ILivingEntity) entity;
        accessor.tarasande_setInInventory(tarasande_wasInInventory);
    }

}
