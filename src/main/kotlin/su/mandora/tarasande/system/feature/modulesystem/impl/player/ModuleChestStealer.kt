package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.math.Vec2f
import org.apache.commons.lang3.ArrayUtils
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.event.impl.EventScreenInput
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.*
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.kotlinruntime.nullOr
import su.mandora.tarasande.util.extension.minecraft.extractContent
import su.mandora.tarasande.util.extension.minecraft.safeCount
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.util.player.container.Cleaner
import su.mandora.tarasande.util.player.container.ContainerUtil
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.sqrt

class ModuleChestStealer : Module("Chest stealer", "Takes all items out of a chest", ModuleCategory.PLAYER) {

    private val delay = ValueNumberRange(this, "Delay", 0.0, 100.0, 200.0, 500.0, 1.0)
    private val openDelay = ValueNumber(this, "Open delay", 0.0, 100.0, 500.0, 1.0)
    private val closeDelay = ValueNumber(this, "Close delay", 0.0, 100.0, 500.0, 1.0)
    private val randomize = ValueNumber(this, "Randomize", 0.0, 0.0, 30.0, 1.0) // used to be called parkinson...
    private val checkTitle = ValueBoolean(this, "Check title", false)
    private val titleSubstring = ValueText(this, "Title substring", "Chest", isEnabled = { checkTitle.value })
    private val checkType = ValueMode(this, "Check type", false, "Contains", "Equals", isEnabled = { checkTitle.value })
    private val ignoreCase = ValueBoolean(this, "Ignore case", false, isEnabled = { checkTitle.value })
    private val failChance = ValueNumber(this, "Fail chance", 0.0, 0.0, 100.0, 1.0)

    private val intelligent = ValueBoolean(this, "Intelligent", true)
    private val cleaner = Cleaner(this, isEnabled = { intelligent.value})

    private val timeUtil = TimeUtil()

    private var wasClosed = true
    private var mousePos: Vec2f? = null
    private var nextDelay: Long = 0

    private fun hasBetterEquivalent(slot: Slot, list: List<Slot>): Boolean {
        if (!intelligent.value)
            return false

        @Suppress("NAME_SHADOWING")
        val list = ArrayList(list)

        list.remove(slot)
        list.removeIf { ItemStack.canCombine(slot.stack, it.stack) }

        return cleaner.hasBetterEquivalent(slot.stack, ArrayUtils.addAll(list.distinctBy { it.stack.item }.filter { it != slot }.toTypedArray(), *ContainerUtil.getValidSlots(mc.player?.playerScreenHandler!!).filter { it.hasStack() }.toTypedArray()).map { it.stack })
    }

    /**
     * Checks if the player inventory contains a slot, which is capable of picking up the new stack
     */
    private fun canTransfer(slot: Slot): Boolean {
        val playerInventory = mc.player?.playerScreenHandler!!
        return playerInventory.slots
            .filter { it.isEnabled }
            .any { !it.hasStack() || (ItemStack.canCombine(it.stack, slot.stack) && it.stack.safeCount() < it.stack.maxCount) }
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

            val screen = mc.currentScreen as GenericContainerScreen

            if (checkTitle.value) {
                val string = screen.title.extractContent()
                when {
                    checkType.isSelected(0) -> {
                        if (!string.contains(titleSubstring.value, ignoreCase.value))
                            return@registerEvent
                    }

                    checkType.isSelected(1) -> {
                        if (!string.equals(titleSubstring.value, ignoreCase.value))
                            return@registerEvent
                    }
                }
            }

            val screenHandler = screen.screenHandler

            if (!screenHandler.cursorStack.nullOr { it.isEmpty })
                return@registerEvent

            if (mousePos == null) {
                mousePos = Vec2f(screen.backgroundWidth / 2F, screen.backgroundHeight / 2F)
            }

            var nextSlot = ContainerUtil.getClosestSlot(screenHandler, mousePos!!) { slot, list ->
                slot.hasStack() &&
                        slot.id < screenHandler.inventory.size() &&
                        !hasBetterEquivalent(slot, list) &&
                        canTransfer(slot)
            }

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
                val displayPos = ContainerUtil.getDisplayPosition(nextSlot).add(Vec2f(
                    if (randomize.value == 0.0) 0F else ThreadLocalRandom.current().nextDouble(-randomize.value, randomize.value).toFloat(),
                    if (randomize.value == 0.0) 0F else ThreadLocalRandom.current().nextDouble(-randomize.value, randomize.value).toFloat()
                ))
                if (ThreadLocalRandom.current().nextInt(100) < failChance.value) {
                    val interpolated = mousePos?.add(displayPos?.add(mousePos?.negate())?.multiply(ThreadLocalRandom.current().nextDouble(0.0, 1.0).toFloat()))!!
                    ContainerUtil.getClosestSlot(screenHandler, interpolated) { slot, _ -> !slot.hasStack() }?.also {
                        nextSlot = it
                    }
                }
                val distance = mousePos?.distanceSquared(displayPos)!!
                mousePos = displayPos
                val mapped = sqrt(distance).div(Vec2f(screen.backgroundWidth.toFloat(), screen.backgroundHeight.toFloat()).length())
                nextDelay = delay.interpolate(mapped.toDouble()).toLong()
                mc.interactionManager?.clickSlot(screenHandler.syncId, nextSlot?.id!!, GLFW.GLFW_MOUSE_BUTTON_LEFT, SlotActionType.QUICK_MOVE, mc.player)
                event.doneInput = true
            }
        }
    }
}
