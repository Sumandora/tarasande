package net.tarasandedevelopment.tarasande.module.movement

import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShapes
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventCollisionShape
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.value.ValueNumber

class ModuleJesus : Module("Jesus", "Lets you walk on liquids", ModuleCategory.MOVEMENT) {

    private val height = ValueNumber(this, "Height", 0.0, 1.0, 1.0, 0.01)
    private val jumpMotion = ValueNumber(this, "Jump motion", 0.0, 0.0, 1.0, 0.01)

    init {
        registerEvent(EventCollisionShape::class.java) { event ->
            if (mc.player?.isSubmergedInWater == true || mc.player?.isInLava == true || mc.player?.input?.sneaking == true)
                return@registerEvent
            if (event.pos.y < (mc.player?.y ?: return@registerEvent))
                if (mc.world?.getBlockState(event.pos)?.fluidState?.isEmpty == false) {
                    event.collisionShape = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, height.value * (mc.world?.getBlockState(event.pos)?.fluidState?.height?.toDouble() ?: 1.0), 1.0)
                }
        }

        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE)
                if (mc.world?.getBlockState(mc.player?.blockPos?.add(0.0, mc.player?.stepHeight?.toDouble()?.times(-1)!!, 0.0))?.fluidState?.isEmpty == false && mc.player?.isOnGround == true) {
                    if (jumpMotion.value > 0.0) {
                        val prevVelocity = mc.player?.velocity?.add(0.0, 0.0, 0.0)!!
                        mc.player?.jump()
                        mc.player?.velocity = prevVelocity.withAxis(Direction.Axis.Y, mc.player?.velocity?.y?.times(jumpMotion.value)!!)
                    }
                }
        }
    }

}