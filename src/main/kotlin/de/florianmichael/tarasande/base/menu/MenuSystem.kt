package de.florianmichael.tarasande.base.menu

import de.florianmichael.tarasande.menu.*
import de.florianmichael.tarasande.mixin.accessor.IClickableWidget
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import su.mandora.tarasande.base.Manager
import java.awt.Color

class ManagerMenu : Manager<ElementMenu>() {

    init {
        this.spacer("General")
        this.add(ElementMenuScreenAccountManager(), ElementMenuScreenProxySystem(), ElementMenuScreenProtocolHack())
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
        return ButtonWidget(x, y, width, height, this.buttonText()) {
            this.onClick()
            it.message = this.buttonText()
        }
    }

    open fun buttonColor() : Int = Color.ORANGE.rgb
    open fun buttonText() : Text = Text.literal(this.name).styled {
        it.withColor(this.buttonColor())
    }

    abstract fun onClick()
}

abstract class ElementMenuScreen(name: String) : ElementMenu(name) {

    override fun onClick() {
        MinecraftClient.getInstance().setScreen(this.getScreen())
    }

    override fun buttonColor() = Color.white.rgb

    abstract fun getScreen(): Screen
}

abstract class ElementMenuToggle(name: String) : ElementMenu(name) {

    internal var state = false

    override fun onClick() {
        this.state = !this.state
        this.onToggle(this.state)
    }

    override fun buttonColor() = if (state) Color.green.rgb else Color.red.rgb

    abstract fun onToggle(state: Boolean)
}

class ElementMenuTitle(name: String) : ElementMenu(name) {
    override fun onClick() {
    }

    override fun buttonColor() = Color.gray.rgb

    override fun buildWidget(x: Int, y: Int, width: Int, height: Int): ButtonWidget {
        val widget = super.buildWidget(x, y, width, height)
        (widget as IClickableWidget).florianMichael_removeBackground()
        return widget
    }
}
