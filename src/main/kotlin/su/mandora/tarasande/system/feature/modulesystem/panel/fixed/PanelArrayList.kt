package su.mandora.tarasande.system.feature.modulesystem.panel.fixed

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.math.MathHelper
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.screen.panelsystem.Panel
import su.mandora.tarasande.util.extension.javaruntime.withAlpha
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.animation.Animator
import su.mandora.tarasande.util.render.font.FontWrapper
import su.mandora.tarasande.util.render.helper.Alignment

class PanelArrayList(private val moduleSystem: ManagerModule) : Panel("Array List", 75.0, FontWrapper.fontHeight().toDouble(), resizable = false) {

    private val animations = HashMap<Module, Double>()

    override fun renderContent(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val enabledModules = ArrayList<Module>()

        for (module in moduleSystem.list) {
            val animation = animations[module]!!
            if (module.visible.value && animation > 0.0) {
                enabledModules.add(module)
            }
        }

        var index = 0.0
        enabledModules.sortedBy { FontWrapper.getWidth(it.name) }.reversed().forEach {
            val animation = animations[it]!!
            if (animation > animator.speedIn.min) { // hack
                val accent = TarasandeValues.accentColor.getColor()
                val color = accent.withAlpha((animation * 255).toInt())
                RenderSystem.enableBlend()
                val animatedPosition = animator.easing.ease(animation)
                when (alignment) {
                    Alignment.LEFT -> FontWrapper.textShadow(context, it.name, (x - (FontWrapper.getWidth(it.name) * (1.0 - animatedPosition))).toFloat(), (y + titleBarHeight + FontWrapper.fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
                    Alignment.MIDDLE -> FontWrapper.textShadow(context, it.name, x.toFloat() + panelWidth.toFloat() / 2F - FontWrapper.getWidth(it.name).toFloat() / 2F, (y + titleBarHeight + FontWrapper.fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
                    Alignment.RIGHT -> FontWrapper.textShadow(context, it.name, (x + panelWidth - FontWrapper.getWidth(it.name) * animatedPosition).toFloat(), (y + titleBarHeight + FontWrapper.fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
                }
                index += animatedPosition
            }
        }
    }

    private val animator = Animator(this)

    override fun isVisible(): Boolean {
        moduleSystem.list.forEach { module ->
            var animation = animations.putIfAbsent(module, 0.0)
            if (animation == null || animation.isNaN()) animation = 0.0 else {
                if (module.enabled.value) {
                    animation += animator.speedIn.value * RenderUtil.deltaTime
                } else {
                    animation -= animator.speedOut.value * RenderUtil.deltaTime
                }
            }
            animations[module] = MathHelper.clamp(animation, 0.0, 1.0)
        }

        for (module in moduleSystem.list) {
            val animation = animations[module]!!
            if (module.visible.value && animation > 0.0) {
                return true
            }
        }
        return false
    }
}
