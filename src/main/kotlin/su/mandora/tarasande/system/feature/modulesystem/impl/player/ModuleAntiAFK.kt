package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.client.gui.screen.ingame.HandledScreen
import su.mandora.tarasande.event.impl.EventInput
import su.mandora.tarasande.event.impl.EventMouseDelta
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.system.screen.informationsystem.Information
import su.mandora.tarasande.system.screen.informationsystem.ManagerInformation
import su.mandora.tarasande.util.math.time.TimeUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.string.StringUtil

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
                    StringUtil.round(timer.getTimeLeft(delay.value.toLong()) / 1000.0, 1)
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

        registerEvent(EventInput::class.java) { event ->
            if(event.input == mc.player?.input)
                if (timer.hasReached(delay.value.toLong())) {
                    event.input.jumping = true
                    timer.reset()
                } else if (event.input.movementInput.lengthSquared() > 0.0)
                    timer.reset()
        }
    }

}