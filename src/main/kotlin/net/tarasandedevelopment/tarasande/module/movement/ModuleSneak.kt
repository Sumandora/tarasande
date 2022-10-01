package net.tarasandedevelopment.tarasande.module.movement

import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventInput
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import java.util.function.Consumer

class ModuleSneak : Module("Sneak", "Automatically sneaks", ModuleCategory.MOVEMENT) {

    private val standStill = ValueBoolean(this, "Stand still", false)
    private val dontSlowdown = ValueBoolean(this, "Don't slowdown", false)

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventKeyBindingIsPressed ->
                if (event.keyBinding == mc.options.sneakKey)
                    event.pressed = event.pressed || !standStill.value || !PlayerUtil.isPlayerMoving()

            is EventInput ->
                if (dontSlowdown.value)
                    event.slowDown = false
        }
    }

}