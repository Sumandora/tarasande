package net.tarasandedevelopment.tarasande.screen.menu.panel.impl.fixed.impl

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.screen.menu.panel.Alignment
import net.tarasandedevelopment.tarasande.screen.menu.panel.impl.fixed.PanelFixed
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import java.awt.Color

class PanelFixedArrayList(x: Double, y: Double) : PanelFixed("Array List", x, y, 75.0, resizable = false) {

    private val animations = HashMap<Module, Double>()

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
            val color = Color(accent.red, accent.green, accent.blue, (animation * 255).toInt())
            RenderSystem.enableBlend()
            when (alignment) {
                Alignment.LEFT -> RenderUtil.drawWithSmallShadow(matrices, it.name, (x - (MinecraftClient.getInstance().textRenderer.getWidth(it.name) * (1.0 - animation))).toFloat(), (y + titleBarHeight + MinecraftClient.getInstance().textRenderer.fontHeight * index).toFloat(), color.rgb)
                Alignment.MIDDLE -> RenderUtil.drawWithSmallShadow(matrices, it.name, x.toFloat() + panelWidth.toFloat() / 2.0f - MinecraftClient.getInstance().textRenderer.getWidth(it.name).toFloat() / 2.0f, (y + titleBarHeight + MinecraftClient.getInstance().textRenderer.fontHeight * index).toFloat(), color.rgb)
                Alignment.RIGHT -> RenderUtil.drawWithSmallShadow(matrices, it.name, (x + panelWidth - MinecraftClient.getInstance().textRenderer.getWidth(it.name) * animation).toFloat(), (y + titleBarHeight + MinecraftClient.getInstance().textRenderer.fontHeight * index).toFloat(), color.rgb)
            }
            index += animation
        }
    }

    override fun isVisible(): Boolean {
        TarasandeMain.get().managerModule.list.forEach { module ->
            var animation = animations.putIfAbsent(module, 0.0)
            if (animation == null || animation.isNaN()) animation = 0.0 else {
                if (module.enabled) {
                    animation += 0.005 * RenderUtil.deltaTime
                } else {
                    animation -= 0.005 * RenderUtil.deltaTime
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