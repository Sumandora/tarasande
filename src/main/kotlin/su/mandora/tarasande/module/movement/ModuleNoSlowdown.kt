package su.mandora.tarasande.module.movement

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventSlowdown
import su.mandora.tarasande.event.EventSlowdownAmount
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.mixin.accessor.IClientPlayerInteractionManager
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer

class ModuleNoSlowdown : Module("No Slowdown", "Removes blocking/eating/drinking etc... slowdowns", ModuleCategory.MOVEMENT) {

	private val slowdown = ValueNumber(this, "Slowdown", 0.0, 1.0, 1.0, 0.1)
	private val bypass = ValueMode(this, "Bypass", true, "Reuse", "Rehold")
	private val reuseMode = object : ValueMode(this, "Reuse mode", false, "Same slot", "Different slot") {
		override fun isVisible() = bypass.isSelected(1)
	}

	val eventConsumer = Consumer<Event> { event ->
		when (event) {
			is EventSlowdownAmount -> event.slowdownAmount = slowdown.value.toFloat()
			is EventSlowdown -> event.usingItem = false
			is EventUpdate -> {
				if (mc.player?.isUsingItem!!)
					when {
						bypass.isSelected(0) -> {
							when (event.state) {
								EventUpdate.State.PRE_PACKET -> {
									mc.networkHandler?.sendPacket(PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN))
								}
								EventUpdate.State.POST -> {
									for(hand in Hand.values()) {
										val itemStack = mc.player?.getStackInHand(hand)
										if(itemStack?.isEmpty!!) {
											(mc.interactionManager as IClientPlayerInteractionManager).setOnlyPackets(true)
											mc.interactionManager?.interactItem(mc.player, mc.world, hand)
											(mc.interactionManager as IClientPlayerInteractionManager).setOnlyPackets(false)
											break
										}
									}
								}
								else -> {}
							}
						}
						bypass.isSelected(1) -> {
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