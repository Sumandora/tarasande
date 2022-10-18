package net.tarasandedevelopment.tarasande.util.player.container

import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.Vec2f
import net.tarasandedevelopment.tarasande.mixin.accessor.IHandledScreen

object ContainerUtil {

    fun getDisplayPosition(accessor: IHandledScreen, slot: Slot): Vec2f {
        return Vec2f(accessor.tarasande_getX() + slot.x.toFloat() + 8, accessor.tarasande_getY() + slot.y.toFloat() + 8)
    }

    fun getValidSlots(screenHandler: ScreenHandler): List<Slot> {
        return screenHandler.slots
            .filter { it != null && it.isEnabled && it.hasStack() }
    }

    fun getClosestSlot(screenHandler: ScreenHandler, accessor: IHandledScreen, lastMouseClick: Vec2f, block: (Slot) -> Boolean): Slot? {
        return screenHandler.slots
            .filter { it != null && it.isEnabled && it.hasStack() && block.invoke(it) }
            .minByOrNull { lastMouseClick.distanceSquared(getDisplayPosition(accessor, it)) }
    }
}