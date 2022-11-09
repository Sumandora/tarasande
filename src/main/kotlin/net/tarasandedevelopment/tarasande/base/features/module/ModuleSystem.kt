package net.tarasandedevelopment.tarasande.base.features.module

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventRespawn
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.features.module.chat.ModuleAllowAllCharacters
import net.tarasandedevelopment.tarasande.features.module.chat.ModuleNoChatContext
import net.tarasandedevelopment.tarasande.features.module.chat.ModulePrivateMsgDetector
import net.tarasandedevelopment.tarasande.features.module.chat.ModuleSpammer
import net.tarasandedevelopment.tarasande.features.module.combat.*
import net.tarasandedevelopment.tarasande.features.module.exploit.*
import net.tarasandedevelopment.tarasande.features.module.ghost.*
import net.tarasandedevelopment.tarasande.features.module.misc.*
import net.tarasandedevelopment.tarasande.features.module.movement.*
import net.tarasandedevelopment.tarasande.features.module.player.*
import net.tarasandedevelopment.tarasande.features.module.render.*
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.fixed.PanelNotifications
import net.tarasandedevelopment.tarasande.value.ValueBind
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueMode
import org.lwjgl.glfw.GLFW
import java.util.function.Consumer

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
            ModuleSpeed(),
            ModuleVehicleFlight(),
            ModuleNoSlowdown(),
            ModuleTeams(),
            ModuleInventoryMove(),
            ModuleBlink(),
            ModuleFlight(),
            ModuleTickBaseManipulation(),
            ModuleWTap(),
            ModuleTrajectories(),
            ModuleAntiBot(),
            ModuleNoFOV(),
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
            ModuleAntiBindingCurse(),
            ModuleCommandBlockBypass(),
            ModuleAntiParticleHide(),
            ModulePrivateMsgDetector(),
            ModuleNoChatContext(),
            ModuleNoMiningTrace(),
            ModuleDisableTelemetry(),
            ModuleBlockChangeTracker(),
            ModuleTrueSight(),
            ModulePhase(),
            ModuleInventoryCleaner(),
            ModuleLecternCrash(),
            ModuleCraftingDupe(),
            ModulePrediction(),
            ModulePerfectHorseJump(),
            ModuleTridentBoost(),
            ModuleAutoRespawn(),
            ModulePortalScreen(),
            ModuleAutoFish(),
            ModuleJesus(),
            ModuleFastClimb(),
            ModuleAntiAFK(),
            ModuleEntityControl(),
            ModuleFastBreak(),
            ModuleCivBreak(),
            ModuleHealingBot(),
            ModuleVehicleSpeed(),
            ModuleIgnoreResourcePackHash(),
            ModuleNoJumpCooldown(),
            ModuleEveryItemOnArmor(),
            ModuleNoRender(),
            ModuleNoStatusEffect(),
            ModuleAllowAllCharacters()
        )
        TarasandeMain.get().managerEvent.also {
            it.add(EventTick::class.java) {
                if (it.state == EventTick.State.POST) {
                    for (module in list)
                        for (i in 0 until module.bind.wasPressed())
                            module.switchState()
                }
            }
            it.add(EventRespawn::class.java) {
                for (module in list)
                    if (module.disableWhen.isSelected(0) && module.enabled)
                        module.enabled = false
            }
            it.add(EventDisconnect::class.java) {
                for (module in list)
                    if (module.disableWhen.isSelected(1) && module.enabled)
                        module.enabled = false
            }
        }
    }
}

open class Module(val name: String, val description: String, val category: String) {
    private val eventListeners = HashSet<Triple<Class<Event>, Int, Consumer<Event>>>()

    var enabled = false
        set(value) {
            if (field != value) if (value) {
                onEnable()
                eventListeners.forEach { TarasandeMain.get().managerEvent.add(it.first, it.second, it.third) }
                PanelNotifications.notify("$name is now enabled")
            } else {
                eventListeners.forEach { TarasandeMain.get().managerEvent.rem(it.first, it.third) }
                onDisable()
                PanelNotifications.notify("$name is now disabled")
            }

            field = value
        }

    val visible = ValueBoolean(this, "Visible in ArrayList", true)
    val disableWhen = ValueMode(this, "Disable when", true, "Death", "Disconnect")
    val bind = ValueBind(this, "Bind", ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN)

    val mc: MinecraftClient = MinecraftClient.getInstance()

    fun switchState() {
        enabled = !enabled
    }

    open fun onEnable() {}
    open fun onDisable() {}

    fun <T : Event> registerEvent(clazz: Class<T>, priority: Int = 1000, c: Consumer<T>) {
        @Suppress("UNCHECKED_CAST") // BYPASS GENERICS ONCE AGAIN $$$$$$$
        eventListeners.add(Triple(clazz as Class<Event>, priority, c as Consumer<Event>))
    }
}

object ModuleCategory {
    const val COMBAT = "Combat"
    const val MOVEMENT = "Movement"
    const val PLAYER = "Player"
    const val RENDER = "Render"
    const val MISC = "Misc"
    const val GHOST = "Ghost"
    const val EXPLOIT = "Exploit"
    const val CHAT = "Chat"
}