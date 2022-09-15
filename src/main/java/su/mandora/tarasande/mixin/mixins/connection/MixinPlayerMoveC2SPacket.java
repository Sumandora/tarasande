package su.mandora.tarasande.mixin.mixins.connection;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import su.mandora.tarasande.mixin.accessor.IPlayerMoveC2SPacket;

@Mixin(PlayerMoveC2SPacket.class)
public class MixinPlayerMoveC2SPacket implements IPlayerMoveC2SPacket {
    @Mutable
    @Shadow
    @Final
    protected float yaw;

    @Mutable
    @Shadow
    @Final
    protected float pitch;

    @Mutable
    @Shadow
    @Final
    protected boolean onGround;

    @Override
    public void tarasande_setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Override
    public void tarasande_setPitch(float pitch) {
        this.pitch = pitch;
    }

    @Override
    public void tarasande_setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}
