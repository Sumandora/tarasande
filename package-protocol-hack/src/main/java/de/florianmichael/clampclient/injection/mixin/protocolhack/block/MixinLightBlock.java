package de.florianmichael.clampclient.injection.mixin.protocolhack.block;

import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.util.VersionListEnum;
import net.minecraft.block.LightBlock;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightBlock.class)
public class MixinLightBlock {

    // Not relevant for GamePlay
    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isCreativeLevelTwoOp()Z"))
    public boolean removeIf(PlayerEntity instance) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_19_1tor1_19_2)) {
            return true;
        }
        return instance.isCreativeLevelTwoOp();
    }
}
