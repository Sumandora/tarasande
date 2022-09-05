package su.mandora.tarasande.module.player

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.math.Vec2f
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventPollEvents
import su.mandora.tarasande.mixin.accessor.IHandledScreen
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.value.ValueNumber
import su.mandora.tarasande.value.ValueNumberRange
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer
import kotlin.math.sqrt

class ModuleChestStealer : Module("Chest stealer", "Takes all items out of a chest", ModuleCategory.PLAYER) {

    private val delay = ValueNumberRange(this, "Delay", 0.0, 100.0, 200.0, 500.0, 1.0)
    private val openDelay = ValueNumber(this, "Open delay", 0.0, 100.0, 500.0, 1.0)
    private val closeDelay = ValueNumber(this, "Close delay", 0.0, 100.0, 500.0, 1.0)
    private val randomize = ValueNumber(this, "Randomize", 0.0, 0.0, 30.0, 1.0) // used to be called parkinson...

    private val timeUtil = TimeUtil()

    private var wasClosed = true
    private var mousePos: Vec2f? = null
    private var nextDelay: Long = 0

    private fun getDisplayPosition(accessor: IHandledScreen, slot: Slot): Vec2f {
        return Vec2f(accessor.tarasande_getX() + slot.x.toFloat() + 8, accessor.tarasande_getY() + slot.y.toFloat() + 8)
    }

    val eventConsumer = Consumer<Event> { event ->
        /*
         * Side story: <=1.12.2 does this in the tick method
         * We do it in the frame
         * Means we can steal faster and on top, we can steal with a low timer, without any issues
         * Maybe 1.8 isn't so good after all for cheating?
         */
        if (event is EventPollEvents) {
            if (event.fake)
                return@Consumer
            if (mc.currentScreen !is GenericContainerScreen) {
                timeUtil.reset()
                wasClosed = true
                mousePos = null
                return@Consumer
            }

            val accessor = mc.currentScreen as IHandledScreen
            val screenHandler = (mc.currentScreen as GenericContainerScreen).screenHandler

            if (mousePos == null) {
                mousePos = Vec2f(mc.window.scaledWidth / 2f, mc.window.scaledHeight / 2f)
            }

            val nextSlot = screenHandler.slots.filter { it != null && it.isEnabled && it.hasStack() && it.id < screenHandler.inventory.size() }.minByOrNull { mousePos?.distanceSquared(getDisplayPosition(accessor, it))!! }

            if (!timeUtil.hasReached(when {
                    wasClosed -> openDelay.value.toLong()
                    nextSlot == null -> closeDelay.value.toLong()
                    else -> nextDelay
                })) return@Consumer
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