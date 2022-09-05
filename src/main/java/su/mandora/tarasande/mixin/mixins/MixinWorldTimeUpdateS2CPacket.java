package su.mandora.tarasande.mixin.mixins;

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import su.mandora.tarasande.mixin.accessor.IWorldTimeUpdateS2CPacket;

@Mixin(WorldTimeUpdateS2CPacket.class)
public class MixinWorldTimeUpdateS2CPacket implements IWorldTimeUpdateS2CPacket {

    @Mutable
    @Shadow
    @Final
    private long timeOfDay;

    @Override
    public void tarasande_setTimeOfDay(long timeOfDay) {
        this.timeOfDay = timeOfDay;
    }
}
