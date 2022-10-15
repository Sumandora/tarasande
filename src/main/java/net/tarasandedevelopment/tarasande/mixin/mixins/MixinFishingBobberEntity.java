package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.entity.projectile.FishingBobberEntity;
import net.tarasandedevelopment.tarasande.mixin.accessor.IFishingBobberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FishingBobberEntity.class)
public class MixinFishingBobberEntity implements IFishingBobberEntity {
    @Shadow
    private boolean caughtFish;

    @Override
    public boolean tarasande_isCaughtFish() {
        return caughtFish;
    }
}
