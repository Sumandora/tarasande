package net.tarasandedevelopment.tarasande.mixin.mixins.event.connection.invalidgamemode;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.GameMode;
import net.tarasandedevelopment.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.events.EventInvalidGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.network.packet.s2c.play.PlayerListS2CPacket$Action$1")
public class MixinPlayerListS2CPacketUpdateAddPlayer {

    @Unique
    private GameProfile tarasande_trackedGameProfile;

    @Redirect(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;readGameProfile()Lcom/mojang/authlib/GameProfile;"))
    public GameProfile trackGameProfile(PacketByteBuf instance) {
        this.tarasande_trackedGameProfile = instance.readGameProfile();
        return this.tarasande_trackedGameProfile;
    }

    @Redirect(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;byId(I)Lnet/minecraft/world/GameMode;"))
    public GameMode hookEventInvalidGameMode(int id) {
        if (GameMode.byId(id, null) == null) {
            EventInvalidGameMode eventInvalidGameMode = new EventInvalidGameMode(tarasande_trackedGameProfile.getId());
            EventDispatcher.INSTANCE.call(eventInvalidGameMode);
        }
        return GameMode.byId(id);
    }
}
