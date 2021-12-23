package su.mandora.tarasande.util.render.screen

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

open class ScreenBetter(private val prevScreen: Screen?) : Screen(Text.of("")) {

	override fun onClose() {
		client?.setScreen(prevScreen)
	}

	override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
		renderBackground(matrices)
		super.render(matrices, mouseX, mouseY, delta)
	}
}