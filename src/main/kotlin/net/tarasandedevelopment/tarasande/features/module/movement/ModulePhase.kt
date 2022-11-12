package net.tarasandedevelopment.tarasande.features.module.movement

import net.minecraft.util.shape.VoxelShapes
import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventCollisionShape
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.util.extension.plus
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.value.impl.ValueMode
import net.tarasandedevelopment.tarasande.value.impl.ValueNumber

class ModulePhase : Module("Phase", "Allows you to move through blocks", ModuleCategory.MOVEMENT) {

    private val mode = ValueMode(this, "Mode", false, "Skip", "Disable collision")
    private val distance = object : ValueNumber(this, "Distance", 0.0, 1.0, 2.0, 0.1) {
        override fun isEnabled() = mode.isSelected(0)
    }
    private val fallThrough = object : ValueBoolean(this, "Fall through", false) {
        override fun isEnabled() = mode.isSelected(1)
    }

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state != EventUpdate.State.PRE_PACKET)
                return@registerEvent

            if (mode.isSelected(0))
                if (mc.player?.horizontalCollision == true) {
                    mc.player?.setPosition(mc.player?.pos!! + Rotation(PlayerUtil.getMoveDirection().toFloat(), 0.0f).forwardVector(distance.value))
                }
        }

        registerEvent(EventCollisionShape::class.java) { event ->
            if (mode.isSelected(1)) {
                if (fallThrough.value || event.pos.y >= mc.player?.blockPos?.y!!)
                    event.collisionShape = VoxelShapes.empty()
            }
        }
    }
}
