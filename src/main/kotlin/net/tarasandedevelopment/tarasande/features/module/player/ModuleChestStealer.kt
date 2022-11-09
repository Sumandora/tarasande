package net.tarasandedevelopment.tarasande.features.module.player

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.math.Vec2f
import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventScreenInput
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.player.container.ContainerUtil
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueNumber
import net.tarasandedevelopment.tarasande.value.ValueNumberRange
import net.tarasandedevelopment.tarasande.value.ValueText
import org.apache.commons.lang3.ArrayUtils
import org.lwjgl.glfw.GLFW
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.sqrt

class ModuleChestStealer : Module("Chest stealer", "Takes all items out of a chest", ModuleCategory.PLAYER) {

    private val delay = ValueNumberRange(this, "Delay", 0.0, 100.0, 200.0, 500.0, 1.0)
    private val openDelay = ValueNumber(this, "Open delay", 0.0, 100.0, 500.0, 1.0)
    private val closeDelay = ValueNumber(this, "Close delay", 0.0, 100.0, 500.0, 1.0)
    private val randomize = ValueNumber(this, "Randomize", 0.0, 0.0, 30.0, 1.0) // used to be called parkinson...
    private val checkTitle = ValueBoolean(this, "Check title", false)
    private val titleSubstring = object : ValueText(this, "Title substring", "Chest") {
        override fun isEnabled() = checkTitle.value
    }
    private val failChance = ValueNumber(this, "Fail chance", 0.0, 0.0, 100.0, 1.0)

    private val intelligent = ValueBoolean(this, "Intelligent", true)
    private val keepSameMaterial = object : ValueBoolean(this, "Keep same material", true) {
        override fun isEnabled() = intelligent.value
    }
    private val keepSameEnchantments = object : ValueBoolean(this, "Keep same enchantments", true) {
        override fun isEnabled() = intelligent.value
    }

    private val timeUtil = TimeUtil()

    private var wasClosed = true
    private var mousePos: Vec2f? = null
    private var nextDelay: Long = 0

    private fun intelligent(slot: Slot, list: List<Slot>): Boolean {
        if (!intelligent.value)
            return false
        return ContainerUtil.hasBetterEquivalent(slot.stack, ArrayUtils.addAll(list.toTypedArray(), *ContainerUtil.getValidSlots(mc.player?.playerScreenHandler!!).toTypedArray()).filter { it != slot }.map { it.stack }, keepSameMaterial.value, keepSameEnchantments.value)
    }

    init {
        registerEvent(EventScreenInput::class.java) { event ->
            /*
             * Side story: <=1.12.2 does this in the tick method
             * We do it in the frame
             * Means we can steal faster and on top, we can steal with a low timer, without any issues
             * Maybe 1.8 isn't so good after all for cheating?
             */
            if (event.doneInput)
                return@registerEvent

            if (mc.currentScreen !is GenericContainerScreen) {
                timeUtil.reset()
                wasClosed = true
                mousePos = null
                return@registerEvent
            }

            val accessor = mc.currentScreen as HandledScreen<*>

            if (checkTitle.value) {
                val string = StringUtil.extractContent(accessor.title)
                if (!string.contains(titleSubstring.value))
                    return@registerEvent
            }

            val screenHandler = (mc.currentScreen as GenericContainerScreen).screenHandler

            if (mousePos == null) {
                mousePos = Vec2f(mc.window.scaledWidth / 2f, mc.window.scaledHeight / 2f)
            }

            var nextSlot = ContainerUtil.getClosestSlot(screenHandler, accessor, mousePos!!) { slot, list -> slot.id < screenHandler.inventory.size() && !intelligent(slot, list) }

            if (!timeUtil.hasReached(when {
                    wasClosed -> openDelay.value.toLong()
                    nextSlot == null -> closeDelay.value.toLong()
                    else -> nextDelay
                }))
                return@registerEvent

            wasClosed = false
            timeUtil.reset()

            if (nextSlot == null)
                mc.currentScreen?.close()
            else {
                val displayPos = ContainerUtil.getDisplayPosition(accessor, nextSlot).add(Vec2f(
                    if (randomize.value == 0.0) 0.0f else ThreadLocalRandom.current().nextDouble(-randomize.value, randomize.value).toFloat(),
                    if (randomize.value == 0.0) 0.0f else ThreadLocalRandom.current().nextDouble(-randomize.value, randomize.value).toFloat()
                ))
                if (ThreadLocalRandom.current().nextInt(100) < failChance.value) {
                    val interp = mousePos?.add(displayPos?.add(mousePos?.negate())?.multiply(ThreadLocalRandom.current().nextDouble(0.0, 1.0).toFloat()))!!
                    ContainerUtil.getClosestSlot(screenHandler, accessor, interp) { slot, list -> slot.id < screenHandler.inventory.size() && !intelligent(slot, list) }?.also {
                        nextSlot = it
                    }
                }
                val distance = mousePos?.distanceSquared(displayPos)!!
                mousePos = displayPos
                val mapped = sqrt(distance).div(Vec2f(accessor.backgroundWidth.toFloat(), accessor.backgroundHeight.toFloat()).length())
                nextDelay = (delay.minValue + (delay.maxValue - delay.minValue) * mapped).toLong()
                mc.interactionManager?.clickSlot(screenHandler.syncId, nextSlot?.id!!, GLFW.GLFW_MOUSE_BUTTON_LEFT, SlotActionType.QUICK_MOVE, mc.player)
                event.doneInput = true
            }
        }
    }
}
