package de.florianmichael.tarasande_custom_minecraft.screenextension

import de.florianmichael.tarasande_custom_minecraft.tarasandevalues.debug.ConnectionState
import de.florianmichael.tarasande_custom_minecraft.tarasandevalues.debug.DetailedConnectionStatus
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.ConnectScreen
import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueSpacer
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.PanelElements
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtension
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper

class ScreenExtensionConnectScreen : ScreenExtension<ConnectScreen>(ConnectScreen::class.java) {

    override fun createElements(screen: ConnectScreen): MutableList<Element> {
        if (DetailedConnectionStatus.showDetailedConnectionStatus.value && DetailedConnectionStatus.showDetailedConnectionStatus.isEnabled()) {
            return mutableListOf(ClickableWidgetPanel(object : PanelElements<ElementWidthValueComponent<*>>("Connection status", 200.0, ConnectionState.values().size * (FontWrapper.fontHeight().toDouble())) {
                private fun addText(input: String) = elementList.add(ValueSpacer(this, input, scale = 0.75F, manage = false).createValueComponent())

                override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
                    elementList.clear()

                    x = screen.width / 2F - panelWidth / 2F
                    y = (screen.height / 4 + 120 + 12) - panelHeight - FontWrapper.fontHeight()

                    for (string in DetailedConnectionStatus.connectionHistory) addText(string)

                    super.render(matrices, mouseX, mouseY, delta)
                }
            }))
        }
        return mutableListOf()
    }
}
