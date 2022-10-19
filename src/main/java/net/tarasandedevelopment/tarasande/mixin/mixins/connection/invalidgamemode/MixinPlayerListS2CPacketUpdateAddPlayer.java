package net.tarasandedevelopment.tarasande.mixin.mixins.connection.invalidgamemode;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.GameMode;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventInvalidGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.network.packet.s2c.play.PlayerListS2CPacket$Action$1")
public class MixinPlayerListS2CPacketUpdateAddPlayer {

    @Unique
    private GameProfile trackedGameProfile;

    @Redirect(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;readGameProfile()Lcom/mojang/authlib/GameProfile;"))
    public GameProfile trackUUID(PacketByteBuf instance) {
        this.trackedGameProfile = instance.readGameProfile();
        return this.trackedGameProfile;
    }

    @Redirect(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;byId(I)Lnet/minecraft/world/GameMode;"))
    public GameMode fixGameMode(int id) {
        if (GameMode.byId(id, null) == null) {
            EventInvalidGameMode eventInvalidGameMode = new EventInvalidGameMode(trackedGameProfile.getId());
            TarasandeMain.Companion.get().getEventDispatcher().call(eventInvalidGameMode);
        }
        return GameMode.byId(id);
    }
}
