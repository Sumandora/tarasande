package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar

import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.ManagerValue
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueSpacer
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.PanelElements
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.panel.ClickableWidgetPanelSidebar
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import org.lwjgl.glfw.GLFW
import java.awt.Color

class ManagerSidebar : Manager<SidebarEntry>() {

    fun build(): ClickableWidgetPanelSidebar {
        return PanelElements<ElementWidthValueComponent<*>>("Sidebar", 0.0, 0.0).let {
            val categories = ArrayList<String>()
            this@ManagerSidebar.list.forEach { entry ->
                if (!categories.contains(entry.category)) {
                    categories.add(entry.category)
                }
            }

            categories.forEach { category ->
                it.elementList.add(object : ValueSpacer(it, category, 1.0F, manage = false) {
                    override fun getColor(hovered: Boolean) = Color.gray
                }.createValueComponent())
                this@ManagerSidebar.list.filter { entry -> entry.category == category }.onEach { each ->
                    it.elementList.addAll(each.createElements(it))
                }
            }
            var width = 0.0F
            for (element in it.elementList) {
                if (element.value !is ValueSpacer) continue
                if (width < FontWrapper.getWidth(element.value.name)) {
                    width = FontWrapper.getWidth(element.value.name).toFloat()
                }
            }
            it.panelWidth = width.toDouble() + 4
            return@let ClickableWidgetPanelSidebar(it)
        }
    }
}

open class SidebarEntry(val name: String, val category: String) {

    fun openValues() {
        if (ManagerValue.getValues(this).isNotEmpty()) {
            mc.setScreen(ScreenBetterOwnerValues(name, mc.currentScreen!!, this))
        }
    }

    open fun onClick(mouseButton: Int) {
    }

    open fun createElements(owner: Any): List<ElementWidthValueComponent<*>> {
        return listOf(object : ValueSpacer(owner, name, 1.0F, manage = false) {
            override fun onClick(mouseButton: Int) {
                if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    this@SidebarEntry.onClick(mouseButton)
                } else if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    openValues()
                }
            }
        }.createValueComponent())
    }
}

@Suppress("unused") // Packages use this
abstract class SidebarEntrySelection(name: String, category: String, val list: List<String>) : SidebarEntry(name, category) {
    abstract fun onClick(newValue: String)

    abstract fun isSelected(value: String): Boolean

    override fun createElements(owner: Any): List<ElementWidthValueComponent<*>> {
        return list.map {
            object : ValueSpacer(owner, it, 1.0F, manage = false) {
                override fun onClick(mouseButton: Int) {
                    if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                        onClick(it)
                    } else if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                        openValues()
                    }
                }

                override fun getColor(hovered: Boolean): Color {
                    if (isSelected(it)) {
                        return ClientValues.accentColor.getColor()
                    }
                    return super.getColor(hovered)
                }
            }.createValueComponent()
        }
    }
}

open class SidebarEntryToggleable(name: String, category: String) : SidebarEntry(name, category) {
    @Suppress("LeakingThis")
    val enabled = ValueBoolean(this, name, false)
    open fun onClick(enabled: Boolean) {
    }

    override fun createElements(owner: Any): List<ElementWidthValueComponent<*>> {
        return listOf(object : ValueSpacer(owner, name, 1.0F, manage = false) {
            override fun onClick(mouseButton: Int) {
                if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    enabled.value = !enabled.value
                    onClick(enabled.value)
                } else if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    openValues()
                }
            }

            override fun getColor(hovered: Boolean) = (if (enabled.value) Color.green else Color.red).let { if (hovered) RenderUtil.colorInterpolate(it, ClientValues.accentColor.getColor(), 0.4) else it }
        }.createValueComponent())
    }
}
