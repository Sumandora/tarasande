package su.mandora.tarasande.screen.menu.panel.impl.fixed.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.screen.menu.panel.Alignment
import su.mandora.tarasande.screen.menu.panel.impl.fixed.PanelFixed
import su.mandora.tarasande.util.render.RenderUtil
import kotlin.math.min

class PanelFixedLog(x: Double, y: Double) : PanelFixed("Log", x, y, 150.0, 75.0, true, true, true) {

	private val animations = HashMap<Int, Double>()

	override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
		val lines = ArrayList<String>()

		for (line in TarasandeMain.get().logString.toString().replace("\r", "").split("\n")) {
			var line = line
			val parts = ArrayList<String>()
			while (MinecraftClient.getInstance().textRenderer.getWidth(line) > panelWidth) {
				var part = MinecraftClient.getInstance().textRenderer.trimToWidth(line, panelWidth.toInt())
				while (part.contains(" ") && part[part.length - 1] != ' ')
					part = part.substring(0, part.length - 1)
				parts.add(part)
				line = line.substring(part.length)
			}
			if (line.isNotEmpty())
				lines.add(line)
			lines.addAll(parts.reversed())
		}

		lines.reverse()

		animations.entries.removeIf { it.key > lines.size }

		for ((index, line) in lines.withIndex()) {
			animations[lines.size - index] = min(animations.getOrDefault(lines.size - index, 0.0) + 0.01 * (RenderUtil.deltaTime / 5f), 1.0)
			if (index * MinecraftClient.getInstance().textRenderer.fontHeight > panelHeight - MinecraftClient.getInstance().textRenderer.fontHeight)
				return
			val animation = animations[lines.size - index]!!
			val accent = TarasandeMain.get().clientValues?.accentColor?.getColor()!!
			when (alignment) {
				Alignment.LEFT -> RenderUtil.drawWithSmallShadow(matrices, line, (x - (MinecraftClient.getInstance().textRenderer.getWidth(line) * (1.0 - animation))).toFloat(), y.toFloat() + MinecraftClient.getInstance().textRenderer.fontHeight * (index + 1), accent.rgb)
				Alignment.MIDDLE -> RenderUtil.drawWithSmallShadow(matrices, line, x.toFloat() + panelWidth.toFloat() / 2.0f - MinecraftClient.getInstance().textRenderer.getWidth(line).toFloat() / 2.0f, y.toFloat() + MinecraftClient.getInstance().textRenderer.fontHeight * (index + (1 * animation).toFloat()), accent.rgb)
				Alignment.RIGHT -> RenderUtil.drawWithSmallShadow(matrices, line, (x + panelWidth - MinecraftClient.getInstance().textRenderer.getWidth(line) * animation).toFloat(), y.toFloat() + MinecraftClient.getInstance().textRenderer.fontHeight * (index + 1), accent.rgb)
			}
		}
	}

}