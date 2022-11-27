package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.feature.friend.panel.PanelElementsFriends
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.ElementWidthValueComponentRegistry
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.ElementWidthValueComponentText
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.ElementWidthValueComponentTextList
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.panel.element.PanelElementsCategory
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class ModuleInventoryMove : Module("Inventory move", "Allows you to move while in inventory", ModuleCategory.MOVEMENT) {

    val cancelClosePacket = ValueBoolean(this, "Cancel close packets", false)
    private val updateSneaking = ValueBoolean(this, "Update sneaking", false)

    private val movementKeys = ArrayList(PlayerUtil.movementKeys)

    private var textBoxFocused = false

    init {
        movementKeys.add(mc.options.jumpKey)
        movementKeys.add(mc.options.sneakKey)
    }

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.SEND) {
                if (cancelClosePacket.value && event.packet is CloseHandledScreenC2SPacket && event.packet.syncId == 0)
                    event.cancelled = true
            }
        }

        registerEvent(EventKeyBindingIsPressed::class.java, 1) { event ->
            if (isPassingEvents())
                if (movementKeys.contains(event.keyBinding))
                    if (event.keyBinding != mc.options.sneakKey || updateSneaking.value)
                        event.pressed = InputUtil.isKeyPressed(mc.window?.handle!!, event.keyBinding.boundKey.code)
        }

        registerEvent(EventTick::class.java) { event ->
            if (event.state == EventTick.State.POST)
                textBoxFocused = isTextboxFocused()
        }
    }

    private fun isFocused(valueComponent: ElementWidthValueComponent): Boolean = when (valueComponent) {
        is ElementWidthValueComponentText -> valueComponent.isFocused()
        is ElementWidthValueComponentRegistry -> valueComponent.isFocused()
        is ElementWidthValueComponentTextList -> valueComponent.isFocused()
        else -> false
    }

    private fun isTextboxFocused(): Boolean {
        return TarasandeMain.managerPanel().list.any {
            when (it) {
                is PanelElementsCategory -> it.elementList.any { it.components.any { isFocused(it) } }
                is PanelElementsFriends -> it.elementList.any { it.textField.isFocused() }
                else -> false
            }
        }
    }

    private fun isPassingEvents(): Boolean {
        return (enabled && (mc.currentScreen is HandledScreen<*> || (mc.currentScreen is ScreenCheatMenu && !textBoxFocused))) || (mc.currentScreen == null || mc.currentScreen?.passEvents == true)
    }
}
