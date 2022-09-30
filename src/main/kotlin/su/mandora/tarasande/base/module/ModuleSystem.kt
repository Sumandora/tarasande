package su.mandora.tarasande.base.module

import de.florianmichael.tarasande.module.exploit.*
import de.florianmichael.tarasande.module.misc.ModuleFurnaceProgress
import de.florianmichael.tarasande.module.player.ModuleNoMiningTrace
import de.florianmichael.tarasande.module.qol.ModuleInstantWorld
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.Manager
import su.mandora.tarasande.event.EventTick
import su.mandora.tarasande.module.combat.*
import su.mandora.tarasande.module.exploit.ModuleDeadByDaylightEscape
import su.mandora.tarasande.module.exploit.ModuleResourcePackSpoofer
import su.mandora.tarasande.module.exploit.ModuleTickBaseManipulation
import su.mandora.tarasande.module.ghost.*
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
            ModuleKeepSprint(),
            ModuleMurderMystery(),
            ModuleNoSwing(),
            ModuleColorCorrection(),
            ModuleWorldTime(),
            ModuleNuker(),
            ModuleMovementRecorder(),
            ModuleBedESP(),
            ModuleFog(),
            ModuleProjectileAimBot(),
            ModuleNoHunger(),
            ModuleNoRotate(),
            ModuleQuakeAura(),
            ModuleBlockBot(),
            ModuleChestStealer(),
            ModuleAutoTool(),
            ModuleFreeCam(),
            ModuleRain(),
            ModuleNoFall(),
            ModuleCriticals(),
            ModuleReach(),
            ModuleAutoClicker(),
            ModuleAimAssist(),
            ModuleNoFriends(),
            ModuleBlockESP(),
            ModuleParkour(),
            ModuleNameProtect(),
            ModuleNoCramming(),
            ModuleStep(),
            ModuleResourcePackSpoofer(),
            ModuleMidClick(),
            ModuleTargetStrafe(),
            ModuleSneak(),
            ModuleAirStuck(),
            ModuleTNTBlock(),
            ModuleNoWeb(),
            ModuleClickTP(),
            ModuleBacktrace(),
            ModuleAntiFall(),
            ModuleHitBox(),
            ModuleFastPlace(),
            ModuleFastUse(),
            ModuleDisableSequencePackets(),
            ModuleAntiBindingCurse(),
            ModuleBungeeHack(),
            ModuleCommandBlockBypass(),
            ModuleAntiParticleHide(),
            ModulePrivateMsgDetector(),
            ModuleNoChatContext(),
            ModuleInstantWorld(),
            ModuleFurnaceProgress(),
            ModuleNoMiningTrace(),
            ModuleDisableTelemetry(),
            ModuleBlockChangeTracker()
        )
        TarasandeMain.get().managerEvent.add { event ->
            if (event is EventTick)
                if (event.state == EventTick.State.POST) {
                    for (module in list)
                        for (i in 0 until module.bind.wasPressed())
                            module.switchState()
                }
        }
    }

}

open class Module(val name: String, val description: String, val category: ModuleCategory) {
    var visibleInMenu = true
    val visible = ValueBoolean(this, "Visible in ArrayList", true)

    @Suppress("PropertyName")
    var _enabled = false
        private set
    var enabled: Boolean
        set(value) {
            if (_enabled != value) if (value) {
                onEnable()
                TarasandeMain.get().managerEvent.addObject(this)
            } else {
                TarasandeMain.get().managerEvent.remObject(this)
                onDisable()
            }

            _enabled = value
        }
        get() = _enabled && isEnabled()

    val bind = ValueBind(this, "Bind", ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN)

    val mc: MinecraftClient = MinecraftClient.getInstance()

    fun switchState() {
        enabled = !enabled
    }

    open fun onEnable() {}
    open fun onDisable() {}

    open fun isEnabled() = true
}

enum class ModuleCategory {
    COMBAT, MOVEMENT, PLAYER, RENDER, MISC, GHOST, EXPLOIT, QUALITY_OF_LIFE
}