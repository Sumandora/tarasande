package net.tarasandedevelopment.tarasande.module.player

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.LiteralTextContent
import net.minecraft.util.math.Vec2f
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventPollEvents
import net.tarasandedevelopment.tarasande.mixin.accessor.IHandledScreen
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueNumber
import net.tarasandedevelopment.tarasande.value.ValueNumberRange
import net.tarasandedevelopment.tarasande.value.ValueText
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

    private val timeUtil = TimeUtil()

    private var wasClosed = true
    private var mousePos: Vec2f? = null
    private var nextDelay: Long = 0

    private fun getDisplayPosition(accessor: IHandledScreen, slot: Slot): Vec2f {
        return Vec2f(accessor.tarasande_getX() + slot.x.toFloat() + 8, accessor.tarasande_getY() + slot.y.toFloat() + 8)
    }

    init {
        registerEvent(EventPollEvents::class.java) { event ->
            /*
             * Side story: <=1.12.2 does this in the tick method
             * We do it in the frame
             * Means we can steal faster and on top, we can steal with a low timer, without any issues
             * Maybe 1.8 isn't so good after all for cheating?
             */

            if (event.fake)
                return@registerEvent
            if (mc.currentScreen !is GenericContainerScreen) {
                timeUtil.reset()
                wasClosed = true
                mousePos = null
                return@registerEvent
            }

            val accessor = mc.currentScreen as IHandledScreen

            if (checkTitle.value) {
                val title = accessor.tarasande_getTitle()
                val content = title.content
                if (content is LiteralTextContent)
                    if (content.string?.contains(titleSubstring.value) == false)
                        return@registerEvent
            }

            val screenHandler = (mc.currentScreen as GenericContainerScreen).screenHandler

            if (mousePos == null) {
                mousePos = Vec2f(mc.window.scaledWidth / 2f, mc.window.scaledHeight / 2f)
            }

            val nextSlot = screenHandler.slots
                .filter { it != null && it.isEnabled && it.hasStack() && it.id < screenHandler.inventory.size() }
                .minByOrNull { mousePos?.distanceSquared(getDisplayPosition(accessor, it))!! }

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
                val displayPos = getDisplayPosition(accessor, nextSlot).add(Vec2f(
                    if (randomize.value == 0.0) 0.0f else ThreadLocalRandom.current().nextDouble(-randomize.value, randomize.value).toFloat(),
                    if (randomize.value == 0.0) 0.0f else ThreadLocalRandom.current().nextDouble(-randomize.value, randomize.value).toFloat()
                ))
                val distance = mousePos?.distanceSquared(displayPos)!!
                mousePos = displayPos
                val mapped = sqrt(distance).div(Vec2f(accessor.tarasande_getBackgroundWidth().toFloat(), accessor.tarasande_getBackgroundHeight().toFloat()).length())
                nextDelay = (delay.minValue + (delay.maxValue - delay.minValue) * mapped).toLong()
                mc.interactionManager?.clickSlot(screenHandler.syncId, nextSlot.id, GLFW.GLFW_MOUSE_BUTTON_1, SlotActionType.QUICK_MOVE, mc.player)
            }
        }
    }
}