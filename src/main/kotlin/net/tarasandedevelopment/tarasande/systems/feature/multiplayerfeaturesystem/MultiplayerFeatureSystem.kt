package net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.GameMenuScreen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventChildren
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.meta.ValueSpacer
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.valuecomponent.ElementValueComponent
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.impl.*
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.api.ClickableWidgetPanelSidebar
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.api.PanelElements
import org.lwjgl.glfw.GLFW
import su.mandora.event.EventDispatcher
import java.awt.Color

class ManagerMultiplayerFeature : Manager<MultiplayerFeature>() {

    private lateinit var panelElementsMultiplayerFeature: PanelElementsMultiplayerFeature

    init {
        this.add(
            // Exploits
            MultiplayerFeatureExploitsBungeeHack(),
            MultiplayerFeatureExploitsForgeFaker(),
            MultiplayerFeatureExploitsHAProxyHack(),
            MultiplayerFeatureExploitsQuiltFaker(),
            MultiplayerFeatureExploitsClientBrandSpoofer()
        )

        list.filterIsInstance<MultiplayerFeatureToggleable>().forEach {
            it.state.owner = this
        }

        EventDispatcher.add(EventChildren::class.java) {
            if (it.screen is MultiplayerScreen || it.screen is GameMenuScreen) {
                it.add(ClickableWidgetPanelSidebar(panelElementsMultiplayerFeature))
            }
        }

        EventDispatcher.add(EventSuccessfulLoad::class.java, 10000) {
            // Protocol Hack
            this.list.add(0, MultiplayerFeatureProtocolHack())

            panelElementsMultiplayerFeature = PanelElementsMultiplayerFeature(this)
        }
    }
}

class PanelElementsMultiplayerFeature(managerMultiplayerFeature: ManagerMultiplayerFeature) : PanelElements<ElementValueComponent>("Multiplayer Sidebar", 120.0, 0.0) {
    init {
        val categories = ArrayList<String>()
        managerMultiplayerFeature.list.forEach {
            if (!categories.contains(it.category))
                categories.add(it.category)
        }

        categories.forEach { localEach ->
            elementList.add(object : ValueSpacer(this, localEach, 1.0F) {
                override fun getColor() = Color.gray
            }.createValueComponent())
            managerMultiplayerFeature.list.filter { it.category == localEach }.forEach {
                it.createElements(this)
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

    open fun createElements(panel: PanelElements<ElementValueComponent>) {
        panel.elementList.add(object : ValueSpacer(panel, name, 1.0F) {
            override fun onChange(mouseButton: Int) {
                if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    openSettings()
                } else {
                    onClick(mouseButton)
                }
            }
            override fun getColor() = Color.white
        }.createValueComponent())
    }
}

open class MultiplayerFeatureSelection(name: String, category: String, val list: List<String>, var selected: String) : MultiplayerFeature(name, category) {
    open fun onChange(newValue: String) {
    }

    override fun createElements(panel: PanelElements<ElementValueComponent>) {
        list.forEach {
            panel.elementList.add(object : ValueSpacer(panel, it, 1.0F) {
                override fun onChange(mouseButton: Int) {
                    if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                        selected = it
                        onChange(it)
                    } else {
                        openSettings()
                    }
                }

                override fun getColor(): Color? {
                    if (it == selected) {
                        return TarasandeMain.clientValues().accentColor.getColor()
                    }
                    return Color.white
                }
            }.createValueComponent())
        }
    }
}

open class MultiplayerFeatureToggleable(name: String, category: String) : MultiplayerFeature(name, category) {
    val state = ValueBoolean(this, name, false)
    open fun onToggle(state: Boolean) {
    }

    override fun createElements(panel: PanelElements<ElementValueComponent>) {
        panel.elementList.add(object : ValueSpacer(panel, name, 1.0F) {
            override fun onChange(mouseButton: Int) {
                if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    state.value = !state.value
                    onToggle(state.value)
                } else {
                    openSettings()
                }
            }
            override fun getColor() = if (state.value) Color.green else Color.red
        }.createValueComponent())
    }
}

object MultiplayerFeatureCategory {
    const val PROTOCOL_HACK = "Protocol Hack"
    const val EXPLOITS = "Exploits"
}
