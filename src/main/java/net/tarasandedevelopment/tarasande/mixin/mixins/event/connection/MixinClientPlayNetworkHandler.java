package net.tarasandedevelopment.tarasande.mixin.mixins.event.connection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.tarasandedevelopment.events.EventDispatcher;
import net.tarasandedevelopment.events.impl.EventRotationSet;
import net.tarasandedevelopment.events.impl.EventShowsDeathScreen;
import net.tarasandedevelopment.events.impl.EventVelocity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Shadow
    @Final
    private MinecraftClient client;

    @Redirect(method = "onEntityVelocityUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocityClient(DDD)V"))
    public void hookEventVelocity(Entity entity, double x, double y, double z) {
        EventVelocity eventVelocity = new EventVelocity(x, y, z, EventVelocity.Packet.VELOCITY);
        if (entity == client.player)
            EventDispatcher.INSTANCE.call(eventVelocity);
        if (!eventVelocity.getCancelled())
            entity.setVelocityClient(eventVelocity.getVelocityX(), eventVelocity.getVelocityY(), eventVelocity.getVelocityZ());
    }

    @Redirect(method = "onExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d hookEventVelocityExplosion(Vec3d vec3d, double x, double y, double z) {
        EventVelocity eventVelocity = new EventVelocity(x, y, z, EventVelocity.Packet.EXPLOSION);
        EventDispatcher.INSTANCE.call(eventVelocity);
        return eventVelocity.getCancelled() ? vec3d : vec3d.add(eventVelocity.getVelocityX(), eventVelocity.getVelocityY(), eventVelocity.getVelocityZ());
    }

    @Inject(method = "onPlayerPositionLook", at = @At("TAIL"))
    public void hookEventRotationSet(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        EventDispatcher.INSTANCE.call(new EventRotationSet(client.player.getYaw(), client.player.getPitch()));
    }

    @Redirect(method = "onDeathMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;showsDeathScreen()Z"))
    public boolean hookEventRespawn(ClientPlayerEntity instance) {
        EventShowsDeathScreen eventShowsDeathScreen = new EventShowsDeathScreen(instance.showsDeathScreen());
        EventDispatcher.INSTANCE.call(eventShowsDeathScreen);
        return eventShowsDeathScreen.getShowsDeathScreen();
    }
}