package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.entity.TntEntity;
import net.tarasandedevelopment.tarasande.mixin.accessor.ITntEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TntEntity.class)
public class MixinTntEntity implements ITntEntity {
    @Shadow
    @Final
    private static int DEFAULT_FUSE;

    public int tarasande_getMaxFuse() {
        return DEFAULT_FUSE;
    }
}
