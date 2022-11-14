package net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.GameMenuScreen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventChildren
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.meta.ValueSpacer
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.valuecomponent.ElementValueComponent
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.impl.*
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.impl.accountmanager.multiplayerfeature.MultiplayerFeatureAccountManager
import net.tarasandedevelopment.tarasande.systems.feature.multiplayerfeaturesystem.impl.proxy.multiplayerfeature.MultiplayerFeatureProxySystem
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.api.ClickableWidgetPanelSidebar
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.api.PanelElements
import org.lwjgl.glfw.GLFW
import su.mandora.event.EventDispatcher
import java.awt.Color

class ManagerMultiplayerFeature : Manager<MultiplayerFeature>() {

    private val panelElementsMultiplayerFeature = PanelElements<ElementValueComponent>("Sidebar", 120.0, 0.0)

    init {
        this.add(
            // General
            MultiplayerFeatureAccountManager(),
            MultiplayerFeatureProxySystem(),

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

        EventDispatcher.apply {
            add(EventChildren::class.java) {
                if (it.screen is MultiplayerScreen || it.screen is GameMenuScreen) {
                    it.add(ClickableWidgetPanelSidebar(panelElementsMultiplayerFeature))
                }
            }
        }
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
                    override fun getColor() = Color.gray
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

    open fun createElements(): List<ElementValueComponent> {
        return listOf(object : ValueSpacer(this, name, 1.0F) {
            override fun onChange(mouseButton: Int) {
                if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    openSettings()
                } else {
                    onClick(mouseButton)
                }
            }
        }.createValueComponent())
    }
}

open class MultiplayerFeatureSelection(name: String, category: String, val list: List<String>, var selected: String) : MultiplayerFeature(name, category) {
    open fun onClick(newValue: String) {
    }

    override fun createElements(): List<ElementValueComponent> {
        return list.map {
            object : ValueSpacer(this, it, 1.0F) {
                override fun onChange(mouseButton: Int) {
                    if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                        onClick(it)
                        selected = it
                    } else {
                        openSettings()
                    }
                }

                override fun getColor(): Color? {
                    if (it == selected) {
                        return TarasandeMain.clientValues().accentColor.getColor()
                    }
                    return super.getColor()
                }
            }.createValueComponent()
        }
    }
}

open class MultiplayerFeatureToggleable(name: String, category: String) : MultiplayerFeature(name, category) {
    val state = ValueBoolean(this, name, false)
    open fun onClick(state: Boolean) {
    }

    override fun createElements(): List<ElementValueComponent> {
        return listOf(object : ValueSpacer(this, name, 1.0F) {
            override fun onChange(mouseButton: Int) {
                if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    state.value = !state.value
                    onClick(state.value)
                } else {
                    openSettings()
                }
            }
            override fun getColor() = if (state.value) Color.green else Color.red
        }.createValueComponent())
    }
}

object MultiplayerFeatureCategory {

    const val GENERAL = "General"
    const val PROTOCOL_HACK = "Protocol Hack"
    const val EXPLOITS = "Exploits"
}
