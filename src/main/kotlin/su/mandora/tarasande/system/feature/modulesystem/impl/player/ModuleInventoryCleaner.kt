package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.math.Vec2f
import su.mandora.tarasande.event.impl.EventScreenInput
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumberRange
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.DEFAULT_CONTAINER_HEIGHT
import su.mandora.tarasande.util.DEFAULT_CONTAINER_WIDTH
import su.mandora.tarasande.util.extension.kotlinruntime.nullOr
import su.mandora.tarasande.util.math.time.TimeUtil
import su.mandora.tarasande.util.player.container.Cleaner
import su.mandora.tarasande.util.player.container.ContainerUtil
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.sqrt

class ModuleInventoryCleaner : Module("Inventory cleaner", "Drops unwanted items in your inventory", ModuleCategory.PLAYER) {

    private val openInventory = ValueBoolean(this, "Open inventory", true)
    private val delay = ValueNumberRange(this, "Delay", 0.0, 100.0, 200.0, 500.0, 1.0)
    private val openDelay = ValueNumber(this, "Open delay", 0.0, 100.0, 500.0, 1.0, isEnabled = { openInventory.value })
    private val randomize = ValueNumber(this, "Randomize", 0.0, 0.0, 30.0, 1.0)

    private val cleaner = Cleaner(this)

    private val timeUtil = TimeUtil()

    private var wasClosed = true
    private var mousePos: Vec2f? = null
    private var nextDelay: Long = 0

    init {
        registerEvent(EventScreenInput::class.java) { event ->
            if (event.doneInput)
                return@registerEvent

            if (mc.player == null || (openInventory.value && mc.currentScreen !is AbstractInventoryScreen<*>)) {
                timeUtil.reset()
                wasClosed = true
                mousePos = null
                return@registerEvent
            }

            val screenHandler = mc.player!!.playerScreenHandler

            if (!screenHandler.cursorStack.nullOr { it.isEmpty })
                return@registerEvent

            if (mousePos == null) {
                mousePos = Vec2f(DEFAULT_CONTAINER_WIDTH / 2F, DEFAULT_CONTAINER_HEIGHT / 2F)
            }

            val nextSlot = ContainerUtil.getClosestSlot(screenHandler, mousePos!!) { slot, list -> slot.hasStack() && cleaner.hasBetterEquivalent(slot.stack, list.filter { it != slot }.map { it.stack }) }

            if (!timeUtil.hasReached(
                    if (wasClosed && openInventory.value)
                        openDelay.value.toLong()
                    else nextDelay
                ))
                return@registerEvent

            wasClosed = false
            timeUtil.reset()

            if (nextSlot != null) {
                val displayPos = ContainerUtil.getDisplayPosition(nextSlot).add(Vec2f(
                    if (randomize.value == 0.0) 0F else ThreadLocalRandom.current().nextDouble(-randomize.value, randomize.value).toFloat(),
                    if (randomize.value == 0.0) 0F else ThreadLocalRandom.current().nextDouble(-randomize.value, randomize.value).toFloat()
                ))
                val distance = mousePos?.distanceSquared(displayPos)!!
                mousePos = displayPos
                val mapped = sqrt(distance).div(Vec2f(DEFAULT_CONTAINER_WIDTH.toFloat(), DEFAULT_CONTAINER_HEIGHT.toFloat()).length())
                nextDelay = delay.interpolate(mapped.toDouble()).toLong()
                mc.interactionManager?.clickSlot(screenHandler.syncId, nextSlot.id, 1 /* 1 = all; 0 = single */, SlotActionType.THROW, mc.player)
                event.doneInput = true
            }
        }
    }

}
