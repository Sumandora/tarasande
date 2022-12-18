package net.tarasandedevelopment.tarasande.injection.mixin.event.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.tarasandedevelopment.tarasande.event.EventEntityHurt;
import net.tarasandedevelopment.tarasande.event.EventSwing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.event.EventDispatcher;

@Mixin(value = LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract float getYaw(float tickDelta);

    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;)V", at = @At("HEAD"), cancellable = true)
    public void hookEventSwing(Hand hand, CallbackInfo ci) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventSwing eventSwing = new EventSwing(hand);
            EventDispatcher.INSTANCE.call(eventSwing);
            if (eventSwing.getCancelled())
                ci.cancel();
        }
    }

    @Inject(method = "handleStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    public void hookEventEntityHurtDamage(byte status, CallbackInfo ci) {
        EventDispatcher.INSTANCE.call(new EventEntityHurt(this));
    }
}
