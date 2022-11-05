package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.entity.passive.WolfEntity;
import net.tarasandedevelopment.tarasande.features.protocol.util.WolfHealthTracker1_14_4;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WolfEntity.class)
public class MixinWolfEntity {

    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/WolfEntity;getHealth()F"))
    public float rewriteHealth(WolfEntity instance) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_14_4)) {
            return WolfHealthTracker1_14_4.INSTANCE.getHealth(instance.getId());
        }

        return instance.getHealth();
    }
}
