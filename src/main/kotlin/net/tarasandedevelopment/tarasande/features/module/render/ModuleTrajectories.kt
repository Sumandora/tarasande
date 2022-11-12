package net.tarasandedevelopment.tarasande.features.module.render

import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventRender3D
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.prediction.projectile.ProjectileUtil
import net.tarasandedevelopment.tarasande.util.render.RenderUtilimport net.tarasandedevelopment.tarasande.value.impl.ValueBoolean

class ModuleTrajectories : Module("Trajectories", "Renders paths of trajectories", ModuleCategory.RENDER) {

    private val predictVelocity = ValueBoolean(this, "Predict velocity", false)

    init {
        registerEvent(EventRender3D::class.java) { event ->
            var stack: ItemStack? = null; Hand.values().forEach { hand ->
            if (ProjectileUtil.projectileItems.any {
                    mc.player?.getStackInHand(hand)?.item?.let { item ->
                        it.isSame(item)
                    } == true
                }) stack = mc.player?.getStackInHand(hand)
        }

            if (stack != null) {
                RenderUtil.renderPath(event.matrices, ProjectileUtil.predict(stack!!, RotationUtil.fakeRotation, predictVelocity.value), -1)
            }
        }
    }
}