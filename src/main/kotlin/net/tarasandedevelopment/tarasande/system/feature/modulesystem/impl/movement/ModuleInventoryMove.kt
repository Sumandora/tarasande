package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.InputUtil
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.feature.friend.panel.PanelElementsFriends
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.ElementWidthValueComponentFocusable
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.panel.element.PanelElementsCategory
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterFileChooser
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande.util.extension.mc
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class ModuleInventoryMove : Module("Inventory move", "Allows you to move while in inventory", ModuleCategory.MOVEMENT) {

    private val updateSneaking = ValueBoolean(this, "Update sneaking", false)

    private val movementKeys = ArrayList(PlayerUtil.movementKeys)

    private var textBoxFocused = false

    init {
        movementKeys.add(mc.options.jumpKey)
        movementKeys.add(mc.options.sneakKey)
    }

    init {
        registerEvent(EventKeyBindingIsPressed::class.java, 1) { event ->
            if (isPassingEvents())
                if (movementKeys.contains(event.keyBinding))
                    if (event.keyBinding != mc.options.sneakKey || updateSneaking.value)
                        event.pressed = InputUtil.isKeyPressed(mc.window.handle, event.keyBinding.boundKey.code)
        }

        registerEvent(EventTick::class.java) { event ->
            if (event.state == EventTick.State.POST)
                textBoxFocused = isTextBoxFocused()
        }
    }

    private fun isFocused(valueComponent: ElementWidthValueComponent) = valueComponent is ElementWidthValueComponentFocusable && valueComponent.isFocused()

    private fun isTextBoxFocused(): Boolean {
        if(mc.currentScreen is ScreenBetterOwnerValues) {
            return (mc.currentScreen as ScreenBetterOwnerValues).panel.elementList.any { isFocused(it) }
        }
        return TarasandeMain.managerPanel().list.any {
            when (it) {
                is PanelElementsCategory -> it.elementList.any { moduleElement -> moduleElement.components.any { component -> isFocused(component) } }
                is PanelElementsFriends -> it.elementList.any { playerElement -> playerElement.textField.isFocused() }
                else -> false
            }
        }
    }

    private fun isPassingEvents() = (mc.currentScreen is ScreenCheatMenu || mc.currentScreen is ScreenBetterOwnerValues || mc.currentScreen is ScreenBetterFileChooser) && !textBoxFocused || mc.currentScreen is HandledScreen<*>
}
