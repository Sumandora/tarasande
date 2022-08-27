package su.mandora.tarasande.mixin.mixins.connection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventRotationSet;
import su.mandora.tarasande.event.EventVelocity;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Shadow
    @Final
    private MinecraftClient client;

    @Redirect(method = "onEntityVelocityUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocityClient(DDD)V"))
    public void hookedSetVelocityClient_onVelocityUpdate(Entity entity, double x, double y, double z) {
        EventVelocity eventVelocity = new EventVelocity(x, y, z, EventVelocity.Packet.VELOCITY);
        if (entity == client.player)
            TarasandeMain.Companion.get().getManagerEvent().call(eventVelocity);
        if (!eventVelocity.getCancelled())
            entity.setVelocityClient(eventVelocity.getVelocityX(), eventVelocity.getVelocityY(), eventVelocity.getVelocityZ());
    }

    @Redirect(method = "onExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d hookedSetVelocityClient_onExplosion(Vec3d vec3d, double x, double y, double z) {
        EventVelocity eventVelocity = new EventVelocity(x, y, z, EventVelocity.Packet.EXPLOSION);
        TarasandeMain.Companion.get().getManagerEvent().call(eventVelocity);
        return eventVelocity.getCancelled() ? vec3d : vec3d.add(eventVelocity.getVelocityX(), eventVelocity.getVelocityY(), eventVelocity.getVelocityZ());
    }

    @Redirect(method = "onPlayerPositionLook", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;updatePositionAndAngles(DDDFF)V"))
    public void hookedUpdatePositionAndAngles(PlayerEntity instance, double x, double y, double z, float yaw, float pitch) {
        EventRotationSet eventRotationSet = new EventRotationSet(yaw, pitch);
        TarasandeMain.Companion.get().getManagerEvent().call(eventRotationSet);
        instance.updatePositionAndAngles(x, y, z, eventRotationSet.getYaw(), eventRotationSet.getPitch());
    }

}
