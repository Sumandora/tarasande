package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.item.BowItem
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly
import net.minecraft.registry.Registries
import net.minecraft.util.UseAction
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.extension.mc
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import su.mandora.event.EventDispatcher

class ModuleFastUse : Module("Fast use", "Speeds up item usage", ModuleCategory.PLAYER) {

    private val useActions = arrayOf(UseAction.EAT, UseAction.DRINK, UseAction.BOW, UseAction.SPEAR, UseAction.CROSSBOW)
    private lateinit var actions: ValueMode
    private val values = HashMap<UseAction, ValueNumber>()

    private val nameMap = HashMap<UseAction, String>()

    init {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            actions = ValueMode(this, "Actions", true, *useActions.map { StringUtil.formatEnumTypes(it.name).also { name -> nameMap[it] = name } }.toTypedArray())
            for (useAction in useActions) {
                // This when is expressing my hate towards mojang really nicely
                val longest = when (useAction) {
                    UseAction.BOW -> {
                        var tick = 0
                        var lastProgress = 0.0F // something lower than 0
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
                values[useAction] = object : ValueNumber(this, nameMap[useAction] + ": Ticks", 0.0, longest.toDouble(), longest.toDouble(), 1.0) {
                    override fun isEnabled() = actions.selected.contains(nameMap[useAction])
                }
            }
        }
    }

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE_PACKET) {
                if (mc.player?.isUsingItem == true) {
                    val usedStack = mc.player?.getStackInHand(PlayerUtil.getUsedHand() ?: return@registerEvent)!!
                    if (useActions.contains(usedStack.useAction) && actions.selected.contains(nameMap[usedStack.useAction!!])) {
                        val useTime = mc.player?.itemUseTime!!
                        if (useTime > values[usedStack.useAction]?.value!!) {
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
}