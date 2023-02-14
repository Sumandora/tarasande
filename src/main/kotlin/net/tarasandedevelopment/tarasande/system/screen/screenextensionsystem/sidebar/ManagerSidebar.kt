package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar

import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.TarasandeValues
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.impl.AccessibilityValues
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.ManagerValue
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueSpacer
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.PanelElements
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.panel.ClickableWidgetPanelSidebar
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.util.screen.ScreenUtil
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
                it.elementList.add(object : ValueSpacer(it, category, 0.75F, manage = false) {
                    override fun getColor(hovered: Boolean) = Color.gray
                }.createValueComponent())
                this@ManagerSidebar.list.filter { entry -> entry.category == category }.onEach { each ->
                    it.elementList.addAll(each.createElements(it))
                }
            }
            var width = 0.0F
            for (element in it.elementList) {
                if (element.value !is ValueSpacer) continue
                if (width < FontWrapper.getWidth(element.value.name) * 0.75F) {
                    width = FontWrapper.getWidth(element.value.name) * 0.75F
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
            if (AccessibilityValues.playSoundWhenClickingInSidebar.value) ScreenUtil.playClickSound()
        }
    }

    open fun onClick(mouseButton: Int) {
    }

    open fun createElements(owner: Any): List<ElementWidthValueComponent<*>> {
        return listOf(object : ValueSpacer(owner, name, 0.75F, manage = false) {
            override fun onClick(mouseButton: Int) {
                if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    this@SidebarEntry.onClick(mouseButton)
                    if (AccessibilityValues.playSoundWhenClickingInSidebar.value) ScreenUtil.playClickSound()
                } else if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    openValues()
                }
            }
        }.createValueComponent()!!)
    }
}

@Suppress("unused") // Packages use this
abstract class SidebarEntrySelection(name: String, category: String, val list: List<String>) : SidebarEntry(name, category) {
    val value = ValueMode(this, "Selection", false, *list.toTypedArray())

    override fun createElements(owner: Any): List<ElementWidthValueComponent<*>> {
        return list.map {
            object : ValueSpacer(owner, it, 0.75F, manage = false) {
                override fun onClick(mouseButton: Int) {
                    if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                        value.select(list.indexOf(it))
                        if (AccessibilityValues.playSoundWhenClickingInSidebar.value) ScreenUtil.playClickSound()
                    } else if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                        openValues()
                    }
                }

                override fun getColor(hovered: Boolean): Color {
                    if (value.isSelected(it)) {
                        return TarasandeValues.accentColor.getColor()
                    }
                    return super.getColor(hovered)
                }
            }.createValueComponent()!!
        }
    }
}

open class SidebarEntryToggleable(name: String, category: String) : SidebarEntry(name, category) {
    @Suppress("LeakingThis")
    val enabled = ValueBoolean(this, "Enabled", false, visible = false)

    override fun createElements(owner: Any): List<ElementWidthValueComponent<*>> {
        return listOf(object : ValueSpacer(owner, name, 0.75F, manage = false) {
            override fun onClick(mouseButton: Int) {
                if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    enabled.value = !enabled.value
                    this@SidebarEntryToggleable.onClick(mouseButton)
                    if (AccessibilityValues.playSoundWhenClickingInSidebar.value) ScreenUtil.playClickSound()
                } else if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    openValues()
                }
            }

            override fun getColor(hovered: Boolean) = (if (enabled.value) Color.green else Color.red).let { if (hovered) RenderUtil.colorInterpolate(it, TarasandeValues.accentColor.getColor(), 0.4) else it }
        }.createValueComponent()!!)
    }
}
