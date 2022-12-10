package de.florianmichael.clampclient.injection.mixin.protocolhack.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
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

    @Inject(method = "getActiveEyeHeight", at = @At("HEAD"), cancellable = true)
    public void changeEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_19_1)) {
            cir.setReturnValue(getEyeHeight(pose, dimensions));
        }
    }

    @Inject(method = "getHeightOffset", at = @At("HEAD"), cancellable = true)
    public void changeHeightOffset(CallbackInfoReturnable<Double> cir) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_19_1)) {
            cir.setReturnValue(0.0);
        }
    }
}
