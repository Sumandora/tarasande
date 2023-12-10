package su.mandora.tarasande.system.feature.modulesystem.impl.render

import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import su.mandora.tarasande.event.impl.EventRender3D
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.player.prediction.projectile.ProjectileUtil
import su.mandora.tarasande.util.render.RenderUtil

class ModuleTrajectories : Module("Trajectories", "Renders paths of trajectories", ModuleCategory.RENDER) {

    private val predictVelocity = ValueBoolean(this, "Predict velocity", true)

    init {
        registerEvent(EventRender3D::class.java) { event ->
            if(event.state != EventRender3D.State.POST) return@registerEvent

            var stack: ItemStack? = null; Hand.entries.forEach { hand ->
            if (ProjectileUtil.projectileItems.any {
                    mc.player?.getStackInHand(hand)?.item?.let { item ->
                        it.isSame(item)
                    } == true
                }) stack = mc.player?.getStackInHand(hand)
        }

            if (stack != null) {
                RenderUtil.renderPath(event.matrices, ProjectileUtil.predict(stack!!, Rotations.fakeRotation, predictVelocity.value).also { if (it.size >= 1) it.removeFirst() }, -1)
            }
        }
    }
}
