package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.math.Vec2f
import net.tarasandedevelopment.tarasande.event.EventScreenInput
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumberRange
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.extension.mc
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.player.container.ContainerUtil
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.sqrt

class ModuleInventoryCleaner : Module("Inventory cleaner", "Drops items in your inventory", ModuleCategory.PLAYER) {

    private val openInventory = ValueBoolean(this, "Open inventory", true)
    private val delay = ValueNumberRange(this, "Delay", 0.0, 100.0, 200.0, 500.0, 1.0)
    private val openDelay = object : ValueNumber(this, "Open delay", 0.0, 100.0, 500.0, 1.0) {
        override fun isEnabled() = openInventory.value
    }
    private val randomize = ValueNumber(this, "Randomize", 0.0, 0.0, 30.0, 1.0)

    private val keepSameMaterial = ValueBoolean(this, "Keep same material", true)
    private val keepSameEnchantments = ValueBoolean(this, "Keep same enchantments", true)

    private val timeUtil = TimeUtil()

    private var wasClosed = true
    private var mousePos: Vec2f? = null
    private var nextDelay: Long = 0

    init {
        registerEvent(EventScreenInput::class.java) { event ->
            if (event.doneInput)
                return@registerEvent

            if (openInventory.value && mc.currentScreen !is AbstractInventoryScreen<*>) {
                timeUtil.reset()
                wasClosed = true
                mousePos = null
                return@registerEvent
            }

            val accessor = mc.currentScreen as HandledScreen<*>

            val screenHandler = mc.player?.playerScreenHandler!!

            if(screenHandler.cursorStack?.isEmpty == false)
                return@registerEvent

            if (mousePos == null) {
                mousePos = Vec2f(mc.window.scaledWidth / 2f, mc.window.scaledHeight / 2f)
            }

            val nextSlot = ContainerUtil.getClosestSlot(screenHandler, accessor, mousePos!!) { slot, list -> slot.hasStack() && ContainerUtil.hasBetterEquivalent(slot.stack, list.filter { it != slot }.map { it.stack }, keepSameMaterial.value, keepSameEnchantments.value) }

            if (!timeUtil.hasReached(
                    if (wasClosed && !openInventory.value)
                        openDelay.value.toLong()
                    else nextDelay
                ))
                return@registerEvent

            wasClosed = false
            timeUtil.reset()

            if (nextSlot != null) {
                val displayPos = ContainerUtil.getDisplayPosition(accessor, nextSlot).add(Vec2f(
                    if (randomize.value == 0.0) 0.0F else ThreadLocalRandom.current().nextDouble(-randomize.value, randomize.value).toFloat(),
                    if (randomize.value == 0.0) 0.0F else ThreadLocalRandom.current().nextDouble(-randomize.value, randomize.value).toFloat()
                ))
                val distance = mousePos?.distanceSquared(displayPos)!!
                mousePos = displayPos
                val mapped = sqrt(distance).div(Vec2f(accessor.backgroundWidth.toFloat(), accessor.backgroundHeight.toFloat()).length())
                nextDelay = delay.interpolate(mapped.toDouble()).toLong()
                mc.interactionManager?.clickSlot(screenHandler.syncId, nextSlot.id, 1 /* 1 = all; 0 = single */, SlotActionType.THROW, mc.player)
            }
        }
    }

}