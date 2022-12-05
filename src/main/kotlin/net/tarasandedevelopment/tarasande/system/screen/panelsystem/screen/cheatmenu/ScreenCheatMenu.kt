package net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.cheatmenu

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventChangeScreen
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBind
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.ManagerPanel
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.cheatmenu.particle.Particle
import net.tarasandedevelopment.tarasande.util.extension.withAlpha
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import org.lwjgl.glfw.GLFW
import su.mandora.event.EventDispatcher
import kotlin.math.floor
import kotlin.math.round

class ScreenCheatMenu(private val panelSystem: ManagerPanel) : Screen(Text.of("Cheat Menu")) {

    // Values
    private val hotkey = object : ValueBind(this, "Hotkey", Type.KEY, GLFW.GLFW_KEY_RIGHT_SHIFT) {
        override fun filter(type: Type, bind: Int) = bind != GLFW.GLFW_KEY_UNKNOWN
    }
    private val animationLength = ValueNumber(this, "Animation length", 0.0, 100.0, 500.0, 1.0)
    private val accentBackground = ValueBoolean(this, "Accent background", true)
    private val blurredBackground = ValueBoolean(this, "Blurred background", true)
    private val imageValue = object : ValueMode(this, "Image", false, "Off", "Rimuru", "Shuya's girl", "Nanakusa", "Jannick", "Azusa") {
        override fun onChange() {
            image = null
        }
    }

    private var screenChangeTime = System.currentTimeMillis()
    private var isClosing = false

    var image: NativeImageBackedTexture? = null
    private val particles = ArrayList<Particle>()

    var disableAnimation = false

    init {
        passEvents = false
        EventDispatcher.apply {
            add(EventChangeScreen::class.java) { event ->
                if (client?.currentScreen is ScreenCheatMenu && event.newScreen == null)
                    panelSystem.list.forEach { it.onClose() }
            }
            add(EventUpdate::class.java) { event ->
                if (event.state == EventUpdate.State.PRE)
                    if (hotkey.wasPressed().let { it > 0 && it % 2 != 0 })
                        MinecraftClient.getInstance().setScreen(this@ScreenCheatMenu)
            }
        }
    }

    override fun init() {
        if (disableAnimation) {
            disableAnimation = false
            return
        }
        screenChangeTime = System.currentTimeMillis()
        isClosing = false
        super.init()
        for (panel in panelSystem.list) panel.init()

        particles.clear()
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        var animation = ((System.currentTimeMillis() - screenChangeTime) / animationLength.value).coerceAtMost(1.0)
        if (isClosing) animation = 1.0 - animation

        if (isClosing && animation <= 0.0) {
            RenderSystem.recordRenderCall {
                super.close()
            }
        }

        val color = TarasandeMain.clientValues().accentColor.getColor()

        val strength = round(animation * TarasandeMain.managerBlur().strength.value).toInt()
        if (strength > 0 && blurredBackground.value) {
            TarasandeMain.managerBlur().bind(true)
            RenderUtil.fill(matrices, 0.0, 0.0, client?.window?.scaledWidth?.toDouble()!!, client?.window?.scaledHeight?.toDouble()!!, -1)
            client?.framebuffer?.beginWrite(true)

            if (animation != 1.0) { // Prevent it from recalculating every frame
                TarasandeMain.managerBlur().blurScene(strength)
            } else {
                super.render(matrices, mouseX, mouseY, delta)
            }
        }

        matrices?.push()

        if (!imageValue.isSelected(0)) {
            if (image == null)
                image = RenderUtil.createImage(imageValue.selected[0].lowercase().replace(" ", "").replace("'", "") + ".png")

            matrices?.push()
            RenderSystem.setShader { GameRenderer.getPositionTexShader() }
            RenderSystem.setShaderTexture(0, image!!.glId)
            @Suppress("NAME_SHADOWING")
            val color = color.brighter().brighter()
            RenderSystem.setShaderColor(color.red / 255f, color.green / 255f, color.blue / 255f, (animation * animation * animation).toFloat())
            RenderSystem.enableBlend()
            RenderSystem.defaultBlendFunc()
            RenderSystem.enableDepthTest()

            val aspect = image!!.image!!.width / image!!.image!!.height.toDouble()

            val height = client?.window?.scaledHeight!! * 0.85
            val width = height * aspect

            DrawableHelper.drawTexture(matrices, (client?.window?.scaledWidth!! - animation * width).toInt(), (client?.window?.scaledHeight!! - height).toInt(), 0, 0.0F, 0.0F, width.toInt(), height.toInt(), width.toInt(), height.toInt())
            matrices?.pop()
        }

        matrices?.push()
        val numPoints = 100
        if (particles.size < numPoints)
            particles.add(Particle(width / 2.0, height / 2.0))
        matrices?.pop()

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F)

        if (accentBackground.value) {
            matrices?.push()
            DrawableHelper.fill(matrices, 0, 0, width, height, color.withAlpha((animation * 255 * 0.66).toInt()).rgb)
            matrices?.pop()
        }

        particles.forEach { it.render(matrices, mouseX.toDouble(), mouseY.toDouble(), animation) }

        panelSystem.list.reversed().forEach {
            matrices?.push()
            val panelHeight = (if (it.opened) it.panelHeight else textRenderer?.fontHeight)?.toDouble()!!
            if (!it.fixed || !(it.isVisible() && it.opened)) {
                matrices?.translate(it.x + it.panelWidth / 2.0, it.y + panelHeight / 2.0, 0.0)
                matrices?.scale(animation.toFloat(), animation.toFloat(), 1.0F)
                matrices?.translate(-(it.x + it.panelWidth / 2.0), -(it.y + panelHeight / 2.0), 0.0)
            }
            val x = it.x + it.panelWidth * (1 - animation) / 2.0
            val y = it.y + panelHeight - panelHeight * (1 - animation) / 2.0 - 1
            val width = it.panelWidth - it.panelWidth * (1 - animation)
            val height = (panelHeight - panelHeight * (1 - animation) - 1)
            if (it.opened && !it.fixed && animation > 0.0) {
                GlStateManager._enableScissorTest()
                GlStateManager._scissorBox(round(x * client?.window?.scaleFactor!!).toInt(), round(client?.window?.height!! - y * client?.window?.scaleFactor!!).toInt(), round(width * client?.window?.scaleFactor!!).toInt().coerceAtLeast(1), round(height * client?.window?.scaleFactor!!).toInt().coerceAtLeast(1))
            }
            if (it.fixed || animation > 0.0) {
                it.render(matrices, mouseX, mouseY, delta)
            }
            GlStateManager._disableScissorTest()
            matrices?.pop()
        }

        matrices?.pop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        var animation = ((System.currentTimeMillis() - screenChangeTime) / animationLength.value).coerceAtMost(1.0)
        if (isClosing) animation = 1.0 - animation

        if (animation != 1.0) return true

        for (it in panelSystem.list) {
            if (it.mouseClicked(floor(mouseX), floor(mouseY), button)) {
                panelSystem.reorderPanels(it, 0) // The panel was clicked, we should give it priority
                break
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        panelSystem.list.forEach { it.mouseReleased(mouseX, mouseY, button) }
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        for (it in panelSystem.list) {
            if (it.mouseScrolled(mouseX, mouseY, amount)) break
        }
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        panelSystem.list.forEach {
            if (it.keyPressed(keyCode, scanCode, modifiers)) return false
        }
        val animation = ((System.currentTimeMillis() - screenChangeTime) / animationLength.value).coerceAtMost(1.0)
        if (animation != 1.0) return false
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        panelSystem.list.forEach { it.charTyped(chr, modifiers) }
        return super.charTyped(chr, modifiers)
    }

    override fun tick() {
        panelSystem.list.forEach { it.tick() }
        super.tick()
    }

    override fun close() {
        if (disableAnimation) {
            return
        }
        if (!isClosing) {
            screenChangeTime = System.currentTimeMillis()
            isClosing = true
        }
    }

    override fun shouldPause() = false
}
