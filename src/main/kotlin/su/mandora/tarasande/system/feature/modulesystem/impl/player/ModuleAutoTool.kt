package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.AxeItem
import net.minecraft.item.ItemStack
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.util.hit.BlockHitResult
import su.mandora.tarasande.event.impl.EventAttackEntity
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.system.feature.modulesystem.impl.combat.ModuleHealingBot
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.player.container.ContainerUtil

class ModuleAutoTool : Module("Auto tool", "Selects the best tool for breaking a block", ModuleCategory.PLAYER) {

    val mode = ValueMode(this, "Mode", true, "Blocks", "Entities")

    val useAxeToCounterBlocking = ValueBoolean(this, "Use axe to counter blocking", false, isEnabled = { mode.isSelected(1) })
    private val despiseAxes = ValueBoolean(this, "Despise axes", false, isEnabled = { mode.isSelected(1) })

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                if (!mode.isSelected(0))
                    return@registerEvent
                if (!mc.interactionManager?.isBreakingBlock!!)
                    return@registerEvent
                if (mc.crosshairTarget !is BlockHitResult)
                    return@registerEvent
                val blockPos = (mc.crosshairTarget as BlockHitResult).blockPos
                val pair = PlayerUtil.getBreakSpeed(blockPos)

                if (pair.second == mc.player?.inventory?.selectedSlot)
                    return@registerEvent

                val currentSpeed = PlayerUtil.getBreakSpeed(blockPos, mc.player?.inventory?.selectedSlot ?: return@registerEvent)
                if (currentSpeed == pair.first)
                    return@registerEvent

                val bestTool = pair.second
                if (bestTool == -1)
                    return@registerEvent
                mc.player?.inventory?.selectedSlot = bestTool
            }
        }

        registerEvent(EventAttackEntity::class.java) { event ->
            if (event.state == EventAttackEntity.State.PRE) {
                if (!mode.isSelected(1))
                    return@registerEvent
                if (ManagerModule.get(ModuleHealingBot::class.java).let { it.enabled.value && it.state != ModuleHealingBot.State.IDLE })
                    return@registerEvent

                var hotbar: Iterable<IndexedValue<ItemStack>> = ContainerUtil.getHotbarSlots().withIndex()

                if(useAxeToCounterBlocking.value && event.entity is PlayerEntity && event.entity.isBlocking) {
                    if(hotbar.any { it.value.item is AxeItem })
                        hotbar = hotbar.filter { it.value.item is AxeItem }
                }
                if(despiseAxes.value)
                    if(hotbar.any { it.value.item is SwordItem })
                        hotbar = hotbar.filter { it.value.item is SwordItem }

                hotbar = hotbar.filter { it.value.item is ToolItem }

                val best = hotbar.maxByOrNull { ContainerUtil.wrapMaterialDamage(it.value) + ContainerUtil.getProperEnchantments(it.value).filter { it.key.target == EnchantmentTarget.WEAPON }.values.sum() }

                if (best != null)
                    mc.player?.inventory?.selectedSlot = best.index
            }
        }
    }
}