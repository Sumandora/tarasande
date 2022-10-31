package net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.fixed

import com.mojang.blaze3d.systems.RenderSystem
import de.florianmichael.ezeasing.EzEasing
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Alignment
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Panel
import net.tarasandedevelopment.tarasande.util.extension.withAlpha
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber

class PanelArrayList(x: Double, y: Double, screenCheatMenu: ScreenCheatMenu) : Panel("Array List", x, y, 75.0, MinecraftClient.getInstance().textRenderer.fontHeight.toDouble(), background = false, resizable = false, fixed = true) {

    private val animations = HashMap<Module, Double>()
    private var easing = EzEasing.LINEAR

    private val speedIn = ValueNumber(this, "Speed: in", 0.001, 0.005, 0.02, 0.001)
    private val speedOut = ValueNumber(this, "Speed: out", 0.001, 0.005, 0.02, 0.001)

    init {
        object : ValueMode(this, "Easing function", false, *EzEasing.functionNames().toTypedArray()) {
            override fun onChange() {
                easing = EzEasing.byName(selected[0])
            }
        }
    }

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val enabledModules = ArrayList<Module>()

        for (module in TarasandeMain.get().managerModule.list) {
            val animation = animations[module]!!
            if (module.visible.value && animation > 0.0) {
                enabledModules.add(module)
            }
        }

        var index = 0.0
        enabledModules.sortedBy { MinecraftClient.getInstance().textRenderer.getWidth(it.name) }.reversed().forEach {
            val animation = animations[it]!!
            val accent = TarasandeMain.get().clientValues.accentColor.getColor()
            val color = accent.withAlpha((animation * 255).toInt())
            RenderSystem.enableBlend()
            val animatedPosition = easing.ease(animation.toFloat())
            when (alignment) {
                Alignment.LEFT -> RenderUtil.drawWithSmallShadow(matrices, it.name, (x - (MinecraftClient.getInstance().textRenderer.getWidth(it.name) * (1.0 - animatedPosition))).toFloat(), (y + titleBarHeight + MinecraftClient.getInstance().textRenderer.fontHeight * index).toFloat(), color.rgb)
                Alignment.MIDDLE -> RenderUtil.drawWithSmallShadow(matrices, it.name, x.toFloat() + panelWidth.toFloat() / 2.0f - MinecraftClient.getInstance().textRenderer.getWidth(it.name).toFloat() / 2.0f, (y + titleBarHeight + MinecraftClient.getInstance().textRenderer.fontHeight * index).toFloat(), color.rgb)
                Alignment.RIGHT -> RenderUtil.drawWithSmallShadow(matrices, it.name, (x + panelWidth - MinecraftClient.getInstance().textRenderer.getWidth(it.name) * animatedPosition).toFloat(), (y + titleBarHeight + MinecraftClient.getInstance().textRenderer.fontHeight * index).toFloat(), color.rgb)
            }
            index += animatedPosition
        }
    }

    override fun isVisible(): Boolean {
        TarasandeMain.get().managerModule.list.forEach { module ->
            var animation = animations.putIfAbsent(module, 0.0)
            if (animation == null || animation.isNaN()) animation = 0.0 else {
                if (module.enabled) {
                    animation += speedIn.value * RenderUtil.deltaTime
                } else {
                    animation -= speedOut.value * RenderUtil.deltaTime
                }
            }
            animations[module] = MathHelper.clamp(animation, 0.0, 1.0)
        }

        for (module in TarasandeMain.get().managerModule.list) {
            val animation = animations[module]!!
            if (module.visible.value && animation > 0.0) {
                return true
            }
        }
        return false
    }
}
