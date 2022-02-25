package su.mandora.tarasande.screen.menu.particle

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec2f
import su.mandora.tarasande.util.math.MathUtil
import su.mandora.tarasande.util.render.RenderUtil
import java.awt.Color
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.min
import kotlin.math.sqrt

class Particle(private var x: Double, private var y: Double) {

	private var xMotion: Double = 0.0
	private var yMotion: Double = 0.0

	private var point = Vec2f(
		ThreadLocalRandom.current().nextDouble(MinecraftClient.getInstance().window?.scaledWidth?.toDouble()!!).toFloat(),
		ThreadLocalRandom.current().nextDouble(MinecraftClient.getInstance().window?.scaledHeight?.toDouble()!!).toFloat()
	)

	fun render(matrices: MatrixStack?, mouseX: Double, mouseY: Double, animation: Double) {
		val position = Vec2f(x.toFloat(), y.toFloat())
		val dist = (1.0 / sqrt(Vec2f(mouseX.toFloat(), mouseY.toFloat()).distanceSquared(position)) * 16.0).coerceAtMost(2.0)

		var newMotionX = (x - mouseX) * dist
		var newMotionY = (y - mouseY) * dist

		// get to random point
		newMotionX += (point.x - x) * 0.7 * animation
		newMotionY += (point.y - y) * 0.7 * animation

		xMotion += newMotionX
		yMotion += newMotionY

		val deltaTime = min(RenderUtil.deltaTime / 144f, 1.0)

		x += xMotion * deltaTime
		y += yMotion * deltaTime

		xMotion *= 0.98 * deltaTime
		yMotion *= 0.98 * deltaTime

		RenderUtil.fillCircle(matrices, x, y, MathUtil.getBias(animation, 0.95), Color(255, 255, 255, (animation * 255).toInt()).rgb)
	}

}