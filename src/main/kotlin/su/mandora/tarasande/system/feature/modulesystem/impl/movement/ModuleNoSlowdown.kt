package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
import net.minecraft.util.UseAction
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import su.mandora.tarasande.event.impl.EventItemCooldown
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.string.StringUtil
import java.util.concurrent.ThreadLocalRandom

class ModuleNoSlowdown : Module("No slowdown", "Removes slowdowns when using items", ModuleCategory.MOVEMENT) {

    private val useActions = HashMap<UseAction, String>()

    init {
        for (useAction in UseAction.entries)
            useActions[useAction] = StringUtil.formatEnumTypes(useAction.name)
    }

    val slowdown = ValueNumber(this, "Slowdown", 0.0, 1.0, 1.0, 0.01)
    val actions = ValueMode(this, "Actions", true, *useActions.map { it.value }.toTypedArray())
    private val bypass = ValueMode(this, "Bypass", true, "Reuse", "Rehold", "Sneaking")
    private val reuseMode = ValueMode(this, "Reuse mode", false, "Same slot", "Different slot", isEnabled = { bypass.isSelected(1) })
    private val bypassedActions = ValueMode(this, "Bypassed actions", true, *useActions.map { it.value }.toTypedArray(), isEnabled = { bypass.anySelected() })

    fun isActionEnabled(setting: ValueMode): Boolean {
        val activeHand = PlayerUtil.getUsedHand()
        val usedStack = if (activeHand != null) mc.player?.getStackInHand(activeHand) else null
        @Suppress("FoldInitializerAndIfToElvis", "RedundantSuppression") // That looks gross & idea moment
        if (usedStack == null)
            return mc.player?.isUsingItem == true && setting.isSelected(useActions[UseAction.NONE]!!)
        return setting.isSelected(useActions[usedStack.useAction!!]!!)
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
                            if (reuseMode.isSelected(0)) {
                                mc.networkHandler?.sendPacket(UpdateSelectedSlotC2SPacket(mc.player?.inventory?.selectedSlot!!))
                            }
                            if (reuseMode.isSelected(1)) {
                                var slot = ThreadLocalRandom.current().nextInt(0, PlayerInventory.getHotbarSize() - 2 /* 1 for array access and 1 for the current slot*/)
                                if (slot >= mc.player?.inventory?.selectedSlot!!) slot++
                                mc.networkHandler?.sendPacket(UpdateSelectedSlotC2SPacket(slot))
                                mc.networkHandler?.sendPacket(UpdateSelectedSlotC2SPacket(mc.player?.inventory?.selectedSlot!!))
                            }
                        }
                    }
                    if (bypass.isSelected(2) && mc.player?.isSneaking == false) {
                        when (event.state) {
                            EventUpdate.State.PRE_PACKET -> {
                                mc.networkHandler?.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY))
                            }

                            EventUpdate.State.POST -> {
                                mc.networkHandler?.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY))
                            }

                            else -> {}
                        }
                    }
                }
            }
        }

        registerEvent(EventItemCooldown::class.java) { event ->
            if (interacting)
                event.cooldown = 1F
        }
    }
}
