package net.tarasandedevelopment.tarasande.screen.base

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

open class ScreenBetter(var prevScreen: Screen?) : Screen(Text.of("")) {

    override fun close() {
        client!!.setScreen(prevScreen)
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
    }
}
