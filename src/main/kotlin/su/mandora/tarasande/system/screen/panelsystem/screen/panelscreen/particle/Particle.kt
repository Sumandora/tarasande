package su.mandora.tarasande.system.screen.panelsystem.screen.panelscreen.particle

import net.minecraft.client.gui.DrawContext
import net.minecraft.util.math.Vec2f
import su.mandora.tarasande.mc
import su.mandora.tarasande.util.extension.javaruntime.withAlpha
import su.mandora.tarasande.util.math.MathUtil
import su.mandora.tarasande.util.render.RenderUtil
import java.awt.Color
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.min
import kotlin.math.sqrt

class Particle(private var x: Double, private var y: Double) {

    private var xMotion: Double = 0.0
    private var yMotion: Double = 0.0

    private var point = Vec2f(ThreadLocalRandom.current().nextDouble(mc.window.scaledWidth.toDouble()).toFloat(), ThreadLocalRandom.current().nextDouble(mc.window.scaledHeight.toDouble()).toFloat())

    fun render(context: DrawContext, mouseX: Double, mouseY: Double, animation: Double) {
        val position = Vec2f(x.toFloat(), y.toFloat())
        val dist = (1.0 / sqrt(Vec2f(mouseX.toFloat(), mouseY.toFloat()).distanceSquared(position)) * 16.0).coerceAtMost(2.0)

        var newMotionX = (x - mouseX) * dist
        var newMotionY = (y - mouseY) * dist

        // get to random point
        newMotionX += (point.x - x) * 0.7 * animation
        newMotionY += (point.y - y) * 0.7 * animation

        xMotion += newMotionX
        yMotion += newMotionY

        val deltaTime = min(RenderUtil.deltaTime / 144F, 1.0)

        x += xMotion * deltaTime
        y += yMotion * deltaTime

        xMotion *= 0.98 * deltaTime
        yMotion *= 0.98 * deltaTime

        RenderUtil.fillCircle(context.matrices, x, y, MathUtil.getBias(animation, 0.95), Color.white.withAlpha((animation * 255).toInt()).rgb)
    }

}