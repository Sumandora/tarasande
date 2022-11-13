package net.tarasandedevelopment.tarasande.systems.screen.clientmenu

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.clientmenu.*
import org.lwjgl.glfw.GLFW
import java.awt.Color

class ManagerClientMenu : Manager<ElementMenu>() {

    init {
        this.add(
            // GUIs
            ElementMenuScreenAccountManager(),
            ElementMenuScreenProxySystem(),
            ElementMenuScreenPackages(),

            // Exploits
            ElementMenuToggleBungeeHack(),
            ElementMenuToggleForgeFaker(),
            ElementMenuToggleHAProxyHack(),
            ElementMenuToggleQuiltFaker(),
            ElementMenuToggleClientBrandSpoofer()
        )

        list.filterIsInstance<ElementMenuToggle>().forEach {
            it.state.owner = this
        }
    }
}

abstract class ElementMenu(val name: String, val category: String) {

    internal fun onClickInternal(mouseButton: Int): Boolean {
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            if (TarasandeMain.managerValue().getValues(this@ElementMenu).isNotEmpty()) {
                MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(MinecraftClient.getInstance().currentScreen!!, name, this@ElementMenu))
                return true
            }
        } else {
            this.onClick(mouseButton)
            return true
        }
        return false
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

    val state = ValueBoolean(this, name, false)

    override fun onClick(mouseButton: Int) {
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.state.value = !this.state.value
            this.onToggle(this.state.value)

            return
        }
    }

    override fun elementColor() = if (this.state.value) Color.green.rgb else Color.red.rgb
    abstract fun onToggle(state: Boolean)
}

object ElementCategory {
    const val GENERAL = "General"
    const val EXPLOITS = "Exploits"
}
