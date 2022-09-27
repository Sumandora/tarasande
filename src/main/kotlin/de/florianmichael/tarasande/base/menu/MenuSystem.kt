package de.florianmichael.tarasande.base.menu

import de.florianmichael.tarasande.menu.ElementMenuScreenAccountManager
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import su.mandora.tarasande.base.Manager

class ManagerMenu : Manager<ElementMenu>() {

    init {
        add(
            ElementMenuScreenAccountManager()
        )
    }

    fun byName(name: String): ElementMenu {
        return this.list.first { e -> e.name.equals(name, true) }
    }
}

abstract class ElementMenu(val name: String) {

    open fun buildWidget(x: Int, y: Int, width: Int, height: Int): ButtonWidget {
        return ButtonWidget(x, y, width, height, Text.literal(this.name)) { this.onClick() }
    }

    abstract fun onClick()
}

abstract class ElementMenuScreen(name: String) : ElementMenu(name) {

    override fun onClick() {
        MinecraftClient.getInstance().setScreen(this.getScreen())
    }

    abstract fun getScreen(): Screen
}

abstract class ElementMenuToggle(name: String) : ElementMenu(name) {

    internal var state = false

    override fun onClick() {
        this.state = !this.state
        this.onToggle(this.state)
    }

    private fun displayName(): String {
        return (if (state) Formatting.GREEN.toString() else Formatting.RED.toString()) + this.name
    }

    override fun buildWidget(x: Int, y: Int, width: Int, height: Int): ButtonWidget {
        return ButtonWidget(x, y, width, height, Text.literal(this.displayName())) {
            this.onClick()
            it.message = Text.literal(this.displayName())
        }
    }

    abstract fun onToggle(state: Boolean)
}
