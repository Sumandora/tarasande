package su.mandora.tarasande.injection.mixin.event.item;

import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventItemCooldown;

@Mixin(ItemCooldownManager.class)
public class MixinItemCooldownManager {

    @Inject(method = "getCooldownProgress", at = @At("RETURN"), cancellable = true)
    public void hookEventItemCooldown(Item item, float partialTicks, CallbackInfoReturnable<Float> cir) {
        EventItemCooldown eventItemCooldown = new EventItemCooldown(item, cir.getReturnValue());
        EventDispatcher.INSTANCE.call(eventItemCooldown);
        cir.setReturnValue(eventItemCooldown.getCooldown());
    }

}
