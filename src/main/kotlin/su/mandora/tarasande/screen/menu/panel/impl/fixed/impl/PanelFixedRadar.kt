package su.mandora.tarasande.screen.menu.panel.impl.fixed.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.screen.menu.panel.impl.fixed.PanelFixed
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.render.RenderUtil
import kotlin.math.*

class PanelFixedRadar(x: Double, y: Double) : PanelFixed("Radar", x, y, 100.0, 100.0, background = true, scissor = true) {

	override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
		if (MinecraftClient.getInstance().player == null)
			return

		for (entity in MinecraftClient.getInstance().world?.entities!!) {
			val dist = sqrt((entity.x - MinecraftClient.getInstance().player?.x!!).pow(2.0) + (entity.z - MinecraftClient.getInstance().player?.z!!).pow(2.0))

			if (dist > (panelWidth + panelHeight) / 2)
				continue

			val yawDelta = RotationUtil.getYaw(MinecraftClient.getInstance().player?.x!!, MinecraftClient.getInstance().player?.z!!, entity.x, entity.z) - MathHelper.wrapDegrees(MinecraftClient.getInstance().player?.yaw!!) + 180

			val x = -sin(yawDelta / 360.0 * PI * 2) * dist
			val y = cos(yawDelta / 360.0 * PI * 2) * dist

			RenderUtil.fillCircle(matrices, this.x + panelWidth / 2 + x, this.y + panelHeight / 2 + y, 2.0, TarasandeMain.get().entityColor?.getColor(entity)!!.rgb)
		}
	}

}