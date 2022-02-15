package su.mandora.tarasande.mixin.mixins;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import su.mandora.tarasande.mixin.accessor.IWorld;

@Mixin(World.class)
public abstract class MixinWorld implements IWorld {
    @Mutable
    @Shadow @Final public boolean isClient;

    @Override
    public void setIsClient(boolean isClient) {
        this.isClient = isClient;
    }
}
