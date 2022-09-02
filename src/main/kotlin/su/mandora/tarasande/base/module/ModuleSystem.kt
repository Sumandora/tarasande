package su.mandora.tarasande.base.module

import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.Manager
import su.mandora.tarasande.event.EventTick
import su.mandora.tarasande.module.combat.*
import su.mandora.tarasande.module.misc.*
import su.mandora.tarasande.module.movement.*
import su.mandora.tarasande.module.player.*
import su.mandora.tarasande.module.render.*
import su.mandora.tarasande.value.ValueBind
import su.mandora.tarasande.value.ValueBoolean

class ManagerModule : Manager<Module>() {

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
            ModuleWorldTime(),
            ModuleNuker(),
            ModuleMovementRecorder(),
            ModuleBedESP(),
            ModuleFog(),
            ModuleNoSign(),
            ModuleProjectileAimBot(),
            ModuleNoHunger(),
            ModuleNoRotate(),
            ModuleQuakeAura(),
            ModuleBlockBot(),
            ModuleChestStealer(),
            ModuleAutoTool(),
            ModuleFreeCam(),
            ModuleRain(),
            ModuleNoFall()
        )
        TarasandeMain.get().managerEvent?.add { event ->
            if (event is EventTick) if (event.state == EventTick.State.POST) {
                for (module in list) for (i in 0 until module.bind.wasPressed()) module.switchState()
            }
        }
    }

}

open class Module(val name: String, val description: String, val category: ModuleCategory) {
    val visible = ValueBoolean(this, "Visible in ArrayList", true)
    var enabled = false
        set(value) {
            if (field != value) if (value) {
                onEnable()
                TarasandeMain.get().managerEvent?.addObject(this)
            } else {
                TarasandeMain.get().managerEvent?.remObject(this)
                onDisable()
            }

            field = value
        }
    val bind = ValueBind(this, "Bind", ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN)

    val mc: MinecraftClient = MinecraftClient.getInstance()

    fun switchState() {
        enabled = !enabled
    }

    open fun onEnable() {}
    open fun onDisable() {}

}

enum class ModuleCategory {
    COMBAT, MOVEMENT, PLAYER, RENDER, MISC, GHOST
}