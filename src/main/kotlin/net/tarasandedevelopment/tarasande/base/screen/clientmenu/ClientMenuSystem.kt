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
import net.tarasandedevelopment.tarasande.value.ValueMode
import org.lwjgl.glfw.GLFW
import org.spongepowered.asm.mixin.Unique
import java.awt.Color

class ManagerClientMenu : Manager<ElementMenu>() {

    private val clientMenuFocusedEntry: ValueMode
    internal val clientMenuCategories = ValueBoolean(this, "Show categories", false)

    init {
        val fritzBox = ElementMenuFritzBoxReconnect()

        this.add(
            ElementMenuTitle("General"),

            ElementMenuScreenAccountManager(),
            ElementMenuScreenProxySystem(),
            ElementMenuScreenProtocolHack(),
            ElementMenuScreenAddons(),

            ElementMenuTitle("Exploits"),
            ElementMenuToggleBungeeHack(),
            ElementMenuToggleForgeFaker(),
            ElementMenuToggleHAProxyHack(),

            ElementMenuTitleConditional("Special") { fritzBox.visible() },
            fritzBox
        )

        val entries = mutableListOf("None")
        entries.addAll(list.filterIsInstance<ElementMenuScreen>().map { e -> e.name })

        clientMenuFocusedEntry = ValueMode(this, "Focused entry", false, *entries.toTypedArray())
    }

    private fun byName(name: String): ElementMenu {
        return this.list.first { it.name.equals(name, true) }
    }

    @Unique
    private fun anySelected(): Boolean {
        return clientMenuFocusedEntry.anySelected() && clientMenuFocusedEntry.selected[0] != "None"
    }

    private fun createButtonText(): String {
        val selected = clientMenuFocusedEntry.selected[0]

        val buttonText = if (anySelected()) {
            selected
        } else {
            TarasandeMain.get().name.let { it[0].uppercaseChar().toString() + it.substring(1) + " Menu" }
        }

        return buttonText
    }

    fun createClientMenuButton(x: Int, y: Int, width: Int, height: Int, parent: Screen): ClickableWidgetPanel {
        val selected = clientMenuFocusedEntry.selected[0]

        return PanelButton.createButton(x, y, width, height, this.createButtonText()) {
            if (this.anySelected() && !Screen.hasShiftDown()) {
                val screen = byName(selected)
                if (screen.visible()) {
                    screen.onClick(GLFW.GLFW_MOUSE_BUTTON_LEFT)
                    return@createButton
                }
            }
            MinecraftClient.getInstance().setScreen(ScreenBetterSlotListClientMenu(parent))
        }
    }
}

abstract class ElementMenu(val name: String, val locked: Boolean = false) {

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
    open fun elementTextSize() : Float = 1.0F
    open fun visible() = true
    abstract fun onClick(mouseButton: Int)
}

abstract class ElementMenuScreen(name: String) : ElementMenu(name) {

    override fun onClick(mouseButton: Int) {
        MinecraftClient.getInstance().setScreen(this.getScreen())
    }

    override fun elementColor() = -1
    abstract fun getScreen(): Screen
}

abstract class ElementMenuToggle(name: String) : ElementMenu(name) {

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

open class ElementMenuTitle(name: String) : ElementMenu(name, true) {
    override fun onClick(mouseButton: Int) {
    }

    override fun elementColor() = Color.gray.rgb
    override fun elementTextSize() = 1.5F
}

open class ElementMenuTitleConditional(name: String, private val conditional: () -> Boolean) : ElementMenu(name, true) {
    override fun onClick(mouseButton: Int) {
    }

    override fun elementColor() = Color.gray.rgb
    override fun elementTextSize() = 1.5F

    override fun visible() = conditional.invoke()
}