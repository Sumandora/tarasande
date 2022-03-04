package su.mandora.tarasande.base.module

import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.Manager
import su.mandora.tarasande.event.EventKey
import su.mandora.tarasande.event.EventTick
import su.mandora.tarasande.module.combat.ModuleAntiBot
import su.mandora.tarasande.module.combat.ModuleKillAura
import su.mandora.tarasande.module.combat.ModuleTeams
import su.mandora.tarasande.module.combat.ModuleWTap
import su.mandora.tarasande.module.misc.*
import su.mandora.tarasande.module.movement.*
import su.mandora.tarasande.module.player.ModuleScaffoldWalk
import su.mandora.tarasande.module.player.ModuleTimer
import su.mandora.tarasande.module.render.*
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueKeyBind

class ManagerModule : Manager<Module>() {

    private val toggledModules = ArrayList<Module>()

    init {
        add(
            ModuleSprint(),
            ModuleESP(),
            ModuleKillAura(),
            ModuleVelocity(),
            ModuleTimer(),
            ModuleScaffoldWalk(),
            ModuleSafeWalk(),
            ModuleFullBright(),
            ModuleSpammer(),
            ModuleDeadByDaylightEscape(),
            ModuleSpeed(),
            ModuleVehicleFlight(),
            ModuleNoSlowdown(),
            ModuleTeams(),
            ModuleInventoryMove(),
            ModuleBlink(),
            ModuleFlight(),
            ModuleTickBaseManipulation(),
            ModuleNoCooldown(),
            ModuleWTap(),
            ModuleTrajectories(),
            ModuleAntiBot(),
            ModuleNoFov(),
            ModuleLatency(),
            ModuleKeepSprint(),
            ModuleMurderMystery(),
            ModuleNoSwing(),
            ModuleColorCorrection(),
            ModuleWorldTime()
        )
        TarasandeMain.get().managerEvent?.add { event ->
            if (event is EventKey)
                for (module in list)
                    if (module.keyBind.keyBind == event.key)
                        toggledModules.add(module)
            if (event is EventTick)
                if (event.state == EventTick.State.POST) {
                    toggledModules.forEach(Module::switchState)
                    toggledModules.clear()
                }
        }
    }

}

open class Module(val name: String, val description: String, val category: ModuleCategory) {
    val visible = ValueBoolean(this, "Visible in ArrayList", true)
    var enabled = false
        set(value) {
            if (field != value)
                if (value) {
                    onEnable()
                    TarasandeMain.get().managerEvent?.addObject(this)
                } else {
                    TarasandeMain.get().managerEvent?.remObject(this)
                    onDisable()
                }

            field = value
        }
    val keyBind = ValueKeyBind(this, "Key Bind", GLFW.GLFW_KEY_UNKNOWN)

    val mc: MinecraftClient = MinecraftClient.getInstance()

    fun switchState() {
        enabled = !enabled
    }

    open fun onEnable() {}
    open fun onDisable() {}

    override fun toString(): String {
        return name
    }
}

enum class ModuleCategory {
    COMBAT, MOVEMENT, PLAYER, RENDER, MISC, GHOST
}