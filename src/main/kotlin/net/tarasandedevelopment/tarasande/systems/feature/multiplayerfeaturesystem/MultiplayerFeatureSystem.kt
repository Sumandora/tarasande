package net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.DirectConnectScreen
import net.minecraft.client.gui.screen.GameMenuScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.meta.ValueSpacer
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.impl.*
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtensionCustom
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.api.ClickableWidgetPanelSidebar
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.api.PanelElements
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import org.lwjgl.glfw.GLFW
import java.awt.Color

class ManagerMultiplayerFeature : Manager<MultiplayerFeature>() {

    private val panelElementsMultiplayerFeature = PanelElements<ElementWidthValueComponent>("Sidebar", 120.0, 0.0)

    init {
        this.add(
            // Exploits
            MultiplayerFeatureToggleableExploitsBungeeHack(),
            MultiplayerFeatureToggleableExploitsForgeFaker(),
            MultiplayerFeatureToggleableExploitsHAProxyHack(),
            MultiplayerFeatureToggleableExploitsQuiltFaker(),
            MultiplayerFeatureToggleableExploitsClientBrandSpoofer()
        )

        list.filterIsInstance<MultiplayerFeatureToggleable>().forEach {
            it.state.owner = this
        }

        TarasandeMain.managerScreenExtension().add(object : ScreenExtensionCustom<Screen>("Multiplayer feature", MultiplayerScreen::class.java, DirectConnectScreen::class.java, GameMenuScreen::class.java) {

            override fun createElements(screen: Screen): List<Element> {
                return listOf(
                    ClickableWidgetPanelSidebar(panelElementsMultiplayerFeature)
                )
            }
        })
    }

    override fun insert(obj: MultiplayerFeature, index: Int) {
        super.insert(obj, index)
        panelElementsMultiplayerFeature.apply {
            elementList.clear()
            val categories = ArrayList<String>()
            this@ManagerMultiplayerFeature.list.forEach {
                if (!categories.contains(it.category))
                    categories.add(it.category)
            }

            categories.forEach { localEach ->
                elementList.add(object : ValueSpacer(this@ManagerMultiplayerFeature, localEach, 1.0F) {
                    override fun getColor(hovered: Boolean) = Color.gray
                }.createValueComponent())
                this@ManagerMultiplayerFeature.list.filter { it.category == localEach }.forEach {
                    panelElementsMultiplayerFeature.elementList.addAll(it.createElements().map { it.value.owner = this@ManagerMultiplayerFeature; it })
                }
            }
        }
    }
}

open class MultiplayerFeature(val name: String, val category: String) {

    fun openSettings() {
        if (TarasandeMain.managerValue().getValues(this).isNotEmpty()) {
            MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(MinecraftClient.getInstance().currentScreen!!, name, this))
        }
    }

    open fun onClick(mouseButton: Int) {
    }

    open fun createElements(): List<ElementWidthValueComponent> {
        return listOf(object : ValueSpacer(this, name, 1.0F) {
            override fun onChange(mouseButton: Int) {
                if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    onClick(mouseButton)
                } else if(mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    openSettings()
                }
            }
        }.createValueComponent())
    }
}

open class MultiplayerFeatureSelection(name: String, category: String, val list: List<String>, var selected: String) : MultiplayerFeature(name, category) {
    open fun onClick(newValue: String) {
    }

    override fun createElements(): List<ElementWidthValueComponent> {
        return list.map {
            object : ValueSpacer(this, it, 1.0F) {
                override fun onChange(mouseButton: Int) {
                    if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                        onClick(it)
                        selected = it
                    } else if(mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                        openSettings()
                    }
                }

                override fun getColor(hovered: Boolean): Color {
                    if (it == selected) {
                        return TarasandeMain.clientValues().accentColor.getColor()
                    }
                    return super.getColor(hovered)
                }
            }.createValueComponent()
        }
    }
}

open class MultiplayerFeatureToggleable(name: String, category: String) : MultiplayerFeature(name, category) {
    val state = ValueBoolean(this, name, false)
    open fun onClick(state: Boolean) {
    }

    override fun createElements(): List<ElementWidthValueComponent> {
        return listOf(object : ValueSpacer(this, name, 1.0F) {
            override fun onChange(mouseButton: Int) {
                if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    state.value = !state.value
                    onClick(state.value)
                } else if(mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    openSettings()
                }
            }
            override fun getColor(hovered: Boolean) = (if (state.value) Color.green else Color.red).let { if(hovered) RenderUtil.colorInterpolate(it, TarasandeMain.clientValues().accentColor.getColor(), 0.4) else it }
        }.createValueComponent())
    }
}

object MultiplayerFeatureCategory {

    const val GENERAL = "General"
    const val PROTOCOL_HACK = "Protocol Hack"
    const val EXPLOITS = "Exploits"
}
