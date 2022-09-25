package su.mandora.tarasande.mixin.mixins;

import net.minecraft.entity.TntEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import su.mandora.tarasande.mixin.accessor.ITntEntity;

@Mixin(TntEntity.class)
public class MixinTntEntity implements ITntEntity {
    @Shadow
    @Final
    private static int DEFAULT_FUSE;

    public int tarasande_getMaxFuse() {
        return DEFAULT_FUSE;
    }
}