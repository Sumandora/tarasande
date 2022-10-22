package net.tarasandedevelopment.tarasande.mixin.mixins.event.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventJump;
import net.tarasandedevelopment.tarasande.event.EventSwing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    @Unique
    private float tarasande_originalYaw;

    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract float getYaw(float tickDelta);

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    public void hookEventJumpPre(CallbackInfo ci) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventJump eventJump = new EventJump(tarasande_originalYaw = getYaw(), EventJump.State.PRE);
            TarasandeMain.Companion.get().getEventDispatcher().call(eventJump);
            setYaw(eventJump.getYaw());
            if (eventJump.getCancelled())
                ci.cancel();
        }
    }

    @Inject(method = "jump", at = @At("TAIL"))
    public void hookEventJumpPost(CallbackInfo ci) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventJump eventJump = new EventJump(tarasande_originalYaw, EventJump.State.POST);
            TarasandeMain.Companion.get().getEventDispatcher().call(eventJump);
            setYaw(eventJump.getYaw());
        }
    }

    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;)V", at = @At("HEAD"), cancellable = true)
    public void hookEventSwing(Hand hand, CallbackInfo ci) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventSwing eventSwing = new EventSwing(hand);
            TarasandeMain.Companion.get().getEventDispatcher().call(eventSwing);
            if (eventSwing.getCancelled())
                ci.cancel();
        }
    }
}
