package su.mandora.tarasande.module.player

import net.minecraft.item.BowItem
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly
import net.minecraft.util.UseAction
import net.minecraft.util.registry.Registry
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.string.StringUtil
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleFastUse : Module("Fast use", "Speeds up item usage (Protocol-dependant)", ModuleCategory.PLAYER) {

    private val useActions = arrayOf(UseAction.EAT, UseAction.DRINK, UseAction.BOW, UseAction.SPEAR, UseAction.CROSSBOW)
    private val actions: ValueMode
    private val settings = HashMap<UseAction, ValueNumber>()

    private val nameMap = HashMap<UseAction, String>()

    init {
        actions = ValueMode(this, "Actions", true, *useActions.map { StringUtil.formatEnumTypes(it.name).also { name -> nameMap[it] = name } }.toTypedArray())
        for (useAction in useActions) {
            // This when is expressing my hate towards mojang really nicely
            val longest = when (useAction) {
                UseAction.BOW -> {
                    var tick = 0
                    var lastProgress = 0.0f // something lower than 0
                    while (true) {
                        tick++
                        val progress = BowItem.getPullProgress(tick)
                        if (lastProgress == progress) // We cant charge up anymore than this
                            break
                        lastProgress = progress
                    }
                    tick
                }

                UseAction.SPEAR -> 10 // TridentItem#onStoppedUsing hard coded value, mojang is shit
                else -> {
                    var longest = 0
                    Registry.ITEM.forEach {
                        val stack = ItemStack(it)
                        if (it.getUseAction(stack) == useAction) {
                            val maxUseTime = it.getMaxUseTime(stack)
                            if (maxUseTime > longest)
                                longest = maxUseTime
                        }
                    }
                    longest
                }
            }
            settings[useAction] = object : ValueNumber(this, nameMap[useAction] + ": Ticks", 0.0, longest.toDouble(), longest.toDouble(), 1.0) {
                override fun isEnabled() = actions.selected.contains(nameMap[useAction])
            }
        }
    }

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventUpdate && event.state == EventUpdate.State.PRE_PACKET) {
            if (mc.player?.isUsingItem == true) {
                val usedStack = mc.player?.getStackInHand(PlayerUtil.getUsedHand() ?: return@Consumer)!!
                if (useActions.contains(usedStack.useAction) && actions.selected.contains(nameMap[usedStack.useAction!!])) {
                    val useTime = mc.player?.itemUseTime!!
                    if (useTime > settings[usedStack.useAction]?.value!!) {
                        for (i in 0..useTime) {
                            mc.networkHandler?.sendPacket(OnGroundOnly(mc.player?.isOnGround!!))
                        }
                        if (usedStack.useAction == UseAction.BOW || usedStack.useAction == UseAction.SPEAR || usedStack.isUsedOnRelease)
                            mc.interactionManager?.stopUsingItem(mc.player)
                    }
                }
            }
        }
    }

}