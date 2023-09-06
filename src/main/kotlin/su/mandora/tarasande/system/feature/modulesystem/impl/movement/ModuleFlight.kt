package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.entity.Entity
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.math.Direction.Axis
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShapes
import su.mandora.tarasande.event.impl.EventCollisionShape
import su.mandora.tarasande.event.impl.EventJump
import su.mandora.tarasande.event.impl.EventMovement
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.minecraft.math.plus
import su.mandora.tarasande.util.extension.minecraft.math.times
import su.mandora.tarasande.util.math.MathUtil
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.feature.rotation.api.Rotation
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleFlight : Module("Flight", "Allows flight in non-creative modes", ModuleCategory.MOVEMENT) {

    val mode = ValueMode(this, "Mode", false, "Vanilla", "Motion", "Air walk", "Packet")
    val flightSpeed = ValueNumber(this, "Flight speed", 0.0, 1.0, 5.0, 0.1, isEnabled = { mode.isSelected(0) || mode.isSelected(1) })
    private val baseYMotion = ValueNumber(this, "Base Y-motion", -1.0, 0.0, 1.0, 0.01, isEnabled = { mode.isSelected(1) })
    private val distance = ValueNumber(this, "Distance", 0.0, 1.0, 5.0, 0.1, isEnabled = { mode.isSelected(3) })
    private val delay = ValueNumber(this, "Delay", 0.0, 200.0, 1000.0, 50.0, isEnabled = { mode.isSelected(3) })
    private val order = ValueMode(this, "Order", false, "Valid first", "Invalid first", isEnabled = { mode.isSelected(3) })
    private val onGround = ValueMode(this, "On ground", true, "Valid packet", "Invalid packet", isEnabled = { mode.isSelected(3) })
    private val packetType = ValueMode(this, "Packet type", false, "Position", "Position and rotation", isEnabled = { mode.isSelected(3) })
    private val xAxis = PacketFlightAxis(this, Axis.X, isEnabled = { mode.isSelected(3) })
    private val yAxis = PacketFlightAxis(this, Axis.Y, isEnabled = { mode.isSelected(3) })
    private val zAxis = PacketFlightAxis(this, Axis.Z, isEnabled = { mode.isSelected(3) })
    private val repeatValid = ValueNumber(this, "Repeat valid", 1.0, 1.0, 5.0, 1.0, isEnabled = { mode.isSelected(3) })
    private val repeatInvalid = ValueNumber(this, "Repeat invalid", 1.0, 1.0, 5.0, 1.0, isEnabled = { mode.isSelected(3) })
    private val hardStop = ValueBoolean(this, "Hard stop", false)

    val timeUtil = TimeUtil()

    override fun onDisable() {
        if (hardStop.value)
            mc.player?.velocity = Vec3d.ZERO
    }

    init {
        registerEvent(EventMovement::class.java, 1002) { event ->
            if (event.entity != mc.player)
                return@registerEvent

            if (!mode.isSelected(1))
                return@registerEvent

            var yMotion = 0.0

            if (PlayerUtil.input.jumping)
                yMotion += flightSpeed.value
            if (PlayerUtil.input.sneaking)
                yMotion -= flightSpeed.value

            if (yMotion == 0.0)
                yMotion = baseYMotion.value

            event.velocity = Entity.movementInputToVelocity(Vec3d(
                MathUtil.roundAwayFromZero(PlayerUtil.input.movementSideways.toDouble()),
                0.0,
                MathUtil.roundAwayFromZero(PlayerUtil.input.movementForward.toDouble())
            ), flightSpeed.value.toFloat(), mc.player?.yaw!!)
            event.velocity = Vec3d(event.velocity.x, yMotion, event.velocity.z)
        }

        registerEvent(EventCollisionShape::class.java) { event ->
            if (!mode.isSelected(2))
                return@registerEvent
            if (!event.collisionShape.isEmpty)
                return@registerEvent
            val offset = (mc.player ?: return@registerEvent).y - event.pos.y
            if (offset in 0.0..1.0)
                event.collisionShape = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, offset, 1.0)
        }

        registerEvent(EventJump::class.java) { event ->
            if (event.state == EventJump.State.PRE && mode.isSelected(2))
                event.cancelled = true
        }

        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE)
                if (mode.isSelected(3)) {
                    event.cancelled = true
                    if (!timeUtil.hasReached(delay.value.toLong()))
                        return@registerEvent

                    timeUtil.reset()

                    val forward = mc.player!!.pos + Rotation(mc.player!!).forwardVector() * distance.value
                    fun makePacket(x: Double, y: Double, z: Double, ground: Boolean): PlayerMoveC2SPacket {
                        return when {
                            packetType.isSelected(0) -> PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, ground)
                            packetType.isSelected(1) -> PlayerMoveC2SPacket.Full(x, y, z, mc.player!!.yaw, mc.player!!.pitch, ground)
                            else -> error("Invalid packet type")
                        }
                    }

                    when {
                        order.isSelected(0) -> {
                            repeat(repeatValid.value.toInt()) {
                                mc.networkHandler?.sendPacket(makePacket(forward.x, forward.y, forward.z, onGround.isSelected(0)))
                            }
                            repeat(repeatInvalid.value.toInt()) {
                                mc.networkHandler?.sendPacket(makePacket(xAxis(mc.player!!.x, forward.x), yAxis(mc.player!!.y, forward.y), zAxis(mc.player!!.z, forward.z), onGround.isSelected(1)))
                            }
                        }

                        order.isSelected(1) -> {
                            repeat(repeatInvalid.value.toInt()) {
                                mc.networkHandler?.sendPacket(makePacket(xAxis(mc.player!!.x, forward.x), yAxis(mc.player!!.y, forward.y), zAxis(mc.player!!.z, forward.z), onGround.isSelected(1)))
                            }
                            repeat(repeatValid.value.toInt()) {
                                mc.networkHandler?.sendPacket(makePacket(forward.x, forward.y, forward.z, onGround.isSelected(0)))
                            }
                        }
                    }
                }
        }
    }

    inner class PacketFlightAxis(owner: Any, axis: Axis, isEnabled: () -> Boolean) {
        private val mode = ValueMode(owner, axis.getName().uppercase() + " mode", false, "Unmodified", "Offset", "Absolute", "Min value", "Max value", "Positive infinity", "Negative infinity", "NaN", isEnabled = isEnabled)
        private val useNewPosition = ValueBoolean(owner, axis.getName().uppercase() + ": Use old position", false, isEnabled = { isEnabled() && (mode.isSelected(0) || mode.isSelected(1)) })
        private val operand = ValueNumber(owner, axis.getName().uppercase() + " operand", -1000.0, 1000.0, 1000.0, 0.1, isEnabled = { isEnabled() && (mode.isSelected(1) || mode.isSelected(2)) })

        init {
            if (axis == Axis.Y) // Defaults that are likely to work
                mode.select(1)
        }

        operator fun invoke(oldPos: Double, newPos: Double): Double {
            val pos = if (useNewPosition.value) newPos else oldPos
            return when {
                mode.isSelected(1) -> pos + operand.value
                mode.isSelected(2) -> operand.value
                mode.isSelected(3) -> Double.MIN_VALUE
                mode.isSelected(4) -> Double.MAX_VALUE
                mode.isSelected(5) -> Double.POSITIVE_INFINITY
                mode.isSelected(6) -> Double.NEGATIVE_INFINITY
                mode.isSelected(7) -> Double.NaN
                else /*mode.isSelected(0)*/ -> pos
            }
        }
    }
}
