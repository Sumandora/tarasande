package de.florianmichael.clampclient.injection.mixin.protocolhack.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.entity.passive.PufferfishEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PufferfishEntity.class)
public class MixinPufferfishEntity {

    @Redirect(method = "readCustomDataFromNbt", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    public int removePuffStateLimit(int a, int b) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_18_2)) {
            return a;
        }
        return Math.min(a, b);
    }
}
