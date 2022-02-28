package su.mandora.tarasande.screen.menu.valuecomponent

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.screen.menu.valuecomponent.ValueComponent
import su.mandora.tarasande.base.value.Value
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueBoolean
import java.awt.Color
import kotlin.math.min

class ValueComponentBoolean(value: Value) : ValueComponent(value) {

    private var toggleTime = 0L

    override fun init() {
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        matrices?.push()
        matrices?.translate(0.0, getHeight() / 2.0, 0.0)
        matrices?.scale(0.5F, 0.5F, 1.0F)
        matrices?.translate(0.0, -getHeight() / 2.0, 0.0)
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, value.name, 0.0F, (getHeight() / 2.0F - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toFloat(), -1)
        matrices?.pop()

        val expandedAnimation = min((System.currentTimeMillis() - toggleTime) / 100.0 /* length in ms */, 1.0)
        val fade = (if ((value as ValueBoolean).value) expandedAnimation else 1.0 - expandedAnimation)
        RenderUtil.fill(matrices, width - 2 - 2 * fade, getHeight() / 2 - 2 * fade, width - 2 + 2 * fade, getHeight() / 2 + 2 * fade, RenderUtil.colorInterpolate(Color.white, TarasandeMain.get().clientValues?.accentColor?.getColor()!!, fade).rgb)
        RenderUtil.outlinedFill(matrices, width - 4, getHeight() / 2 - 2, width, getHeight() / 2 + 2, 2.0F, RenderUtil.colorInterpolate(TarasandeMain.get().clientValues?.accentColor?.getColor()!!, Color.white, fade).rgb)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (RenderUtil.isHovered(mouseX, mouseY, width - 4, getHeight() / 2 - 2, width, getHeight() / 2 + 2)) {
            val valueBoolean = value as ValueBoolean
            valueBoolean.value = !valueBoolean.value
            valueBoolean.onChange()
            toggleTime = System.currentTimeMillis()
            return true
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double) = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) = false

    override fun charTyped(chr: Char, modifiers: Int) {
    }

    override fun tick() {
    }

    override fun onClose() {
    }

    override fun getHeight() = MinecraftClient.getInstance().textRenderer.fontHeight.toDouble()
}