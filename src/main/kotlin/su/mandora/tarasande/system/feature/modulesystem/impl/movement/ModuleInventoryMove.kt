package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.InputUtil
import su.mandora.tarasande.event.impl.EventKeyBindingIsPressed
import su.mandora.tarasande.event.impl.EventTick
import su.mandora.tarasande.feature.friend.panel.PanelElementsFriends
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.ElementWidthValueComponentFocusable
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.system.feature.modulesystem.panel.element.PanelElementsCategory
import su.mandora.tarasande.system.screen.panelsystem.ManagerPanel
import su.mandora.tarasande.system.screen.panelsystem.api.PanelElements
import su.mandora.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterPanel
import su.mandora.tarasande.system.screen.panelsystem.screen.panelscreen.ScreenPanel
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.render.helper.element.ElementWidth

class ModuleInventoryMove : Module("Inventory move", "Allows you to move while in inventory", ModuleCategory.MOVEMENT) {

    private val updateSneaking = ValueBoolean(this, "Update sneaking", false)
    private val screens = ValueMode(this, "Screens", true, "Panel screen", "Player inventory", "Containers")

    init {
        for (i in 0 until screens.values.size) screens.select(i)
    }

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

    private fun isFocused(valueComponent: ElementWidth) = valueComponent is ElementWidthValueComponentFocusable<*> && valueComponent.isFocused()

    private fun isTextBoxFocused(): Boolean {
        if (mc.currentScreen is ScreenBetterPanel) {
            val panel = (mc.currentScreen as ScreenBetterPanel).panel
            if(panel is PanelElements<*>)
                return panel.elementList.any { isFocused(it) }
        }
        if (mc.currentScreen is ScreenPanel) {
            return ManagerPanel.list.any {
                when (it) {
                    is PanelElementsCategory -> it.elementList.any { moduleElement -> moduleElement.components.any { component: ElementWidthValueComponent<*> -> isFocused(component) } }
                    is PanelElementsFriends -> it.elementList.any { playerElement -> playerElement.textField.isFocused() }
                    else -> false
                }
            }
        }
        return false
    }

    fun isPassingEvents(): Boolean {
        if (screens.isSelected(0)) if (mc.currentScreen.let { it is ScreenPanel || it is ScreenBetterPanel } && !isTextBoxFocused()) return true
        if (screens.isSelected(1)) if (mc.currentScreen is AbstractInventoryScreen<*>) return true
        if (screens.isSelected(2)) if (mc.currentScreen is HandledScreen<*> && mc.currentScreen !is AbstractInventoryScreen<*>) return true

        return false
    }
}
