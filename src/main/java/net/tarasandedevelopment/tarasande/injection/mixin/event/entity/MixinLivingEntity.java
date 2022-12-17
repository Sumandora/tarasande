package net.tarasandedevelopment.tarasande.injection.mixin.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.tarasandedevelopment.tarasande.event.EventEntityHurt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.event.EventDispatcher;

@Mixin(value = LivingEntity.class)
public class MixinLivingEntity {

    @Inject(method = "handleStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    public void hookEventEntityHurtDamage(byte status, CallbackInfo ci) {
        EventDispatcher.INSTANCE.call(new EventEntityHurt((Entity) (Object) this));
    }
}
