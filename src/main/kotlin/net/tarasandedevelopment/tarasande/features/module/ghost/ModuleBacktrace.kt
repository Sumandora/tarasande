package net.tarasandedevelopment.tarasande.features.module.ghost

import net.minecraft.entity.Entity
import net.minecraft.util.math.Box
import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventBoundingBoxOverride
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mixin.accessor.IGameRenderer
import net.tarasandedevelopment.tarasande.util.extension.plus
import net.tarasandedevelopment.tarasande.util.math.MathUtil
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.impl.ValueNumber

class ModuleBacktrace : Module("Backtrace", "Allows you to trace back enemy hit boxes", ModuleCategory.GHOST) {

    private val ticks = ValueNumber(this, "Ticks", 0.0, 5.0, 20.0, 1.0)

    private val boundingBoxes = HashMap<Entity, ArrayList<Box>>()

    init {
        registerEvent(EventBoundingBoxOverride::class.java) { event ->
            val playerRotation = Rotation(mc.player!!)
            val playerEye = mc.player?.eyePos
            val rotationVec = mc.player?.eyePos!! + playerRotation.forwardVector((mc.gameRenderer as IGameRenderer).tarasande_getReach())
            event.boundingBox = boundingBoxes[event.entity]?.filter { it.raycast(playerEye, rotationVec).isPresent }?.minByOrNull { playerEye?.squaredDistanceTo(MathUtil.closestPointToBox(playerEye, it))!! } ?: return@registerEvent
        }

        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                for (entity in mc.world?.entities!!)
                    if (PlayerUtil.isAttackable(entity))
                        boundingBoxes.computeIfAbsent(entity) { ArrayList() }.also {
                            if (entity.boundingBox != null)
                                it.add(entity.boundingBox)
                            while (it.size > ticks.value)
                                it.removeAt(0)
                        }
            }
        }
    }

}