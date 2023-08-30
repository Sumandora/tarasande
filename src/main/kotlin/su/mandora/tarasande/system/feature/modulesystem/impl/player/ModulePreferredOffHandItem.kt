package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.screen.slot.SlotActionType
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.event.impl.EventScreenInput
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueRegistry
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.system.screen.informationsystem.Information
import su.mandora.tarasande.system.screen.informationsystem.ManagerInformation
import su.mandora.tarasande.util.extension.kotlinruntime.nullOr
import su.mandora.tarasande.util.extension.kotlinruntime.prefer
import su.mandora.tarasande.util.extension.minecraft.safeCount
import su.mandora.tarasande.util.player.container.ContainerUtil

class ModulePreferredOffHandItem : Module("Preferred off-hand item", "Equips an item in the off-hand slot", ModuleCategory.PLAYER) {
    private val openInventory = ValueBoolean(this, "Open inventory", true)
    private val items = object : ValueRegistry<Item>(this, "Items", Registries.ITEM, false, Items.TOTEM_OF_UNDYING) {
        override fun filter(key: Item) = key != Items.AIR
        override fun getTranslationKey(key: Any?) = (key as Item).translationKey
    }
    private val useSwapKey = ValueBoolean(this, "Use swap key", true)

    init {
        ManagerInformation.add(object : Information(name, "Reserve") {
            override fun getMessage(): String? {
                if (!enabled.value)
                    return null

                val amount = ContainerUtil.getValidSlots(mc.player?.playerScreenHandler!!).filter { it.stack.isOf(items.getSelected()) }.sumOf { it.stack.safeCount() }
                if (amount == 0)
                    return null

                return amount.toString()
            }
        })
    }

    init {
        registerEvent(EventScreenInput::class.java, 999) { event ->
            if (event.doneInput) {
                return@registerEvent
            }

            if (mc.player == null || (openInventory.value && mc.currentScreen !is AbstractInventoryScreen<*>)) {
                return@registerEvent
            }

            val screenHandler = mc.player!!.playerScreenHandler

            if (screenHandler.cursorStack.isOf(items.getSelected()) && mc.player!!.offHandStack.nullOr { it.isEmpty }) {
                mc.interactionManager!!.clickSlot(screenHandler.syncId, PlayerScreenHandler.OFFHAND_ID, GLFW.GLFW_MOUSE_BUTTON_LEFT, SlotActionType.PICKUP, mc.player)
                event.doneInput = true
                return@registerEvent
            }

            if (!screenHandler.cursorStack.nullOr { it.isEmpty } || mc.player?.offHandStack?.isEmpty == false) {
                return@registerEvent
            }

            val items = ContainerUtil.getValidSlots(screenHandler).filter { it.stack.isOf(items.getSelected()) }
            if (items.isEmpty())
                return@registerEvent

            val item = items.prefer { ContainerUtil.isInHotbar(it.id) }
            val offHandSlot = screenHandler.slots.firstOrNull { it.id == PlayerScreenHandler.OFFHAND_ID }

            if (offHandSlot?.isEnabled == true) {
                if (LivingEntity.getPreferredEquipmentSlot(item.stack) == EquipmentSlot.OFFHAND)
                    mc.interactionManager!!.clickSlot(screenHandler.syncId, item.id, GLFW.GLFW_MOUSE_BUTTON_LEFT, SlotActionType.QUICK_MOVE, mc.player)
                else if (ContainerUtil.isInHotbar(item.id) || useSwapKey.value) {
                    mc.interactionManager!!.clickSlot(screenHandler.syncId, item.id, ContainerUtil.getOffHandSlot(screenHandler).id, SlotActionType.SWAP, mc.player)
                } else {
                    mc.interactionManager!!.clickSlot(screenHandler.syncId, item.id, GLFW.GLFW_MOUSE_BUTTON_LEFT, SlotActionType.PICKUP, mc.player)
                }
                event.doneInput = true
            }
        }
    }
}