package net.tarasandedevelopment.tarasande.systems.feature.modulesystem

import com.google.gson.JsonObject
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBind
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.events.Event
import net.tarasandedevelopment.events.impl.EventDisconnect
import net.tarasandedevelopment.events.impl.EventPacket
import net.tarasandedevelopment.events.impl.EventTick
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.chat.*
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.combat.*
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.exploit.*
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.ghost.*
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.misc.*
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.movement.*
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.player.*
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render.*
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.panel.elements.PanelElementsCategory
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.panel.fixed.PanelArrayList
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.impl.fixed.PanelNotifications
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
            ModuleNoChatContext(),
            ModuleNoMiningTrace(),
            ModuleBlockChangeTracker(),
            ModuleTrueSight(),
            ModulePhase(),
            ModuleInventoryCleaner(),
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
        TarasandeMain.get().eventSystem.also {
            it.add(EventTick::class.java) {
                if (it.state == EventTick.State.POST) {
                    for (module in list)
                        for (i in 0 until module.bind.wasPressed())
                            module.switchState()
                }
            }
            it.add(EventPacket::class.java) {
                if(it.type == EventPacket.Type.RECEIVE && it.packet is PlayerRespawnS2CPacket)
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

        TarasandeMain.get().panelSystem.add(PanelArrayList(this))

        this.list.distinctBy { it.category }.forEach {
            TarasandeMain.get().panelSystem.add(PanelElementsCategory(this, it.category))
        }

    }

}

open class Module(val name: String, val description: String, val category: String) {
    private val eventListeners = HashSet<Triple<Class<Event>, Int, Consumer<Event>>>()

    var enabled = false
        set(value) {
            if (field != value) if (value) {
                onEnable()
                eventListeners.forEach { TarasandeMain.get().eventSystem.add(it.first, it.second, it.third) }
                PanelNotifications.notify("$name is now enabled")
            } else {
                eventListeners.forEach { TarasandeMain.get().eventSystem.rem(it.first, it.third) }
                onDisable()
                PanelNotifications.notify("$name is now disabled")
            }

            field = value
        }

    val visible = ValueBoolean(this, "Visible in ArrayList", true)
    val disableWhen = ValueMode(this, "Auto disable", true, "Death", "Disconnect")
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