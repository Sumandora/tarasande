/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 7/9/22, 10:06 AM
 *
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.0--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license.
 */

package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.block;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.InfestedBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class MixinBlock {

    @Inject(method = "getBlastResistance", at = @At("RETURN"), cancellable = true)
    private void modifyBlastResistance(CallbackInfoReturnable<Float> ci) {
        final Block block = ((Block) (Object) this);

        if (VersionList.isOlderOrEqualTo(VersionList.R1_14_4))
            if (block == Blocks.END_STONE_BRICKS || block == Blocks.END_STONE_BRICK_SLAB || block == Blocks.END_STONE_BRICK_STAIRS || block == Blocks.END_STONE_BRICK_WALL)
                ci.setReturnValue(0.8F);

        if (VersionList.isOlderOrEqualTo(VersionList.R1_15_2))
            if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON || block == Blocks.PISTON_HEAD)
                ci.setReturnValue(0.5F);

        if (VersionList.isOlderOrEqualTo(VersionList.R1_16_5))
            if (block instanceof InfestedBlock)
                ci.setReturnValue(0.75F);
    }
}
