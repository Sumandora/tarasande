package net.tarasandedevelopment.tarasande.module.movement

import de.florianmichael.viaprotocolhack.util.VersionList
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
import net.minecraft.util.UseAction
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventItemCooldown
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.util.concurrent.ThreadLocalRandom

class ModuleNoSlowdown : Module("No slowdown", "Removes blocking/eating/drinking etc... slowdowns", ModuleCategory.MOVEMENT) {

    private val useActions = HashMap<UseAction, String>()

    init {
        for (useAction in UseAction.values())
            useActions[useAction] = StringUtil.formatEnumTypes(useAction.name)
    }

    val slowdown = ValueNumber(this, "Slowdown", 0.0, 1.0, 1.0, 0.1)
    val actions = ValueMode(this, "Actions", true, *useActions.map { it.value }.toTypedArray())
    private val bypass = ValueMode(this, "Bypass", true, "Reuse", "Rehold")
    private val reuseMode = object : ValueMode(this, "Reuse mode", false, "Same slot", "Different slot") {
        override fun isEnabled() = bypass.isSelected(1)
    }
    private val bypassedActions = object : ValueMode(this, "Bypassed actions", true, *useActions.map { it.value }.toTypedArray()) {
        override fun isEnabled() = bypass.anySelected()
    }
    val preventUnsprint = object : ValueBoolean(this, "Prevent unsprint", false) {
        override fun isEnabled() = VersionList.isOlderOrEqualTo(VersionList.R1_8)
    }

    fun isActionEnabled(setting: ValueMode): Boolean {
        val usedStack = mc.player?.getStackInHand(PlayerUtil.getUsedHand() ?: return false)
        return usedStack != null && setting.selected.contains(useActions[usedStack.useAction!!])
    }

    private var interacting = false

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (mc.player?.isUsingItem!!) {
                if (isActionEnabled(bypassedActions)) {
                    if (bypass.isSelected(0)) {
                        when (event.state) {
                            EventUpdate.State.PRE_PACKET -> {
                                mc.networkHandler?.sendPacket(PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN))
                            }

                            EventUpdate.State.POST -> {
                                val hand = PlayerUtil.getUsedHand()
                                if (hand != null) {
                                    interacting = true
                                    mc.interactionManager?.interactItem(mc.player, hand)
                                    interacting = false
                                }
                            }

                            else -> {}
                        }
                    }
                    if (bypass.isSelected(1)) {
                        if (event.state == EventUpdate.State.PRE) {
                            if (reuseMode.isSelected(1)) {
                                var slot = mc.player?.inventory?.selectedSlot!!
                                while (slot == mc.player?.inventory?.selectedSlot!!) slot = ThreadLocalRandom.current().nextInt(0, 8)
                                mc.networkHandler?.sendPacket(UpdateSelectedSlotC2SPacket(slot))
                            }
                            mc.networkHandler?.sendPacket(UpdateSelectedSlotC2SPacket(mc.player?.inventory?.selectedSlot!!))
                        }
                    }
                }
            }
        }

        registerEvent(EventItemCooldown::class.java) { event ->
            if (interacting)
                event.cooldown = 1.0F
        }
    }
}
