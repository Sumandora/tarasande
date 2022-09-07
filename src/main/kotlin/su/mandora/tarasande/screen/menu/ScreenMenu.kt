package su.mandora.tarasande.screen.menu

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.texture.MissingSprite
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
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
import su.mandora.tarasande.screen.menu.panel.impl.friends.PanelFriends
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

    private var image: Identifier? = Identifier(TarasandeMain.get().name, "textures/nanakusa.png")
    private val particles = ArrayList<Particle>()

    // unused rn
    var hoveringText: String? = null

    val managerValueComponent = ManagerValueComponent()
    val managerInformation = ManagerInformation()

    init {
        // @mojang, this code is garbage. delete life
        MinecraftClient.getInstance().textureManager.getTexture(image)
        val imageTex = MinecraftClient.getInstance().textureManager.getTexture(image)
        if (imageTex == null || imageTex == MissingSprite.getMissingSpriteTexture())
            image = null

        for (moduleCategory in ModuleCategory.values()) {
            panels.add(PanelCategory(moduleCategory, 0.0, 0.0))
        }
        val fixedPanels = mutableListOf(
            PanelClientValues::class.java,
            PanelFriends::class.java,
            PanelFixedArrayList::class.java,
            PanelFixedInformation::class.java,
            PanelFixedEffects::class.java,
            PanelFixedInventory::class.java,
            PanelFixedWatermark::class.java,
            PanelFixedHypixelBedwarsOverlay::class.java
        )
        if (System.getProperty("os.name").contains("linux", true)) {
            fixedPanels.add(PanelFixedNowPlaying::class.java)
        }
        for (panel in fixedPanels) {
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
        for (panel in panels) panel.init()

        particles.clear()
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        var animation = ((System.currentTimeMillis() - screenChangeTime) / openingAnimationLength).coerceAtMost(1.0)
        if (isClosing) animation = 1.0 - animation

        if (isClosing && animation <= 0.0) {
            panels.forEach { it.onClose() }
            RenderSystem.recordRenderCall {
                super.close()
            }
        }

        val color = TarasandeMain.get().clientValues?.accentColor?.getColor()!!

        val strength = round(animation * TarasandeMain.get().clientValues?.blurStrength?.value!!).toInt()
        if (strength > 0) {
            TarasandeMain.get().blur?.bind(true)
            RenderUtil.fill(matrices, 0.0, 0.0, client?.window?.scaledWidth?.toDouble()!!, client?.window?.scaledHeight?.toDouble()!!, Color.white.rgb)
            client?.framebuffer?.beginWrite(true)

            if (animation != 1.0) { // Prevent it from recalculating every frame
                TarasandeMain.get().blur?.blurScene(strength)
            } else {
                super.render(matrices, mouseX, mouseY, delta)
            }
        }

        matrices?.push()

        if (image != null) {
            matrices?.push()
            RenderSystem.setShader { GameRenderer.getPositionTexShader() }
            RenderSystem.setShaderTexture(0, image)
            val color = color.brighter().brighter()
            RenderSystem.setShaderColor(color.red / 255f, color.green / 255f, color.blue / 255f, (animation * animation * animation).toFloat())
            RenderSystem.enableBlend()
            RenderSystem.defaultBlendFunc()
            RenderSystem.enableDepthTest()
            val aspect = 1414.0 / 920.0
            val width = height / aspect
            val height = width / aspect
            DrawableHelper.drawTexture(matrices, (client?.window?.scaledWidth!! - animation * width).toInt(), (client?.window?.scaledHeight!! - height).toInt(), 0, 0.0f, 0.0f, width.toInt(), height.toInt(), width.toInt(), height.toInt())
            matrices?.pop()
        }

        matrices?.push()
        val numPoints = 100
        if (particles.size < numPoints)
            particles.add(Particle(width / 2.0, height / 2.0))
        matrices?.pop()

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)

        matrices?.push()
        DrawableHelper.fill(matrices, 0, 0, width, height, Color(color.red, color.green, color.blue, (animation * 255 * 0.66).toInt()).rgb)
        matrices?.pop()

        particles.forEach { it.render(matrices, mouseX.toDouble(), mouseY.toDouble(), animation) }

        panels.reversed().forEach {
            matrices?.push()
            val panelHeight = (if (it.opened) it.panelHeight else textRenderer?.fontHeight)?.toDouble()!!
            if (it !is PanelFixed || !(it.isVisible() && it.opened)) {
                matrices?.translate(it.x + it.panelWidth / 2.0, it.y + panelHeight / 2.0, 0.0)
                matrices?.scale(animation.toFloat(), animation.toFloat(), 1.0F)
                matrices?.translate(-(it.x + it.panelWidth / 2.0), -(it.y + panelHeight / 2.0), 0.0)
            }
            val x = it.x + it.panelWidth * (1 - animation) / 2.0
            val y = it.y + panelHeight - panelHeight * (1 - animation) / 2.0 - 1
            val width = it.panelWidth - it.panelWidth * (1 - animation)
            val height = (panelHeight - panelHeight * (1 - animation) - 1)
            if (it.opened && it !is PanelFixed && animation > 0.0) {
                GlStateManager._enableScissorTest()
                GlStateManager._scissorBox(round(x * client?.window?.scaleFactor!!).toInt(), round(client?.window?.height!! - y * client?.window?.scaleFactor!!).toInt(), round(width * client?.window?.scaleFactor!!).toInt().coerceAtLeast(1), round(height * client?.window?.scaleFactor!!).toInt().coerceAtLeast(1))
            }
            if (it is PanelFixed || animation > 0.0) {
                it.render(matrices, mouseX, mouseY, delta)
            }
            GlStateManager._disableScissorTest()
            matrices?.pop()
        }

        matrices?.pop()

        if (hoveringText != null) {
            matrices?.push()
            val strWidth = textRenderer.getWidth(hoveringText)
            matrices?.translate((mouseX + strWidth / 2).toDouble(), (mouseY + textRenderer.fontHeight / 2).toDouble(), 0.0)
            matrices?.scale(0.5f, 0.5f, 1.0f)
            matrices?.translate(-(mouseX + strWidth / 2).toDouble(), -(mouseY + textRenderer.fontHeight / 2).toDouble(), 0.0)
            textRenderer.drawWithShadow(matrices, hoveringText, mouseX.toFloat(), mouseY.toFloat(), Color.white.rgb)
            matrices?.pop()
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        var animation = ((System.currentTimeMillis() - screenChangeTime) / openingAnimationLength).coerceAtMost(1.0)
        if (isClosing) animation = 1.0 - animation

        if (animation != 1.0) return true

        for (it in panels) {
            if (it.mouseClicked(mouseX, mouseY, button)) {
                if (panels.contains(it)) { // in case of self removal
                    panels.remove(it)
                    panels.add(0, it)
                }
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
            if (it.mouseScrolled(mouseX, mouseY, amount)) break
        }
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        panels.forEach {
            if (it.keyPressed(keyCode, scanCode, modifiers)) return false
        }
        val animation = ((System.currentTimeMillis() - screenChangeTime) / openingAnimationLength).coerceAtMost(1.0)
        if (animation != 1.0) return false
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

    override fun close() {
        if (!isClosing) {
            screenChangeTime = System.currentTimeMillis()
            isClosing = true
        }
    }

    override fun shouldPause() = false
}
