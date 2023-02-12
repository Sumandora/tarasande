package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.screen.slot.SlotActionType
import net.tarasandedevelopment.tarasande.event.EventScreenInput
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueRegistry
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.ManagerInformation
import net.tarasandedevelopment.tarasande.util.extension.kotlinruntime.nullOr
import net.tarasandedevelopment.tarasande.util.extension.kotlinruntime.prefer
import net.tarasandedevelopment.tarasande.util.extension.minecraft.safeCount
import net.tarasandedevelopment.tarasande.util.player.container.ContainerUtil
import org.lwjgl.glfw.GLFW

class ModulePreferredOffHandItem : Module("Preferred off-hand item", "Equips your preferred item in the off-hand slot", ModuleCategory.PLAYER) {
    private val openInventory = ValueBoolean(this, "Open inventory", true)
    private val items = object : ValueRegistry<Item>(this, "Items", Registries.ITEM, false, Items.TOTEM_OF_UNDYING) {
        override fun filter(key: Item) = key != Items.AIR
        override fun getTranslationKey(key: Any?) = (key as Item).translationKey
    }
    private val allowIllegalSwapping = ValueBoolean(this, "Allow illegal swapping", false)

    init {
        ManagerInformation.add(object : Information(name, "Reserve") {
            override fun getMessage(): String? {
                if(!enabled.value)
                    return null

                return ContainerUtil.getValidSlots(mc.player?.playerScreenHandler!!).filter { it.stack.isOf(items.getSelected()) }.sumOf { it.stack.safeCount() }.toString()
            }
        })
    }

    init {
        registerEvent(EventScreenInput::class.java, 999) { event ->
            if (event.doneInput) {
                return@registerEvent
            }

            if (openInventory.value && mc.currentScreen !is AbstractInventoryScreen<*>) {
                return@registerEvent
            }

            val screenHandler = mc.player?.playerScreenHandler!!

            if(screenHandler.cursorStack?.isOf(items.getSelected()) == true && mc.player?.offHandStack.nullOr { it.isEmpty }) {
                mc.interactionManager?.clickSlot(screenHandler.syncId, ContainerUtil.offHandIndex.first, GLFW.GLFW_MOUSE_BUTTON_LEFT, SlotActionType.PICKUP, mc.player)
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
            val offHandSlot = screenHandler.slots.firstOrNull { it.id == ContainerUtil.offHandIndex.first }

            if (offHandSlot?.isEnabled == true) {
                if (LivingEntity.getPreferredEquipmentSlot(item.stack) == EquipmentSlot.OFFHAND)
                    mc.interactionManager?.clickSlot(screenHandler.syncId, item.id, GLFW.GLFW_MOUSE_BUTTON_LEFT, SlotActionType.QUICK_MOVE, mc.player)
                else if (ContainerUtil.isInHotbar(item.id) || allowIllegalSwapping.value) {
                    mc.interactionManager?.clickSlot(screenHandler.syncId, item.id, ContainerUtil.offHandIndex.second, SlotActionType.SWAP, mc.player)
                } else {
                    mc.interactionManager?.clickSlot(screenHandler.syncId, item.id, GLFW.GLFW_MOUSE_BUTTON_LEFT, SlotActionType.PICKUP, mc.player)
                }
                event.doneInput = true
            }
        }
    }
}