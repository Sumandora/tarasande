package su.mandora.tarasande.injection.mixin.event.connection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventInvalidPlayerInfo;
import su.mandora.tarasande.event.impl.EventRotationSet;
import su.mandora.tarasande.event.impl.EventShowsDeathScreen;
import su.mandora.tarasande.event.impl.EventVelocity;

import java.util.Iterator;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Redirect(method = "onEntityVelocityUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocityClient(DDD)V"))
    public void hookEventVelocity(Entity entity, double x, double y, double z) {
        EventVelocity eventVelocity = new EventVelocity(x, y, z, EventVelocity.Packet.VELOCITY);
        if (entity == MinecraftClient.getInstance().player)
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
        EventDispatcher.INSTANCE.call(new EventRotationSet(MinecraftClient.getInstance().player.getYaw(), MinecraftClient.getInstance().player.getPitch()));
    }

    @Redirect(method = "onDeathMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;showsDeathScreen()Z"))
    public boolean hookEventRespawn(ClientPlayerEntity instance) {
        EventShowsDeathScreen eventShowsDeathScreen = new EventShowsDeathScreen(instance.showsDeathScreen());
        EventDispatcher.INSTANCE.call(eventShowsDeathScreen);
        return eventShowsDeathScreen.getShowsDeathScreen();
    }

    @Inject(method = "onPlayerList", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", remap = false), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void hookEventInvalidPlayerInfo(PlayerListS2CPacket packet, CallbackInfo ci, Iterator<?> var2, PlayerListS2CPacket.Entry entry, PlayerListEntry playerListEntry) {
        EventInvalidPlayerInfo eventInvalidPlayerInfo = new EventInvalidPlayerInfo(entry.profileId());
        EventDispatcher.INSTANCE.call(eventInvalidPlayerInfo);
    }
}
