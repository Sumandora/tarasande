package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.AxeItem
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
import su.mandora.tarasande.util.extension.minecraft.isBlockingDamage
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.player.container.ContainerUtil

class ModuleAutoTool : Module("Auto tool", "Selects the best tool when breaking a block", ModuleCategory.PLAYER) {

    val mode = ValueMode(this, "Mode", true, "Blocks", "Entities")

    val useAxeToCounterBlocking = ValueBoolean(this, "Use axe to counter blocking", false, isEnabled = { mode.isSelected(1) })
    private val simulateBlock = ValueBoolean(this, "Simulate block", false, isEnabled = { useAxeToCounterBlocking.value })
    private val preferSwords = ValueBoolean(this, "Prefer swords", false, isEnabled = { mode.isSelected(1) })

    private val moduleHealingBot by lazy { ManagerModule.get(ModuleHealingBot::class.java) }

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

                if (pair.second == mc.player!!.inventory.selectedSlot)
                    return@registerEvent

                val currentSpeed = PlayerUtil.getBreakSpeed(blockPos, mc.player!!.inventory.selectedSlot)
                if (currentSpeed == pair.first)
                    return@registerEvent

                val bestTool = pair.second
                if (bestTool == -1)
                    return@registerEvent
                mc.player!!.inventory.selectedSlot = bestTool
            }
        }

        registerEvent(EventAttackEntity::class.java) { event ->
            if (event.state == EventAttackEntity.State.PRE) {
                if (!mode.isSelected(1))
                    return@registerEvent
                if (moduleHealingBot.let { it.enabled.value && it.state != ModuleHealingBot.State.IDLE })
                    return@registerEvent

                var hotbar = ContainerUtil.getHotbarSlots().mapIndexed { index, itemStack -> index to itemStack }

                if (useAxeToCounterBlocking.value && event.entity is PlayerEntity && event.entity.isBlockingDamage(simulateBlock.value))
                    if (hotbar.any { it.second.item is AxeItem })
                        hotbar = hotbar.filter { it.second.item is AxeItem }

                if (preferSwords.value)
                    if (hotbar.any { it.second.item is SwordItem })
                        hotbar = hotbar.filter { it.second.item is SwordItem }

                hotbar = hotbar.filter { it.second.item is ToolItem }

                val best = hotbar.maxByOrNull { (_, item) -> ContainerUtil.wrapMaterialDamage(item) + ContainerUtil.getProperEnchantments(item).filter { it.key.target == EnchantmentTarget.WEAPON }.values.sum() }

                if (best != null)
                    mc.player?.inventory?.selectedSlot = best.first
            }
        }
    }
}