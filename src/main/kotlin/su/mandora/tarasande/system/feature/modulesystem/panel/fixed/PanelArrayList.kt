package su.mandora.tarasande.system.feature.modulesystem.panel.fixed

import com.mojang.blaze3d.systems.RenderSystem
import de.florianmichael.rmath.EzEasing
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.screen.panelsystem.Panel
import su.mandora.tarasande.util.extension.javaruntime.withAlpha
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.font.FontWrapper
import su.mandora.tarasande.util.render.helper.Alignment
import su.mandora.tarasande.system.feature.modulesystem.Module

class PanelArrayList(private val moduleSystem: ManagerModule) : Panel("Array List", 75.0, FontWrapper.fontHeight().toDouble()) {

    private val animations = HashMap<Module, Double>()
    private var easing = EzEasing.LINEAR

    private val speedIn = ValueNumber(this, "Speed: in", 0.001, 0.005, 0.02, 0.001)
    private val speedOut = ValueNumber(this, "Speed: out", 0.001, 0.005, 0.02, 0.001)

    init {
        object : ValueMode(this, "Easing function", false, *EzEasing.functionNames().toTypedArray()) {
            override fun onChange(index: Int, oldSelected: Boolean, newSelected: Boolean) {
                easing = EzEasing.byName(getSelected())
            }
        }
    }

    override fun renderContent(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
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
            if (animation > speedIn.min) { // hack
                val accent = TarasandeValues.accentColor.getColor()
                val color = accent.withAlpha((animation * 255).toInt())
                RenderSystem.enableBlend()
                val animatedPosition = easing.ease(animation.toFloat())
                when (alignment) {
                    Alignment.LEFT -> FontWrapper.textShadow(matrices, it.name, (x - (FontWrapper.getWidth(it.name) * (1.0 - animatedPosition))).toFloat(), (y + titleBarHeight + FontWrapper.fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
                    Alignment.MIDDLE -> FontWrapper.textShadow(matrices, it.name, x.toFloat() + panelWidth.toFloat() / 2.0F - FontWrapper.getWidth(it.name).toFloat() / 2.0F, (y + titleBarHeight + FontWrapper.fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
                    Alignment.RIGHT -> FontWrapper.textShadow(matrices, it.name, (x + panelWidth - FontWrapper.getWidth(it.name) * animatedPosition).toFloat(), (y + titleBarHeight + FontWrapper.fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
                }
                index += animatedPosition
            }
        }
    }

    override fun isVisible(): Boolean {
        moduleSystem.list.forEach { module ->
            var animation = animations.putIfAbsent(module, 0.0)
            if (animation == null || animation.isNaN()) animation = 0.0 else {
                if (module.enabled.value) {
                    animation += speedIn.value * RenderUtil.deltaTime
                } else {
                    animation -= speedOut.value * RenderUtil.deltaTime
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
