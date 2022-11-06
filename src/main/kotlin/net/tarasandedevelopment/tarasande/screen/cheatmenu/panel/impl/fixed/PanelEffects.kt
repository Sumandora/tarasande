package net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.fixed

import com.mojang.blaze3d.systems.RenderSystem
import de.florianmichael.ezeasing.EzEasing
import net.minecraft.client.MinecraftClient
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffectUtil
import net.minecraft.util.Formatting
import net.minecraft.util.math.MathHelper
import net.minecraft.util.registry.Registry
import net.tarasandedevelopment.tarasande.mixin.accessor.IClientPlayerEntity
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Alignment
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Panel
import net.tarasandedevelopment.tarasande.util.extension.withAlpha
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.awt.Color

class PanelEffects(x: Double, y: Double, screenCheatMenu: ScreenCheatMenu) : Panel("Effects", x, y, 75.0, RenderUtil.font().fontHeight().toDouble(), background = false, resizable = false, fixed = true) {

    private val animations = HashMap<StatusEffect, Double>()
    private val prevInstances = HashMap<StatusEffect, StatusEffectInstance>()

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
        val activeStatusEffects = ArrayList<Triple<StatusEffect, String, Int>>()

        for (statusEffect in Registry.STATUS_EFFECT) {
            val animation = animations[statusEffect]!!
            if (animation > 0.0) {
                (MinecraftClient.getInstance().player as IClientPlayerEntity).also {
                    val statusEffectInstance = if (it.tarasande_forceHasStatusEffect(statusEffect)) it.tarasande_forceGetStatusEffect(statusEffect) else prevInstances.getOrDefault(statusEffect, null)
                    if (statusEffectInstance != null) prevInstances[statusEffect] = statusEffectInstance
                    if (statusEffect != null) {
                        var string = I18n.translate(statusEffectInstance?.effectType?.translationKey!!)
                        if (statusEffectInstance.amplifier in 1..9) {
                            string += " " + I18n.translate("enchantment.level." + (statusEffectInstance.amplifier + 1))
                        }
                        string += ": " + Formatting.GRAY.toString() + StatusEffectUtil.durationToString(statusEffectInstance, 1.0f)
                        activeStatusEffects.add(Triple(statusEffect, string, statusEffect.color))
                    }
                }
            }
        }

        var index = 0.0
        activeStatusEffects.sortedBy { RenderUtil.font().getWidth(it.second) }.reversed().forEach {
            val animation = animations[it.first]!!
            val color = Color(it.third).withAlpha((animation * 255).toInt())
            RenderSystem.enableBlend()
            val animatedPosition = easing.ease(animation.toFloat())
            when (alignment) {
                Alignment.LEFT -> RenderUtil.font().textShadow(matrices, it.second, (x - (RenderUtil.font().getWidth(it.second) * (1.0 - animatedPosition))).toFloat(), (y + titleBarHeight + RenderUtil.font().fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
                Alignment.MIDDLE -> RenderUtil.font().textShadow(matrices, it.second, x.toFloat() + panelWidth.toFloat() / 2.0f - RenderUtil.font().getWidth(it.second).toFloat() / 2.0f, (y + titleBarHeight + RenderUtil.font().fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
                Alignment.RIGHT -> RenderUtil.font().textShadow(matrices, it.second, (x + panelWidth - RenderUtil.font().getWidth(it.second) * animatedPosition).toFloat(), (y + titleBarHeight + RenderUtil.font().fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
            }
            index += animatedPosition
        }
    }

    override fun isVisible(): Boolean {
        Registry.STATUS_EFFECT.forEach { statusEffect ->
            var animation = animations.putIfAbsent(statusEffect, 0.0)
            if (animation == null || animation.isNaN()) animation = 0.0 else {
                if ((MinecraftClient.getInstance().player as IClientPlayerEntity).tarasande_forceHasStatusEffect(statusEffect) ?: return false) {
                    animation += speedIn.value * RenderUtil.deltaTime
                } else {
                    animation -= speedOut.value * RenderUtil.deltaTime
                }
            }
            animations[statusEffect] = MathHelper.clamp(animation, 0.0, 1.0)
        }

        return animations.any { it.value > 0.01 }
    }
}
