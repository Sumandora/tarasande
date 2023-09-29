package su.mandora.tarasande.system.screen.panelsystem.screen.panelscreen

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.TARASANDE_NAME
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventChangeScreen
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBind
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.screen.blursystem.ManagerBlur
import su.mandora.tarasande.system.screen.panelsystem.ManagerPanel
import su.mandora.tarasande.system.screen.panelsystem.api.PanelFixed
import su.mandora.tarasande.system.screen.panelsystem.screen.panelscreen.particle.Particle
import su.mandora.tarasande.util.extension.javaruntime.withAlpha
import su.mandora.tarasande.util.render.animation.TimeAnimator
import kotlin.math.floor
import kotlin.math.round

class ScreenPanel(private val panelSystem: ManagerPanel) : Screen(Text.of("Panel screen")) {

    // Values
    private val hotkey = object : ValueBind(this, "Hotkey", Type.KEY, GLFW.GLFW_KEY_RIGHT_SHIFT) {
        override fun filter(type: Type, bind: Int) = bind != GLFW.GLFW_KEY_UNKNOWN
    }
    private val animationLength = object : ValueNumber(this, "Animation length", 0.0, 100.0, 500.0, 1.0) {
        override fun onChange(oldValue: Double?, newValue: Double) {
            animation = TimeAnimator(newValue.toLong()).also {
                it.setReversed(animation.reversed)
                it.setProgress(animation.getProgress())
            }
        }
    }
    private val ichHabEinfachDenBESTEN = ValueBoolean(this, "Ich hab einfach den BESTEN!", false)
    private val screenBackgroundOpacity = ValueNumber(this, "Screen background opacity", 0.0, 0.66, 1.0, 0.01)
    val panelBackgroundOpacity = ValueNumber(this, "Panel background opacity", 0.0, 0.3, 1.0, 0.01)

    private val imageIdentifier = Identifier(TARASANDE_NAME, "textures/jannick.png")
    private val particles = ArrayList<Particle>()

    private var wasClosed = true

    var animation: TimeAnimator = TimeAnimator(animationLength.value.toLong())

    init {
        animation.reversed = true // Play it off like we just closed the ui

        EventDispatcher.apply {
            add(EventChangeScreen::class.java) { event ->
                if (client?.currentScreen is ScreenPanel && event.newScreen == null) {
                    panelSystem.list.forEach { it.onClose() }
                    wasClosed = true // Sad, but true. This cancels our smooth animation, but we can't afford to leave a screen open :c
                }
            }
            add(EventUpdate::class.java) { event ->
                if (event.state == EventUpdate.State.PRE)
                    if (hotkey.wasPressed() > 0)
                        mc.setScreen(this@ScreenPanel)
            }
        }
    }

    override fun init() {
        if (!wasClosed) {
            return
        }
        wasClosed = false
        if(animation.reversed)
            animation.setReversed(false)
        super.init()
        for (panel in panelSystem.list) panel.init()

        particles.clear()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if(animation.reversed) {
            if(hotkey.isPressed(true))
                animation.setReversed(false)
            if (animation.isCompleted())
                RenderSystem.recordRenderCall { super.close() }
        }

        val progress = animation.getProgress()

        val color = TarasandeValues.accentColor.getColor()

        val strength = round(progress * ManagerBlur.blurEffect.strength.value).toInt()
        if (strength > 0) {
            ManagerBlur.bind(true)
            context.fill(
                0,
                0,
                client!!.window.scaledWidth,
                client!!.window.scaledHeight,
                -1
            )
            client?.framebuffer?.beginWrite(true)

            ManagerBlur.blurScene(context.matrices, strength)
        }

        context.matrices.push()

        if (ichHabEinfachDenBESTEN.value) {
            context.matrices.push()
            val aspect = 581.0 / 418.0

            val height = client?.window?.scaledHeight!! * 0.85
            val width = height * aspect

            context.drawTexture(
                imageIdentifier,
                (client?.window?.scaledWidth!! - progress * width).toInt(),
                (client?.window?.scaledHeight!! - height).toInt(),
                0,
                0F,
                0F,
                width.toInt(),
                height.toInt(),
                width.toInt(),
                height.toInt()
            )
            context.matrices.pop()
        }

        context.matrices.push()
        val numPoints = 100
        if (particles.size < numPoints)
            particles.add(Particle(width / 2.0, height / 2.0))
        context.matrices.pop()

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F)

        context.matrices.push()
        context.fill(0, 0, width, height, color.withAlpha((progress * 255 * screenBackgroundOpacity.value).toInt()).rgb)
        context.matrices.pop()

        particles.forEach { it.render(context, mouseX.toDouble(), mouseY.toDouble(), progress) }

        panelSystem.list.reversed().forEach {
            context.matrices.push()
            val panelHeight = it.effectivePanelHeight()
            if (it !is PanelFixed || !(it.isVisible() && it.opened)) {
                context.matrices.translate(it.x + it.panelWidth / 2.0, it.y + panelHeight / 2.0, 0.0)
                context.matrices.scale(progress.toFloat(), progress.toFloat(), 1F)
                context.matrices.translate(-(it.x + it.panelWidth / 2.0), -(it.y + panelHeight / 2.0), 0.0)
            }
            val x = it.x + it.panelWidth * (1 - progress) / 2.0
            val y = it.y + panelHeight - panelHeight * (1 - progress) / 2.0 - 1
            val width = it.panelWidth - it.panelWidth * (1 - progress)
            val height = (panelHeight - panelHeight * (1 - progress) - 1)
            val scissor = it.opened && it !is PanelFixed && progress > 0.0
            if (it is PanelFixed || progress > 0.0) {
                if (scissor)
                    context.enableScissor(
                        round(x).toInt(),
                        round(y - height).toInt(),
                        round(x + width).toInt().coerceAtLeast(1),
                        round(y).toInt().coerceAtLeast(1)
                    )
                it.render(context, mouseX, mouseY, delta)
                if (scissor)
                    context.disableScissor()
            }
            context.matrices.pop()
        }

        context.matrices.pop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
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

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        for (it in panelSystem.list) {
            if (it.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) break
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        panelSystem.list.forEach {
            if (it.keyPressed(keyCode, scanCode, modifiers)) return false
        }
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
        wasClosed = true
        if(!animation.reversed)
            animation.setReversed(true)
    }

    override fun shouldPause() = false
}
