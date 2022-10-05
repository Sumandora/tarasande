package net.tarasandedevelopment.tarasande.screen

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

open class ScreenBetter(internal var prevScreen: Screen?) : Screen(Text.of("")) {

    override fun close() {
        client!!.setScreen(prevScreen)
    }

    fun halfWidth(): Int {
        return this.width / 2
    }

    fun halfHeight(): Int {
        return this.height / 2
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
    }
}
