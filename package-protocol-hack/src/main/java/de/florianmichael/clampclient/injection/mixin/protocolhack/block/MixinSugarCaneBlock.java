package de.florianmichael.clampclient.injection.mixin.protocolhack.block;

import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.util.VersionListEnum;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SugarCaneBlock.class)
public class MixinSugarCaneBlock {

    @Redirect(method = "canPlaceAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    public boolean changePlaceTarget(BlockState instance, TagKey<Block> tagKey) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_17)) {
            return instance.isOf(Blocks.GRASS_BLOCK) || instance.isOf(Blocks.DIRT) || instance.isOf(Blocks.COARSE_DIRT) || instance.isOf(Blocks.PODZOL);
        }
        return instance.isIn(tagKey);
    }
}
