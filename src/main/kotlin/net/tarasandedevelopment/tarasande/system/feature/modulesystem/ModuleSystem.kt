package net.tarasandedevelopment.tarasande.system.feature.modulesystem

import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.event.*
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBind
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.combat.*
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.exploit.*
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.ghost.*
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.misc.*
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.*
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player.*
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render.*
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.panel.element.PanelElementsCategory
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.panel.fixed.PanelArrayList
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.ManagerPanel
import org.lwjgl.glfw.GLFW
import su.mandora.event.Event
import su.mandora.event.EventDispatcher
import java.util.function.Consumer

object ManagerModule : Manager<Module>() {

    init {
        add(
            // Combat
            ModuleKillAura(),
            ModuleTeams(),
            ModuleWTap(),
            ModuleAntiBot(),
            ModuleProjectileAimBot(),
            ModuleQuakeAura(),
            ModuleCriticals(),
            ModuleTNTBlock(),
            ModuleHealingBot(),
            ModuleAntiFireball(),
            ModuleAutoLog(),

            // Movement
            ModuleSprint(),
            ModuleVelocity(),
            ModuleSafeWalk(),
            ModuleSpeed(),
            ModuleVehicleFlight(),
            ModuleNoSlowdown(),
            ModuleInventoryMove(),
            ModuleFlight(),
            ModuleKeepSprint(),
            ModuleNoRotate(),
            ModuleParkour(),
            ModuleNoCramming(),
            ModuleStep(),
            ModuleTargetStrafe(),
            ModuleSneak(),
            ModuleAirStuck(),
            ModuleNoWeb(),
            ModuleClickTP(),
            ModulePhase(),
            ModulePerfectHorseJump(),
            ModuleTridentBoost(),
            ModuleJesus(),
            ModuleFastClimb(),
            ModuleEntityControl(),
            ModuleVehicleSpeed(),
            ModuleNoJumpCooldown(),
            ModuleWaterSpeed(),
            ModuleTerrainSpeed(),

            // Player
            ModuleTimer(),
            ModuleScaffoldWalk(),
            ModuleNuker(),
            ModuleNoHunger(),
            ModuleChestStealer(),
            ModuleAutoTool(),
            ModuleNoFall(),
            ModuleAntiFall(),
            ModuleFastUse(),
            ModuleNoMiningTrace(),
            ModuleInventoryCleaner(),
            ModuleAutoFish(),
            ModuleAntiAFK(),
            ModuleFastBreak(),
            ModuleCivBreak(),
            ModuleNoStatusEffect(),
            ModuleAntiCactus(),
            ModuleAutoArmor(),
            ModuleXCarry(),
            ModuleClickPearl(),
            ModuleBlockAura(),

            // Render
            ModuleESP(),
            ModuleFullBright(),
            ModuleTrajectories(),
            ModuleNoFOV(),
            ModuleNoSwing(),
            ModuleColorCorrection(),
            ModuleWorldTime(),
            ModuleBedESP(),
            ModuleFog(),
            ModuleFreeCam(),
            ModuleRain(),
            ModuleBlockESP(),
            ModuleNameProtect(),
            ModuleTrueSight(),
            ModuleNoRender(),
            ModuleCameraNoClip(),
            ModulePreferredOffHandItem(),

            // Misc
            ModuleBlink(),
            ModuleMurderMystery(),
            ModuleBlockBot(),
            ModuleNoFriends(),
            ModuleMidClick(),
            ModuleAutoRespawn(),
            ModuleTroubleInTerroristTown(),
            ModuleSpammer(),

            // Ghost
            ModuleReach(),
            ModuleAutoClicker(),
            ModuleAimAssist(),
            ModuleBacktrace(),
            ModuleHitBox(),
            ModuleFastPlace(),
            ModuleZoot(),

            // Exploit
            ModuleTickBaseManipulation(),
            ModuleAntiBindingCurse(),
            ModulePortalScreen(),
            ModuleNoPitchLimit(),
            ModuleRegen()
        )

        ManagerPanel.add(PanelArrayList(this@ManagerModule))
        EventDispatcher.apply {
            add(EventTick::class.java) {
                if (it.state == EventTick.State.POST) {
                    for (module in list)
                        repeat(module.bind.wasPressed()) {
                            module.switchState()
                        }
                }
            }
            add(EventPacket::class.java) {
                if (it.type == EventPacket.Type.RECEIVE)
                    when (it.packet) {
                        is PlayerPositionLookS2CPacket -> {
                            for (module in list)
                                if (module.autoDisable.isSelected(0) && module.enabled.value)
                                    module.switchState()
                        }

                        is HealthUpdateS2CPacket -> {
                            if (it.packet.health <= 0)
                                for (module in list)
                                    if (module.autoDisable.isSelected(1) && module.enabled.value)
                                        module.switchState()
                        }
                    }
            }
            add(EventDisconnect::class.java) {
                if (it.connection == mc.networkHandler?.connection) {
                    for (module in list)
                        if (module.autoDisable.isSelected(2) && module.enabled.value)
                            module.switchState()
                }
            }
            add(EventSuccessfulLoad::class.java, 1001) {
                this@ManagerModule.list.distinctBy { it.category }.forEach {
                    ManagerPanel.add(PanelElementsCategory(this@ManagerModule, it.category))
                }
            }
        }
    }
}

open class Module(val name: String, val description: String, val category: String) {
    private val eventListeners = HashSet<Triple<Class<Event>, Int, Consumer<Event>>>()

    var enabled = object : ValueBoolean(ManagerModule, name, false) {
        override fun onChange(oldValue: Boolean?, newValue: Boolean) {
            if (oldValue == null)
                return

            if (oldValue != newValue) if (newValue) {
                onEnable()
                eventListeners.forEach { EventDispatcher.add(it.first, it.second, it.third) }
            } else {
                eventListeners.forEach { EventDispatcher.rem(it.first, it.third) }
                onDisable()
            }

            EventDispatcher.call(EventModuleStateSwitched(this@Module, oldValue, newValue))
        }
    }

    @Suppress("LeakingThis")
    val visible = ValueBoolean(this, "Visible in ArrayList", true)

    @Suppress("LeakingThis")
    val autoDisable = ValueMode(this, "Auto disable", true, "Serverside movement", "Death", "Disconnect")

    @Suppress("LeakingThis")
    val bind = ValueBind(this, "Bind", ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN)

    fun switchState() {
        enabled.value = !enabled.value
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
}
