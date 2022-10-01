package net.tarasandedevelopment.tarasande.base.clientmenu

import net.tarasandedevelopment.tarasande.mixin.accessor.IClickableWidget
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.clientmenu.*
import net.tarasandedevelopment.tarasande.screen.widget.AllMouseButtonWidget
import net.tarasandedevelopment.tarasande.screen.widget.AllMousePressAction
import java.awt.Color

class ManagerClientMenu : Manager<ElementMenu>() {

    val settings: MenuSettingsParent

    init {
        this.spacer("General")
        this.add(ElementMenuScreenAccountManager(), ElementMenuScreenProxySystem(), ElementMenuScreenProtocolHack())

        this.spacer("Exploits")
        this.add(ElementMenuToggleBungeeHack())

        this.settings = MenuSettingsParent(this)
    }

    fun byName(name: String): ElementMenu {
        return this.list.first {
                e -> e.name.equals(name, true)
        }
    }

    private fun spacer(text: String) {
        this.add(ElementMenuTitle(text))
    }
}

abstract class ElementMenu(val name: String) {

    open fun buildWidget(x: Int, y: Int, width: Int, height: Int): ButtonWidget {
        val widget = AllMouseButtonWidget(x, y, width, height, this.buttonText(), object : AllMousePressAction() {
            override fun onPress(mouseButton: Int, button: ButtonWidget) {
                onClick(mouseButton)
                button.message = buttonText()
            }
        })
        return widget
    }

    open fun buttonColor() : Int = Color.ORANGE.rgb
    open fun buttonText() : Text = Text.literal(this.name).styled {
        it.withColor(this.buttonColor())
    }

    abstract fun onClick(mouseButton: Int)
}

abstract class ElementMenuScreen(name: String) : ElementMenu(name) {

    override fun onClick(mouseButton: Int) {
        MinecraftClient.getInstance().setScreen(this.getScreen())
    }

    override fun buttonColor() = Color.white.rgb
    abstract fun getScreen(): Screen
}

abstract class ElementMenuToggle(name: String) : ElementMenu(name) {

    internal var state = false

    override fun onClick(mouseButton: Int) {
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.state = !this.state
            this.onToggle(this.state)

            return
        }

        this.otherMouseHandling(mouseButton)
    }

    override fun buttonColor() = if (state) Color.green.rgb else Color.red.rgb

    abstract fun onToggle(state: Boolean)

    open fun otherMouseHandling(button: Int) {
    }
}

class ElementMenuTitle(name: String) : ElementMenu(name) {
    override fun onClick(mouseButton: Int) {
    }

    override fun buttonColor() = Color.gray.rgb

    override fun buildWidget(x: Int, y: Int, width: Int, height: Int): ButtonWidget {
        val widget = super.buildWidget(x, y, width, height)
        (widget as IClickableWidget).tarasande_removeBackground()
        return widget
    }
}
