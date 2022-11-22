package net.tarasandedevelopment.tarasande.mixin.mixins.feature.module.norender;

import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ClientWorld.class)
public class MixinClientWorld {

    @ModifyArgs(method = "doRandomBlockDisplayTicks", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;randomBlockDisplayTick(IIIILnet/minecraft/util/math/random/Random;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos$Mutable;)V"))
    private void noRender_doRandomBlockDisplayTicks(Args args) {
        if (TarasandeMain.Companion.managerModule().get(ModuleNoRender.class).getWorld().getBarrierInvisibility().should()) {
            args.set(5, Blocks.BARRIER);
        }
    }
}
