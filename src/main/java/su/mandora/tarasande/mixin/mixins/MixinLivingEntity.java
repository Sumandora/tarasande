package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventJump;
import su.mandora.tarasande.event.EventSwing;
import su.mandora.tarasande.mixin.accessor.ILivingEntity;
import su.mandora.tarasande.util.math.rotation.Rotation;
import su.mandora.tarasande.util.math.rotation.RotationUtil;

@Mixin(value = LivingEntity.class, priority = 999 /* baritone fix */)
public abstract class MixinLivingEntity extends Entity implements ILivingEntity {

    @Shadow
    protected int bodyTrackingIncrements;

    @Shadow
    protected double serverYaw;

    @Shadow
    protected double serverPitch;

    @Shadow
    protected double serverX;

    @Shadow
    protected double serverY;

    @Shadow
    protected double serverZ;

    @Shadow
    protected double serverHeadYaw;
    @Shadow
    protected int lastAttackedTicks;
    private float originalYaw;

    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract float getYaw(float tickDelta);

    @Inject(method = "jump", at = @At("HEAD"))
    public void injectPreJump(CallbackInfo ci) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventJump eventJump = new EventJump(originalYaw = getYaw(), EventJump.State.PRE);
            TarasandeMain.Companion.get().getManagerEvent().call(eventJump);
            setYaw(eventJump.getYaw());
        }
    }

    @Inject(method = "jump", at = @At("TAIL"))
    public void injectPostJump(CallbackInfo ci) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventJump eventJump = new EventJump(originalYaw, EventJump.State.POST);
            TarasandeMain.Companion.get().getManagerEvent().call(eventJump);
            setYaw(eventJump.getYaw());
        }
    }

    @Inject(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;bodyTrackingIncrements:I"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateTrackedPosition(DDD)V"), to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setYaw(F)V")))
    public void injectTickMovement(CallbackInfo ci) {
        if (this.bodyTrackingIncrements > 0 && (Object) this == MinecraftClient.getInstance().player && RotationUtil.INSTANCE.getFakeRotation() != null) {
            Rotation rotation = RotationUtil.INSTANCE.getFakeRotation();
            rotation.setYaw((rotation.getYaw() + (float) MathHelper.wrapDegrees(this.serverYaw - (double) rotation.getYaw()) / (float) this.bodyTrackingIncrements) % 360.0F);
            rotation.setPitch((rotation.getPitch() + (float) (this.serverPitch - (double) rotation.getPitch()) / (float) this.bodyTrackingIncrements) % 360.0F);
        }
    }

    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;)V", at = @At("HEAD"), cancellable = true)
    public void injectSwingHand(Hand hand, CallbackInfo ci) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventSwing eventSwing = new EventSwing(hand);
            TarasandeMain.Companion.get().getManagerEvent().call(eventSwing);
            if (eventSwing.getCancelled())
                ci.cancel();
        }
    }

    @Override
    public double tarasande_getServerX() {
        return serverX;
    }

    @Override
    public double tarasande_getServerY() {
        return serverY;
    }

    @Override
    public double tarasande_getServerZ() {
        return serverZ;
    }

    @Override
    public double tarasande_getServerYaw() {
        return serverYaw;
    }

    @Override
    public double tarasande_getServerPitch() {
        return serverPitch;
    }

    @Override
    public int tarasande_getBodyTrackingIncrements() {
        return bodyTrackingIncrements;
    }

    @Override
    public void tarasande_setBodyTrackingIncrements(int bodyTrackingIncrements) {
        this.bodyTrackingIncrements = bodyTrackingIncrements;
    }

    @Override
    public int tarasande_getLastAttackedTicks() {
        return lastAttackedTicks;
    }

    @Override
    public void tarasande_setLastAttackedTicks(int lastAttackedTicks) {
        this.lastAttackedTicks = lastAttackedTicks;
    }
}
