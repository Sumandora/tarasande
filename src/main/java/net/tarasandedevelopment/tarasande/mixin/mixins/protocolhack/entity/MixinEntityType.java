package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.entity;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.tarasandedevelopment.tarasande.protocolhack.fix.EntityDimensionReplacement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityType.class)
public class MixinEntityType {

    @Shadow @Final private EntityDimensions dimensions;

    @Redirect(method = {"getDimensions", "getWidth", "getHeight"}, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityType;dimensions:Lnet/minecraft/entity/EntityDimensions;"))
    public EntityDimensions changeDimensions(EntityType instance) {
        final EntityDimensions wrapped = EntityDimensionReplacement.INSTANCE.wrapDimension(instance);
        if (wrapped != null) {
            return wrapped;
        }
        return dimensions;
    }
}