package su.mandora.tarasande.module.movement

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
import net.minecraft.util.UseAction
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventSlowdown
import su.mandora.tarasande.event.EventSlowdownAmount
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.mixin.accessor.IClientPlayerInteractionManager
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer

class ModuleNoSlowdown : Module("No slowdown", "Removes blocking/eating/drinking etc... slowdowns", ModuleCategory.MOVEMENT) {

    private val useActions = arrayListOf(*UseAction.values())

    init {
        useActions.remove(UseAction.NONE)
    }

    private fun formatEnumTypes(useAction: UseAction) = useAction.name.substring(0, 1) + useAction.name.substring(1).lowercase()

    private val slowdown = ValueNumber(this, "Slowdown", 0.0, 1.0, 1.0, 0.1)
    private val actions = ValueMode(this, "Actions", true, *useActions.map { formatEnumTypes(it) }.toTypedArray())
    private val bypass = ValueMode(this, "Bypass", true, "Reuse", "Rehold")
    private val reuseMode = object : ValueMode(this, "Reuse mode", false, "Same slot", "Different slot") {
        override fun isEnabled() = bypass.isSelected(1)
    }
    private val bypassedActions = object : ValueMode(this, "Bypassed actions", true, *useActions.map { formatEnumTypes(it) }.toTypedArray()) {
        override fun isEnabled() = bypass.selected.isNotEmpty()
    }

    private fun isActionEnabled(setting: ValueMode): Boolean {
        val usedStack = mc.player?.getStackInHand(PlayerUtil.getUsedHand() ?: return false)
        return usedStack != null && setting.selected.contains(formatEnumTypes(usedStack.useAction!!))
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventSlowdownAmount -> if (isActionEnabled(actions)) event.slowdownAmount = slowdown.value.toFloat()
            is EventSlowdown -> if (isActionEnabled(actions)) event.usingItem = false
            is EventUpdate -> {
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
                                        (mc.interactionManager as IClientPlayerInteractionManager).setOnlyPackets(true)
                                        mc.interactionManager?.interactItem(mc.player, mc.world, hand)
                                        (mc.interactionManager as IClientPlayerInteractionManager).setOnlyPackets(false)
                                    }
                                }
                                else -> {}
                            }
                        }
                        if (bypass.isSelected(1)) {
                            if (event.state == EventUpdate.State.PRE) {
                                if (reuseMode.isSelected(1)) {
                                    var slot = mc.player?.inventory?.selectedSlot!!
                                    while (slot == mc.player?.inventory?.selectedSlot!!)
                                        slot = ThreadLocalRandom.current().nextInt(0, 8)
                                    mc.networkHandler?.sendPacket(UpdateSelectedSlotC2SPacket(slot))
                                }
                                mc.networkHandler?.sendPacket(UpdateSelectedSlotC2SPacket(mc.player?.inventory?.selectedSlot!!))
                            }
                        }
                    }
                }
            }
        }
    }
}