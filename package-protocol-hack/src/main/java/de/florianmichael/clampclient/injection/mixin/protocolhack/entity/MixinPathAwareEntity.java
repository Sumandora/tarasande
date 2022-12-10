package de.florianmichael.clampclient.injection.mixin.protocolhack.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.entity.mob.PathAwareEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PathAwareEntity.class)
public class MixinPathAwareEntity {

    @Redirect(method = "updateLeash", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/PathAwareEntity;limitFallDistance()V"))
    public void removeFallDistanceLimitation(PathAwareEntity instance) {
        if (VersionList.isNewerOrEqualTo(ProtocolVersion.v1_19_3)) {
            instance.limitFallDistance();
        }
    }
}
