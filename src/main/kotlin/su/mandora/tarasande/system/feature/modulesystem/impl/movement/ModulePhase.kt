package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.util.shape.VoxelShapes
import su.mandora.tarasande.event.impl.EventCollisionShape
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.minecraft.plus
import su.mandora.tarasande.util.extension.minecraft.times
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.player.PlayerUtil

class ModulePhase : Module("Phase", "Allows you to move through blocks", ModuleCategory.MOVEMENT) {

    private val mode = ValueMode(this, "Mode", false, "Skip", "Disable collision")
    private val distance = ValueNumber(this, "Distance", 0.0, 1.0, 2.0, 0.1, isEnabled = { mode.isSelected(0) })
    private val fallThrough = ValueBoolean(this, "Fall through", false, isEnabled = { mode.isSelected(1) })

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state != EventUpdate.State.PRE_PACKET)
                return@registerEvent

            if (mode.isSelected(0))
                if (mc.player?.horizontalCollision == true) {
                    mc.player?.setPosition(mc.player?.pos!! + Rotation(PlayerUtil.getMoveDirection().toFloat(), 0F).forwardVector() * distance.value)
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
