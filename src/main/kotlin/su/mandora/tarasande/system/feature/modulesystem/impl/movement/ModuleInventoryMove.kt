package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.InputUtil
import su.mandora.tarasande.event.impl.EventKeyBindingIsPressed
import su.mandora.tarasande.event.impl.EventTick
import su.mandora.tarasande.feature.friend.panel.PanelElementsFriends
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.ElementWidthValueComponentFocusable
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.system.feature.modulesystem.panel.element.PanelElementsCategory
import su.mandora.tarasande.system.screen.panelsystem.ManagerPanel
import su.mandora.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import su.mandora.tarasande.system.screen.panelsystem.screen.panelscreen.ScreenPanel
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleInventoryMove : Module("Inventory move", "Allows you to move while in inventory", ModuleCategory.MOVEMENT) {

    private val updateSneaking = ValueBoolean(this, "Update sneaking", false)
    private val onlyInPlayerInventory = ValueBoolean(this, "Only in player inventory", false)

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

    private fun isFocused(valueComponent: ElementWidthValueComponent<*>) = valueComponent is ElementWidthValueComponentFocusable<*> && valueComponent.isFocused()

    private fun isTextBoxFocused(): Boolean {
        if (mc.currentScreen is ScreenBetterOwnerValues) {
            return (mc.currentScreen as ScreenBetterOwnerValues).panel.elementList.any { isFocused(it) }
        }
        return ManagerPanel.list.any {
            when (it) {
                is PanelElementsCategory -> it.elementList.any { moduleElement -> moduleElement.components.any { component: ElementWidthValueComponent<*> -> isFocused(component) } }
                is PanelElementsFriends -> it.elementList.any { playerElement -> playerElement.textField.isFocused() }
                else -> false
            }
        }
    }

    private fun isPassingEvents() = (mc.currentScreen is ScreenPanel || mc.currentScreen is ScreenBetterOwnerValues) && !textBoxFocused || (if (onlyInPlayerInventory.value) mc.currentScreen is AbstractInventoryScreen<*> else mc.currentScreen is HandledScreen<*>)
}
