package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.event.EventMouseDelta
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.ManagerInformation
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import kotlin.math.roundToInt

class ModuleAntiAFK : Module("Anti AFK", "Prevents AFK kicks", ModuleCategory.PLAYER) {

    val delay = object : ValueNumber(this, "Delay", 0.0, 60000.0, 180000.0, 5000.0) {
        override fun onChange(oldValue: Double?, newValue: Double) {
            timer.reset()
        }
    }

    val timer = TimeUtil()
    private val movementKeys = ArrayList(PlayerUtil.movementKeys)

    init {
        movementKeys.add(mc.options.jumpKey)
        movementKeys.add(mc.options.sneakKey)

        movementKeys.add(mc.options.attackKey)
        movementKeys.add(mc.options.useKey)

        ManagerInformation.add(object : Information("Anti AFK", "Jump countdown") {
            override fun getMessage() =
                if (enabled.value)
                    (((delay.value - (System.currentTimeMillis() - timer.time)) / 100).roundToInt() / 10.0).toString()
                else
                    null
        })
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
            if (event.deltaX != 0.0 || event.deltaY != 0.0)
                timer.reset()
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (timer.hasReached(delay.value.toLong())) {
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