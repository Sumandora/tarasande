package su.mandora.tarasande.screen.menu.panel.impl.fixed.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.screen.menu.panel.Alignment
import su.mandora.tarasande.screen.menu.panel.impl.fixed.PanelFixed
import su.mandora.tarasande.util.render.RenderUtil

class PanelFixedInformation(x: Double, y: Double) : PanelFixed("Information", x, y, 75.0, resizable = false) {

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val text = ArrayList<String>()
        for (owner in TarasandeMain.get().screens?.screenMenu?.managerInformation?.getAllOwners()!!) {
            val cache = ArrayList<String>()
            for (information in TarasandeMain.get().screens?.screenMenu?.managerInformation?.getAllInformation(owner)!!) {
                val message = information.getMessage()
                if (message != null) {
                    if (message.contains("\n")) {
                        val parts = message.split("\n")
                        if (parts.isNotEmpty()) {
                            if (parts[0].isNotEmpty())
                                cache.add("[" + information.information + "] " + parts[0])
                            else
                                cache.add("[" + information.information + "]")
                            if (parts.size > 1) {
                                cache.addAll(parts.subList(1, parts.size))
                            }
                        }
                    } else {
                        cache.add("[" + information.information + "] $message")
                    }
                }
            }
            if (cache.isNotEmpty()) {
                text.add("[$owner]")
                text.addAll(cache)
            }
            text.add("")
        }

        for ((index, it) in text.withIndex()) {
            when (alignment) {
                Alignment.LEFT -> RenderUtil.drawWithSmallShadow(matrices, it, x.toFloat(), y.toFloat() + MinecraftClient.getInstance().textRenderer.fontHeight * (index + 1), TarasandeMain.get().clientValues?.accentColor?.getColor()?.rgb!!)
                Alignment.MIDDLE -> RenderUtil.drawWithSmallShadow(matrices, it, x.toFloat() + panelWidth.toFloat() / 2.0f - MinecraftClient.getInstance().textRenderer.getWidth(it).toFloat() / 2.0f, y.toFloat() + MinecraftClient.getInstance().textRenderer.fontHeight * (index + 1), TarasandeMain.get().clientValues?.accentColor?.getColor()?.rgb!!)
                Alignment.RIGHT -> RenderUtil.drawWithSmallShadow(matrices, it, x.toFloat() + panelWidth.toFloat() - MinecraftClient.getInstance().textRenderer.getWidth(it).toFloat(), y.toFloat() + MinecraftClient.getInstance().textRenderer.fontHeight * (index + 1), TarasandeMain.get().clientValues?.accentColor?.getColor()?.rgb!!)
            }
        }
    }

    override fun isVisible(): Boolean {
        for (information in TarasandeMain.get().screens?.screenMenu?.managerInformation?.list!!)
            if (information.isVisible())
                return true
        return false
    }

}