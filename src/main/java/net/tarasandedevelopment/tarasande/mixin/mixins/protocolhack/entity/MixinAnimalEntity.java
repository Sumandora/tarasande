package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnimalEntity.class)
public class MixinAnimalEntity {

    @Redirect(method = "interactMob", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z"))
    public boolean redirectInteractMob(World instance) {
        return instance.isClient && VersionList.isNewerOrEqualTo(ProtocolVersion.v1_15);
    }
}
