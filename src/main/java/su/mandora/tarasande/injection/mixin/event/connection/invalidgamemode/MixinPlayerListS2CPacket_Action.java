package su.mandora.tarasande.injection.mixin.event.connection.invalidgamemode;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventInvalidGameMode;

@Mixin(PlayerListS2CPacket.Action.class)
public class MixinPlayerListS2CPacket_Action {

    @Unique
    private static boolean tarasande_isValid;

    @Redirect(method = "method_46338", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;byId(I)Lnet/minecraft/world/GameMode;"))
    private static GameMode trackValidity(int id) {
        tarasande_isValid = id >= 0 && id < GameMode.values().length;
        return GameMode.byId(id);
    }

    @Inject(method = "method_46338", at = @At(value = "FIELD", target = "Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket$Serialized;gameMode:Lnet/minecraft/world/GameMode;", shift = At.Shift.AFTER))
    private static void hookEventInvalidGameMode(PlayerListS2CPacket.Serialized serialized, PacketByteBuf buf, CallbackInfo ci) {
        if (!tarasande_isValid) {
            EventInvalidGameMode eventInvalidGameMode = new EventInvalidGameMode(serialized.profileId);
            EventDispatcher.INSTANCE.call(eventInvalidGameMode);
        }
    }
}
