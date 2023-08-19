package su.mandora.tarasande.system.feature.modulesystem.impl.misc

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModulePotionSaver : Module("Potion saver", "Sends no movement updates when standing still", ModuleCategory.MISC) {

    private val potionCheck = ValueMode(this, "Potion check", false, "Ignore", "Only positive", "Balance", "Only negative", "Any effect")
    private val balanceThreshold = ValueNumber(this, "Balance threshold", -2.0, 1.0, 2.0, 1.0)
    private val balanceComparison = ValueMode(this, "Balance comparison", false,  "Less than", "Less than or equal", "Equal", "Greater than or equal", "Greater than")
    init {
        balanceComparison.select(4)
    }
    val resyncDurations = ValueBoolean(this, "Resync durations", true)

    var halting = false

    init {
        registerEvent(EventPacket::class.java) { event ->
            if(event.type == EventPacket.Type.SEND && event.packet is PlayerMoveC2SPacket) {
                halting = false
                if(mc.player == null) return@registerEvent

                if(!potionCheck.isSelected(0)) {
                    if(mc.player!!.activeStatusEffects.isEmpty())
                        return@registerEvent
                    if(!potionCheck.isSelected(4)) {
                        val (goodEffects, badEffects) = mc.player!!.activeStatusEffects.toList().partition { it.first.isBeneficial }

                        when {
                            potionCheck.isSelected(1) && badEffects.isNotEmpty() -> return@registerEvent
                            potionCheck.isSelected(3) && goodEffects.isNotEmpty() -> return@registerEvent
                            potionCheck.isSelected(2) -> {
                                val balance = -badEffects.count() + goodEffects.count()

                                when { // Remember those have to be inverted, because the user wants that the flow to fallthrough if the condition is met
                                    balanceComparison.isSelected(0) && balance >= balanceThreshold.value -> return@registerEvent
                                    balanceComparison.isSelected(1) && balance > balanceThreshold.value -> return@registerEvent
                                    balanceComparison.isSelected(2) && balance.toDouble() != balanceThreshold.value -> return@registerEvent
                                    balanceComparison.isSelected(3) && balance < balanceThreshold.value -> return@registerEvent
                                    balanceComparison.isSelected(4) && balance >= balanceThreshold.value -> return@registerEvent
                                }
                            }
                        }
                    }
                }

                if(
                    mc.player!!.lastX == event.packet.getX(mc.player!!.lastX) &&
                    mc.player!!.lastBaseY == event.packet.getY(mc.player!!.lastBaseY) &&
                    mc.player!!.lastZ == event.packet.getZ(mc.player!!.lastZ) &&

                    mc.player!!.lastYaw == event.packet.getYaw(mc.player!!.lastYaw) &&
                    mc.player!!.lastPitch == event.packet.getPitch(mc.player!!.lastPitch)
                ) {
                    event.cancelled = true
                    halting = true
                }
            }
        }
    }
}