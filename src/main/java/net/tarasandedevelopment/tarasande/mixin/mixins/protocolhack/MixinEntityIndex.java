package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.EntityIndex;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;

@Mixin(EntityIndex.class)
public class MixinEntityIndex<T extends EntityLike> {

    @Shadow
    @Final
    private Int2ObjectMap<T> idToEntity;

    @Redirect(method = "add", at = @At(value = "INVOKE", target = "Ljava/util/Map;containsKey(Ljava/lang/Object;)Z", remap = false))
    private boolean allowDuplicateUuid(Map<UUID, T> instance, Object o) {
        return instance.containsKey(o) && VersionList.isNewerTo(ProtocolVersion.v1_16_4);
    }

    @Inject(method = "size", at = @At("HEAD"), cancellable = true)
    private void returnRealSize(CallbackInfoReturnable<Integer> cir) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_16_4))
            cir.setReturnValue(this.idToEntity.size());
    }
}
