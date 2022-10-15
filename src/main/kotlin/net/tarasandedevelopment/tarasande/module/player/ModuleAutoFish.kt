package net.tarasandedevelopment.tarasande.module.player

import net.minecraft.item.FishingRodItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.mixin.accessor.IFishingBobberEntity
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.util.player.projectile.ProjectileUtil

class ModuleAutoFish : Module("Auto fish", "Automates fishing", ModuleCategory.PLAYER) {

    init {
        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.useKey) {
                if (
                    !PlayerUtil.isPlayerMoving() &&

                    (mc.player?.mainHandStack?.item is FishingRodItem ||
                            mc.player?.offHandStack?.item is FishingRodItem)
                )
                    if (mc.player?.fishHook == null) {
                        val lastPos = ProjectileUtil.predict(ItemStack(Items.FISHING_ROD), RotationUtil.fakeRotation ?: Rotation(mc.player!!), false).lastOrNull() ?: return@registerEvent
                        if (mc.world?.getBlockState(BlockPos(lastPos))?.fluidState?.isEmpty == false)
                            event.pressed = true
                    } else if ((mc.player?.fishHook as IFishingBobberEntity).tarasande_isCaughtFish() || (mc.player?.fishHook?.isOnGround == true && mc.player?.fishHook?.isTouchingWater == false)) {
                        event.pressed = true
                    }
            }
        }
    }

}