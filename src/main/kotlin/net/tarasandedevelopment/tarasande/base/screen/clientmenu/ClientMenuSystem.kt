package net.tarasandedevelopment.tarasande.base.screen.clientmenu

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.minecraftbutton.PanelButton
import net.tarasandedevelopment.tarasande.screen.clientmenu.*
import net.tarasandedevelopment.tarasande.screen.widget.panel.ClickableWidgetPanel
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import org.lwjgl.glfw.GLFW
import java.awt.Color

class ManagerClientMenu : Manager<ElementMenu>() {

    init {
        this.add(
            // GUIs
            ElementMenuScreenAccountManager(),
            ElementMenuScreenProxySystem(),
            ElementMenuScreenProtocolHack(),
            ElementMenuScreenPackages(),

            // Exploits
            ElementMenuToggleBungeeHack(),
            ElementMenuToggleForgeFaker(),
            ElementMenuToggleHAProxyHack(),
            ElementMenuToggleQuiltFaker(),
            ElementMenuToggleClientBrandSpoofer()
        )
    }
}

abstract class ElementMenu(val name: String, val category: String) {

    internal fun onClickInternal(mouseButton: Int) {
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            if (TarasandeMain.get().managerValue.getValues(this@ElementMenu).isNotEmpty()) {
                MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(MinecraftClient.getInstance().currentScreen!!, name, this@ElementMenu))
                return
            }
        } else {
            this.onClick(mouseButton)
        }
    }

    open fun elementColor() : Int = Color.ORANGE.rgb
    open fun visible() = true
    abstract fun onClick(mouseButton: Int)
}

abstract class ElementMenuScreen(name: String, category: String) : ElementMenu(name, category) {

    override fun onClick(mouseButton: Int) {
        MinecraftClient.getInstance().setScreen(this.getScreen())
    }

    override fun elementColor() = -1
    abstract fun getScreen(): Screen
}

abstract class ElementMenuToggle(name: String, category: String) : ElementMenu(name, category) {

    var state = false

    override fun onClick(mouseButton: Int) {
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.state = !this.state
            this.onToggle(this.state)

            return
        }
    }

    override fun elementColor() = if (state) Color.green.rgb else Color.red.rgb
    abstract fun onToggle(state: Boolean)
}

object ElementCategory {
    const val GENERAL = "General"
    const val EXPLOITS = "Exploits"
}
