package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.item.BowItem
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly
import net.minecraft.registry.Registries
import net.minecraft.util.UseAction
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.TRIDENT_USE_TIME
import su.mandora.tarasande.util.player.PlayerUtil
import kotlin.math.min

class ModuleFastUse : Module("Fast use", "Speeds up item usage", ModuleCategory.PLAYER) {

    private val useActions = arrayOf(UseAction.EAT, UseAction.DRINK, UseAction.BOW, UseAction.SPEAR, UseAction.CROSSBOW, UseAction.BRUSH)
    private lateinit var actions: ValueMode
    private val values = HashMap<UseAction, ValueNumber>()

    private val nameMap = hashMapOf(
        UseAction.EAT to "Eat",
        UseAction.DRINK to "Drink",
        UseAction.BOW to "Bow",
        UseAction.SPEAR to "Spear",
        UseAction.CROSSBOW to "Crossbow",
        UseAction.BRUSH to "Brush"
    )

    init {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            actions = ValueMode(this, "Actions", true, *useActions.map { nameMap[it]!! }.toTypedArray())
            for (useAction in useActions) {
                // This when is expressing my hate towards mojang really nicely
                val longest = when (useAction) {
                    UseAction.BOW -> {
                        var tick = 0
                        var lastProgress = 0F // something lower than 0
                        while (true) {
                            val progress = BowItem.getPullProgress(tick + 1)
                            if (lastProgress != progress)
                                tick++
                            else
                                break
                            lastProgress = progress
                        }
                        tick
                    }

                    UseAction.SPEAR -> TRIDENT_USE_TIME
                    else -> {
                        var longest = 0
                        Registries.ITEM.forEach {
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
                values[useAction] = ValueNumber(this, nameMap[useAction] + ": Ticks", 0.0, longest.toDouble(), longest.toDouble(), 1.0, isEnabled = { actions.isSelected(nameMap[useAction]!!) })
            }
        }
    }

    private val preventIllegalPackets = ValueBoolean(this, "Prevent illegal packet", true)

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                if (mc.player?.isUsingItem == true) {
                    val usedStack = mc.player?.getStackInHand(PlayerUtil.getUsedHand() ?: return@registerEvent)!!
                    if (useActions.contains(usedStack.useAction) && actions.isSelected(nameMap[usedStack.useAction!!]!!)) {
                        val useTime = mc.player?.itemUseTime!!
                        if (useTime > values[usedStack.useAction]?.value!!) {
                            val remainingTime = min(usedStack.maxUseTime, values[usedStack.useAction]!!.max.toInt()) - useTime
                            if (preventIllegalPackets.value) { // This will lead to more flags, but prevents simple protocol checks
                                var onGround = mc.player?.isOnGround!!
                                if (mc.player?.lastOnGround!! == onGround) {
                                    onGround = !onGround // The server already knows this state, use a different one
                                }
                                repeat(remainingTime) {
                                    mc.networkHandler?.sendPacket(OnGroundOnly(onGround))
                                    onGround = !onGround
                                }
                                mc.player?.lastOnGround = onGround
                            } else {
                                repeat(remainingTime) {
                                    mc.networkHandler?.sendPacket(OnGroundOnly(mc.player?.isOnGround!!))
                                }
                            }
                            if (usedStack.useAction == UseAction.BOW || usedStack.useAction == UseAction.SPEAR || usedStack.isUsedOnRelease)
                                mc.interactionManager?.stopUsingItem(mc.player)
                        }
                    }
                }
            }
        }
    }
}