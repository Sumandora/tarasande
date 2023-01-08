package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar

import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueSpacer
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.PanelElements
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.panel.ClickableWidgetPanelSidebar
import net.tarasandedevelopment.tarasande.util.extension.mc
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import org.lwjgl.glfw.GLFW
import java.awt.Color

class ManagerEntrySidebarPanel : Manager<EntrySidebarPanel>() {

    fun build(): ClickableWidgetPanelSidebar {
        return PanelElements<ElementWidthValueComponent>("Sidebar", 120.0, 0.0).let {
            val categories = ArrayList<String>()
            this@ManagerEntrySidebarPanel.list.forEach { entry ->
                if (!categories.contains(entry.category)) {
                    categories.add(entry.category)
                }
            }

            categories.forEach { category ->
                it.elementList.add(object : ValueSpacer(it, category, 1.0F, manage = false) {
                    override fun getColor(hovered: Boolean) = Color.gray
                }.createValueComponent())
                this@ManagerEntrySidebarPanel.list.filter { entry -> entry.category == category }.onEach { each ->
                    it.elementList.addAll(each.createElements(it))
                }
            }
            return@let ClickableWidgetPanelSidebar(it)
        }
    }
}

open class EntrySidebarPanel(val name: String, val category: String) {

    fun openValues() {
        if (TarasandeMain.managerValue().getValues(this).isNotEmpty()) {
            mc.setScreen(ScreenBetterOwnerValues(name, mc.currentScreen!!, this))
        }
    }

    open fun onClick(mouseButton: Int) {
    }

    open fun createElements(owner: Any): List<ElementWidthValueComponent> {
        return listOf(object : ValueSpacer(owner, name, 1.0F, manage = false) {
            override fun onChange(mouseButton: Int) {
                if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    onClick(mouseButton)
                } else if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    openValues()
                }
            }
        }.createValueComponent())
    }
}

abstract class EntrySidebarPanelSelection(name: String, category: String, val list: List<String>, var selected: String) : EntrySidebarPanel(name, category) {
    abstract fun onClick(newValue: String)

    override fun createElements(owner: Any): List<ElementWidthValueComponent> {
        return list.map {
            object : ValueSpacer(owner, it, 1.0F, manage = false) {
                override fun onChange(mouseButton: Int) {
                    if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                        onClick(it)
                        selected = it
                    } else if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                        openValues()
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

open class EntrySidebarPanelToggleable(sidebar: ManagerEntrySidebarPanel, name: String, category: String) : EntrySidebarPanel(name, category) {
    val enabled = ValueBoolean(sidebar, name, false)
    open fun onClick(enabled: Boolean) {
    }

    override fun createElements(owner: Any): List<ElementWidthValueComponent> {
        return listOf(object : ValueSpacer(owner, name, 1.0F, manage = false) {
            override fun onChange(mouseButton: Int) {
                if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    enabled.value = !enabled.value
                    onClick(enabled.value)
                } else if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    openValues()
                }
            }

            override fun getColor(hovered: Boolean) = (if (enabled.value) Color.green else Color.red).let { if (hovered) RenderUtil.colorInterpolate(it, TarasandeMain.clientValues().accentColor.getColor(), 0.4) else it }
        }.createValueComponent())
    }
}
