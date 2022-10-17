package net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.fixed

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Panel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import java.awt.Color

class PanelArrayList(x: Double, y: Double, screenCheatMenu: ScreenCheatMenu) : Panel("Array List", x, y, 75.0, MinecraftClient.getInstance().textRenderer.fontHeight.toDouble(), background = false, resizable = false, fixed = true) {

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
            alignedString(matrices, it.name, accent.rgb, ((index + animation) * MinecraftClient.getInstance().textRenderer.fontHeight).toFloat())
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