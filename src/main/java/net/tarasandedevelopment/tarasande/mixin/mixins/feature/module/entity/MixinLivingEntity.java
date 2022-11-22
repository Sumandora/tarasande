package net.tarasandedevelopment.tarasande.mixin.mixins.feature.module.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.movement.ModuleFastClimb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = LivingEntity.class, priority = 999 /* baritone fix */)
public abstract class MixinLivingEntity extends Entity {

    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract float getYaw(float tickDelta);

    @Shadow
    public abstract boolean isClimbing();


    @ModifyConstant(method = "applyMovementInput", constant = @Constant(doubleValue = 0.2))
    public double hookFastClimb(double original) {
        if (isClimbing()) {
            ModuleFastClimb moduleFastClimb = TarasandeMain.Companion.managerModule().get(ModuleFastClimb.class);
            if (moduleFastClimb.getEnabled())
                original *= moduleFastClimb.getMultiplier().getValue();
        }
        return original;
    }
}
