package net.tarasandedevelopment.tarasande.module.player

import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.event.EventMouseDelta
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.ValueNumber

class ModuleAntiAFK : Module("Anti AFK", "Prevents AFK kicks", ModuleCategory.PLAYER) {

    val delay = ValueNumber(this, "Delay", 0.0, 60.0, 180.0, 60.0)

    val timer = TimeUtil()
    private val movementKeys = ArrayList(PlayerUtil.movementKeys)

    init {
        movementKeys.add(mc.options.jumpKey)
        movementKeys.add(mc.options.sneakKey)

        movementKeys.add(mc.options.attackKey)
        movementKeys.add(mc.options.useKey)
    }

    override fun onEnable() {
        timer.reset()
    }

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE)
                if (mc.currentScreen is HandledScreen<*> || mc.player == null)
                    timer.reset()
        }

        registerEvent(EventMouseDelta::class.java) { event ->
            if (event.deltaX == 0.0 && event.deltaY == 0.0)
                timer.reset()
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (timer.hasReached((delay.value * 1000).toLong())) {
                if (event.keyBinding == mc.options.jumpKey) {
                    timer.reset()
                    event.pressed = true
                }
            } else if (movementKeys.contains(event.keyBinding))
                if (event.pressed)
                    timer.reset()
        }
    }

}