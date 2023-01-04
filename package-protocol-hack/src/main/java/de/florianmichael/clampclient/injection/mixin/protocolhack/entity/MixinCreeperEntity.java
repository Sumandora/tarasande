package de.florianmichael.clampclient.injection.mixin.protocolhack.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.util.VersionList;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(CreeperEntity.class)
public class MixinCreeperEntity {

    @Redirect(method = "interactMob", at = @At(value = "FIELD", target = "Lnet/minecraft/sound/SoundEvents;ITEM_FIRECHARGE_USE:Lnet/minecraft/sound/SoundEvent;"))
    public SoundEvent fixSound() {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_19_1)) {
            return SoundEvents.ITEM_FLINTANDSTEEL_USE;
        }
        return SoundEvents.ITEM_FIRECHARGE_USE;
    }
}
