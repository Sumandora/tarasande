package net.tarasandedevelopment.tarasande.mixin.mixins.event.connection.invalidgamemode;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.GameMode;
import su.mandora.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.event.EventInvalidGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(targets = "net.minecraft.network.packet.s2c.play.PlayerListS2CPacket$Action$2")
public class MixinPlayerListS2CPacketUpdateGameMode {

    @Unique
    private UUID tarasande_trackedUUID;

    @Redirect(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;readUuid()Ljava/util/UUID;"))
    public UUID trackUUID(PacketByteBuf instance) {
        this.tarasande_trackedUUID = instance.readUuid();
        return this.tarasande_trackedUUID;
    }

    @Redirect(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;byId(I)Lnet/minecraft/world/GameMode;"))
    public GameMode hookEventInvalidGameMode(int id) {
        if (GameMode.byId(id, null) == null) {
            EventInvalidGameMode eventInvalidGameMode = new EventInvalidGameMode(tarasande_trackedUUID);
            EventDispatcher.INSTANCE.call(eventInvalidGameMode);
        }
        return GameMode.byId(id);
    }
}
