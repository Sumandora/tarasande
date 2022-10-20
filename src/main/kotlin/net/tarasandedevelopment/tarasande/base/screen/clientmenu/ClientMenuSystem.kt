package net.tarasandedevelopment.tarasande.base.screen.clientmenu

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.mixin.accessor.IClickableWidget
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.minecraftbutton.PanelButton
import net.tarasandedevelopment.tarasande.screen.clientmenu.*
import net.tarasandedevelopment.tarasande.screen.widget.AllMouseButtonWidget
import net.tarasandedevelopment.tarasande.screen.widget.AllMousePressAction
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

            ElementMenuFritzBoxReconnect.SubTitle(fritzBox),
            fritzBox
        )

        val entries = mutableListOf("None")
        entries.addAll(list.filterIsInstance<ElementMenuScreen>().map { e -> e.name })

        clientMenuFocusedEntry = ValueMode(this, "Focused entry", false, *entries.toTypedArray())
    }

    fun byName(name: String): ElementMenu {
        return this.list.first { e ->
            e.name.equals(name, true)
        }
    }

    @Unique
    private fun anySelected(): Boolean {
        return clientMenuFocusedEntry.anySelected() && clientMenuFocusedEntry.selected[0] != "None"
    }

    fun createButtonText(): String {
        val selected = clientMenuFocusedEntry.selected[0]

        val buttonText = if (anySelected()) {
            selected
        } else {
            TarasandeMain.get().name.let { it[0].uppercaseChar().toString() + it.substring(1) + " Menu" }
        }

        return buttonText
    }

    fun createClientMenuButton(x: Int, y: Int, width: Int, height: Int, parent: Screen, updating: Boolean): ClickableWidgetPanel {
        val selected = clientMenuFocusedEntry.selected[0]

        return PanelButton.createButton(x, y, width, height, this.createButtonText()) {
            if (this.anySelected() && !Screen.hasShiftDown()) {
                val screen = byName(selected)
                if (screen.visible()) {
                    screen.onClick(GLFW.GLFW_MOUSE_BUTTON_LEFT)
                    return@createButton
                }
            }
            MinecraftClient.getInstance().setScreen(ScreenBetterClientMenu(parent))
        }.also { it.updating = updating }
    }
}

abstract class ElementMenu(val name: String) {

    open fun buildWidget(x: Int, y: Int, width: Int, height: Int): ButtonWidget {
        val widget = AllMouseButtonWidget(x, y, width, height, this.buttonText(), object : AllMousePressAction() {
            override fun onPress(mouseButton: Int, button: ButtonWidget) {
                if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    if (TarasandeMain.get().managerValue.getValues(this@ElementMenu).isNotEmpty()) {
                        MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(MinecraftClient.getInstance().currentScreen!!, name, this@ElementMenu))
                        return
                    }
                }
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
    open fun visible() = true

    abstract fun onClick(mouseButton: Int)
}

abstract class ElementMenuScreen(name: String) : ElementMenu(name) {

    override fun onClick(mouseButton: Int) {
        MinecraftClient.getInstance().setScreen(this.getScreen())
    }

    override fun buttonColor() = -1
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
    }

    override fun buttonColor() = if (state) Color.green.rgb else Color.red.rgb

    abstract fun onToggle(state: Boolean)
}

open class ElementMenuTitle(name: String) : ElementMenu(name) {
    override fun onClick(mouseButton: Int) {
    }

    override fun buttonColor() = Color.gray.rgb

    override fun buildWidget(x: Int, y: Int, width: Int, height: Int): ButtonWidget {
        val widget = super.buildWidget(x, y, width, height)
        (widget as IClickableWidget).tarasande_removeBackground()
        return widget
    }
}
