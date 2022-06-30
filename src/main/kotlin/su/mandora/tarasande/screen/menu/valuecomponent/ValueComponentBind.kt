package su.mandora.tarasande.screen.menu.valuecomponent

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.base.screen.menu.valuecomponent.ValueComponent
import su.mandora.tarasande.base.value.Value
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueBind
import java.awt.Color

class ValueComponentBind(value: Value) : ValueComponent(value) {

    private var waitsForInput = false
    private var escapeCharacters = listOf("\t", "\b", "\n", "\r")

    override fun init() {
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val valueBind = value as ValueBind

        matrices?.push()
        matrices?.translate(0.0, getHeight() / 2.0, 0.0)
        matrices?.scale(0.5F, 0.5F, 1.0F)
        matrices?.translate(0.0, -getHeight() / 2.0, 0.0)
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, value.name, 0.0F, (getHeight() / 2.0F - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toFloat(), Color.white.let { if (valueBind.isEnabled()) it else it.darker().darker() }.rgb)
        matrices?.pop()

        val name = getName(valueBind.type, valueBind.button)
        val textWidth = MinecraftClient.getInstance().textRenderer.getWidth(name)

        RenderUtil.fill(matrices, width - textWidth / 2, getHeight() * 0.25, width, getHeight() * 0.75, Int.MIN_VALUE)
        matrices?.push()
        matrices?.translate(width, getHeight() / 2.0, 0.0)
        matrices?.scale(0.5F, 0.5F, 1.0F)
        matrices?.translate(-width, -getHeight() / 2.0, 0.0)
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, name, (width - textWidth).toFloat(), (getHeight() / 2.0F - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toFloat(), Color.white.let { if (valueBind.isEnabled()) it else it.darker().darker() }.rgb)
        matrices?.pop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val valueBind = value as ValueBind

        val name = getName(valueBind.type, valueBind.button)
        val textWidth = MinecraftClient.getInstance().textRenderer.getWidth(name)

        if (button == 0 && RenderUtil.isHovered(mouseX, mouseY, width - textWidth / 2, getHeight() * 0.25, width, getHeight() * 0.75)) {
            waitsForInput = !waitsForInput
            return true
        } else {
            if (waitsForInput && valueBind.mouse) {
                valueBind.type = ValueBind.Type.MOUSE
                valueBind.button = button
                valueBind.onChange()
                waitsForInput = false
                return true
            }
            waitsForInput = false
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double) = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (waitsForInput) {
            val valueBind = value as ValueBind
            val key = if (keyCode == GLFW.GLFW_KEY_ESCAPE) GLFW.GLFW_KEY_UNKNOWN else keyCode
            if (!valueBind.filter(key)) {
                waitsForInput = false
                return true
            }
            valueBind.type = ValueBind.Type.KEY
            valueBind.button = key
            valueBind.onChange()
            waitsForInput = false
            return true
        }
        return false
    }

    override fun charTyped(chr: Char, modifiers: Int) {
    }

    override fun tick() {
    }

    override fun onClose() {
        waitsForInput = false
    }

    override fun getHeight() = MinecraftClient.getInstance().textRenderer.fontHeight.toDouble()

    private fun getName(type: ValueBind.Type, button: Int): String {
        if (waitsForInput) return "_"
        when (type) {
            ValueBind.Type.KEY -> {
                var keyName: String?
                if (button == GLFW.GLFW_KEY_UNKNOWN) keyName = "none"
                else {
                    keyName = GLFW.glfwGetKeyName(button, -1)
                    if (keyName == null) keyName = GLFW.glfwGetKeyName(GLFW.GLFW_KEY_UNKNOWN, GLFW.glfwGetKeyScancode(button))
                }

                if (keyName == null || keyName.trim().isEmpty() || escapeCharacters.contains(keyName)) {
                    for (field in GLFW::class.java.declaredFields) {
                        if (field.name.startsWith("GLFW_KEY_")) {
                            val content = field.get(GLFW::class.java)
                            if (content == button) {
                                keyName = field.name.substring("GLFW_KEY_".length).replace("_", " ").lowercase()
                            }
                        }
                    }
                }

                if (keyName == null || keyName.isEmpty()) {
                    keyName = "Key#$button"
                }
                return keyName
            }
            ValueBind.Type.MOUSE -> return "Mouse#$button"
            else -> return "Invalid type"
        }
    }
}