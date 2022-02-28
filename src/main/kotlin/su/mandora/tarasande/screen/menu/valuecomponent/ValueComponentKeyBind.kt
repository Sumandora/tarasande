package su.mandora.tarasande.screen.menu.valuecomponent

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.base.screen.menu.valuecomponent.ValueComponent
import su.mandora.tarasande.base.value.Value
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueKeyBind

class ValueComponentKeyBind(value: Value) : ValueComponent(value) {

    private var waitsForInput = false
    private var escapeCharacters = listOf("\t", "\b", "\n", "\r")

    override fun init() {
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        matrices?.push()
        matrices?.translate(0.0, getHeight() / 2.0, 0.0)
        matrices?.scale(0.5F, 0.5F, 1.0F)
        matrices?.translate(0.0, -getHeight() / 2.0, 0.0)
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, value.name, 0.0F, (getHeight() / 2.0F - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toFloat(), -1)
        matrices?.pop()

        val keyBind = (value as ValueKeyBind).keyBind
        val keyName = getKeyName(keyBind)
        val textWidth = MinecraftClient.getInstance().textRenderer.getWidth(keyName)

        RenderUtil.fill(matrices, width - textWidth / 2, getHeight() * 0.25, width, getHeight() * 0.75, Int.MIN_VALUE)
        matrices?.push()
        matrices?.translate(width, getHeight() / 2.0, 0.0)
        matrices?.scale(0.5F, 0.5F, 1.0F)
        matrices?.translate(-width, -getHeight() / 2.0, 0.0)
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, keyName, (width - textWidth).toFloat(), (getHeight() / 2.0F - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toFloat(), -1)
        matrices?.pop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val keyBind = (value as ValueKeyBind).keyBind
        val keyName = getKeyName(keyBind)
        val textWidth = MinecraftClient.getInstance().textRenderer.getWidth(keyName)

        waitsForInput = if (RenderUtil.isHovered(mouseX, mouseY, width - textWidth / 2, getHeight() * 0.25, width, getHeight() * 0.75)) {
            !waitsForInput
        } else {
            false
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double) = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (waitsForInput) {
            val valueKeyBind = value as ValueKeyBind
            if (!valueKeyBind.filter(if (keyCode == GLFW.GLFW_KEY_ESCAPE) GLFW.GLFW_KEY_UNKNOWN else keyCode)) {
                waitsForInput = false
                return true
            }
            if (keyCode == GLFW.GLFW_KEY_ESCAPE)
                valueKeyBind.keyBind = GLFW.GLFW_KEY_UNKNOWN
            else
                valueKeyBind.keyBind = keyCode
            valueKeyBind.onChange()
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

    private fun getKeyName(keyBind: Int): String {
        var keyName = GLFW.glfwGetKeyName(keyBind, 0)
        if (waitsForInput)
            keyName = "_"
        else if (keyBind == GLFW.GLFW_KEY_UNKNOWN)
            keyName = "none"
        else if (keyName == null)
            keyName = GLFW.glfwGetKeyName(GLFW.GLFW_KEY_UNKNOWN, GLFW.glfwGetKeyScancode(keyBind))

        if (keyName == null || keyName.trim().isEmpty() || escapeCharacters.contains(keyName)) {
            for (field in GLFW::class.java.declaredFields) {
                if (field.name.startsWith("GLFW_KEY_")) {
                    val content = field.get(GLFW::class.java)
                    if (content == keyBind) {
                        keyName = field.name.substring("GLFW_KEY_".length).replace("_", " ").lowercase()
                    }
                }
            }
        }

        if (keyName == null || keyName.isEmpty()) {
            keyName = "Key#$keyBind"
        }
        return keyName
    }
}