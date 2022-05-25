package su.mandora.tarasande.screen.menu.panel.impl.friends

import com.mojang.authlib.GameProfile
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec2f
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.screen.menu.utils.IElement
import su.mandora.tarasande.util.render.RenderUtil
import java.awt.Color
import kotlin.math.min

class PlayerElement(val gameProfile: GameProfile, var width: Double) : IElement {

    private val defaultHeight = MinecraftClient.getInstance().textRenderer.fontHeight * 1.5 + 2.0

    private var friendTime = 0L

    override fun init() {
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        RenderUtil.fill(matrices, 0.0, 0.0, this.width, this.getHeight(), Int.MIN_VALUE)
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, gameProfile.name, 4.0f, (this.defaultHeight / 2.0f - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0f + 1.0f).toFloat(), Color.white.rgb)

        val toggleAnimation = min((System.currentTimeMillis() - friendTime) / 100.0, 1.0)
        val radius = if (TarasandeMain.get().friends?.isFriend(gameProfile)!!) toggleAnimation else 1.0 - toggleAnimation
        RenderUtil.fillCircle(matrices, width - 7, defaultHeight / 2, radius * 4.0, TarasandeMain.get().clientValues?.accentColor?.getColor()?.rgb!!)
        RenderUtil.outlinedCircle(matrices, width - 7, defaultHeight / 2, 4.0, 2.0f, RenderUtil.colorInterpolate(TarasandeMain.get().clientValues?.accentColor?.getColor()!!, Color.white, radius).rgb)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            if (RenderUtil.isHovered(mouseX, mouseY, 0.0, 0.0, width, getHeight())) {
                if (Vec2f(mouseX.toFloat(), mouseY.toFloat()).distanceSquared(Vec2f((width - 7).toFloat(), (defaultHeight / 2).toFloat())) < 16.0f) {
                    TarasandeMain.get().friends?.changeFriendState(gameProfile)!!
                    friendTime = System.currentTimeMillis()
                }
                return true
            }
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        return false
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return false
    }

    override fun charTyped(chr: Char, modifiers: Int) {
    }

    override fun tick() {
    }

    override fun onClose() {
    }

    override fun getHeight() = defaultHeight
}