package de.florianmichael.clampclient.injection.mixin.protocolhack.entity;

import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.util.VersionListEnum;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VexEntity.class)
public class MixinVexEntity extends HostileEntity {

    public MixinVexEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getHeightOffset", at = @At("HEAD"), cancellable = true)
    public void changeHeightOffset(CallbackInfoReturnable<Double> cir) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_19_1tor1_19_2)) {
            cir.setReturnValue(0.0);
        }
    }
}
