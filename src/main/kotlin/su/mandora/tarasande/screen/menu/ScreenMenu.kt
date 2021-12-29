package su.mandora.tarasande.screen.menu

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import org.lwjgl.opengl.GL13
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.base.screen.menu.graph.ManagerGraph
import su.mandora.tarasande.base.screen.menu.information.ManagerInformation
import su.mandora.tarasande.base.screen.menu.valuecomponent.ManagerValueComponent
import su.mandora.tarasande.screen.menu.panel.Panel
import su.mandora.tarasande.screen.menu.panel.impl.PanelClientValues
import su.mandora.tarasande.screen.menu.panel.impl.category.PanelCategory
import su.mandora.tarasande.screen.menu.panel.impl.fixed.PanelFixed
import su.mandora.tarasande.screen.menu.panel.impl.fixed.impl.*
import su.mandora.tarasande.screen.menu.particle.Particle
import su.mandora.tarasande.util.render.RenderUtil
import java.awt.Color
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.round

class ScreenMenu : Screen(Text.of("Menu")) {

	private val openingAnimationLength = 250.0

	val panels = CopyOnWriteArrayList<Panel>()
	private var screenChangeTime = System.currentTimeMillis()
	private var isClosing = false

	private val managerGraph = ManagerGraph()

	private val particles = ArrayList<Particle>()

	val managerValueComponent = ManagerValueComponent()
	val managerInformation = ManagerInformation()

	init {
		for (moduleCategory in ModuleCategory.values()) {
			panels.add(PanelCategory(moduleCategory, 0.0, 0.0))
		}
		for (panel in listOf(
			PanelClientValues::class.java,
			PanelFixedArrayList::class.java,
			PanelFixedInformation::class.java,
			PanelFixedRadar::class.java,
			PanelFixedEffects::class.java,
			PanelFixedInventory::class.java,
			PanelFixedLog::class.java
		)) {
			panels.add(panel.declaredConstructors[0].newInstance(0.0, 0.0) as Panel)
		}
		for (graph in managerGraph.list) {
			panels.add(PanelFixedGraph(graph, 0.0, 0.0))
		}
	}

	override fun init() {
		screenChangeTime = System.currentTimeMillis()
		isClosing = false
		super.init()
		for (panel in panels)
			panel.init()

		particles.clear()
	}

	override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
		var animation = ((System.currentTimeMillis() - screenChangeTime) / openingAnimationLength).coerceAtMost(1.0)
		if (isClosing)
			animation = 1.0 - animation

		val color = TarasandeMain.get().clientValues?.accentColor?.getColor()!!

		val strength = round(animation * TarasandeMain.get().clientValues?.blurStrength?.value!!).toInt()
		if(strength > 0) {
			TarasandeMain.get().blur?.bind(true)
			RenderUtil.fill(matrices, 0.0, 0.0, client?.window?.scaledWidth!!.toDouble(), client?.window?.scaledHeight!!.toDouble(), -1)
			client?.framebuffer?.beginWrite(true)

			if(animation != 1.0) { // Prevent it from recalculating every frame
				TarasandeMain.get().blur?.blurScene(strength)
			} else {
				super.render(matrices, mouseX, mouseY, delta)
			}
		}

		matrices?.push()

		val numPoints = 100
		for (i in 0..numPoints) {
			if (particles.size == i && animation * numPoints >= i)
				particles.add(Particle(width / 2.0, height / 2.0))
		}

		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)

		matrices?.push()
		DrawableHelper.fill(matrices, 0, 0, width, height, Color(color.red, color.green, color.blue, (animation * 255 * 0.66).toInt()).rgb)
		matrices?.pop()

		particles.forEach { it.render(matrices, mouseX.toDouble(), mouseY.toDouble(), animation) }

		panels.reversed().forEach {
			matrices?.push()
			if (it !is PanelFixed || !(it.opened && it.isVisible())) {
				matrices?.translate(it.x + it.panelWidth / 2.0, it.y + it.panelHeight / 2.0, 0.0)
				matrices?.scale(animation.toFloat(), animation.toFloat(), 1.0F)
				matrices?.translate(-(it.x + it.panelWidth / 2.0), -(it.y + it.panelHeight / 2.0), 0.0)
			}

			if (it.scissor) {
				GlStateManager._enableScissorTest()
				val animation = if (it !is PanelFixed || !(it.opened && it.isVisible())) animation else 1.0
				GlStateManager._scissorBox(
					((it.x + it.panelWidth * (1 - animation) / 2) * client?.window?.scaleFactor!!).toInt(),
					(client?.window?.height!! - (it.y + it.panelHeight - it.panelHeight * (1 - animation) / 2) * client?.window?.scaleFactor!!).toInt(),
					((it.panelWidth - it.panelWidth * (1 - animation)) * client?.window?.scaleFactor!!).toInt(),
					((it.panelHeight - it.panelHeight * (1 - animation)) * client?.window?.scaleFactor!!).toInt()
				)
			}
			it.render(matrices, mouseX, mouseY, delta)
			if (it.scissor) {
				GlStateManager._disableScissorTest()
			}
			matrices?.pop()
		}

		if (isClosing && animation <= 0.0) {
			panels.forEach { it.onClose() }
			RenderSystem.recordRenderCall {
				super.onClose()
			}
		}

		matrices?.pop()
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		var animation = ((System.currentTimeMillis() - screenChangeTime) / openingAnimationLength).coerceAtMost(1.0)
		if (isClosing)
			animation = 1.0 - animation

		if(animation != 1.0) return true

		for (it in panels) {
			if (it.mouseClicked(mouseX, mouseY, button)) {
				panels.remove(it)
				panels.add(0, it)
				break
			}
		}
		return super.mouseClicked(mouseX, mouseY, button)
	}

	override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
		panels.forEach { it.mouseReleased(mouseX, mouseY, button) }
		return super.mouseReleased(mouseX, mouseY, button)
	}

	override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
		for (it in panels) {
			if (it.mouseScrolled(mouseX, mouseY, amount))
				break
		}
		return super.mouseScrolled(mouseX, mouseY, amount)
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		panels.forEach {
			if (it.keyPressed(keyCode, scanCode, modifiers))
				return false
		}
		val animation = ((System.currentTimeMillis() - screenChangeTime) / openingAnimationLength).coerceAtMost(1.0)
		if(animation != 1.0)
			return false
		return super.keyPressed(keyCode, scanCode, modifiers)
	}

	override fun charTyped(chr: Char, modifiers: Int): Boolean {
		panels.forEach { it.charTyped(chr, modifiers) }
		return super.charTyped(chr, modifiers)
	}

	override fun tick() {
		panels.forEach { it.tick() }
		super.tick()
	}

	override fun onClose() {
		if (!isClosing) {
			screenChangeTime = System.currentTimeMillis()
			isClosing = true
		}
	}

	override fun isPauseScreen(): Boolean {
		return false
	}
}