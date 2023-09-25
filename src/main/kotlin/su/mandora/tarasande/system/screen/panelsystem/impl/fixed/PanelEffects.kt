package su.mandora.tarasande.system.screen.panelsystem.impl.fixed

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.resource.language.I18n
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffectUtil
import net.minecraft.registry.Registries
import net.minecraft.util.Formatting
import su.mandora.tarasande.injection.accessor.ILivingEntity
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.panelsystem.Panel
import su.mandora.tarasande.system.screen.panelsystem.api.PanelFixed
import su.mandora.tarasande.util.extension.javaruntime.withAlpha
import su.mandora.tarasande.util.extension.minecraft.extractContent
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.animation.Animator
import su.mandora.tarasande.util.render.font.FontWrapper
import su.mandora.tarasande.util.render.helper.Alignment
import java.awt.Color

class PanelEffects : PanelFixed("Effects", 75.0, FontWrapper.fontHeight().toDouble(), resizable = false) {

    private val animations = HashMap<StatusEffect, Double>()
    private val prevInstances = HashMap<StatusEffect, StatusEffectInstance>()

    private val animator = Animator(this)

    override fun renderContent(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val activeStatusEffects = ArrayList<Triple<StatusEffect, String, Int>>()

        for (statusEffect in Registries.STATUS_EFFECT) {
            val animation = animations[statusEffect]!!
            if (animation > 0.0) {
                (mc.player as ILivingEntity).also {
                    val statusEffectInstance = if (it.tarasande_forceHasStatusEffect(statusEffect)) it.tarasande_forceGetStatusEffect(statusEffect) else prevInstances.getOrDefault(statusEffect, null)
                    if (statusEffectInstance != null) prevInstances[statusEffect] = statusEffectInstance
                    if (statusEffect != null) {
                        var string = I18n.translate(statusEffectInstance?.effectType?.translationKey!!)
                        if (statusEffectInstance.amplifier in 1..9) {
                            string += " " + I18n.translate("enchantment.level." + (statusEffectInstance.amplifier + 1))
                        }
                        string += ": " + Formatting.GRAY.toString() + StatusEffectUtil.getDurationText(statusEffectInstance, 1F).extractContent()
                        activeStatusEffects.add(Triple(statusEffect, string, statusEffect.color))
                    }
                }
            }
        }

        var index = 0.0
        activeStatusEffects.sortedBy { FontWrapper.getWidth(it.second) }.reversed().forEach {
            val animation = animations[it.first]!!
            if (animation > animator.speedIn.min) { // hack
                val color = Color(it.third).withAlpha((animation * 255).toInt())
                RenderSystem.enableBlend()
                val animatedPosition = animator.easing.ease(animation)
                when (alignment) {
                    Alignment.LEFT -> FontWrapper.textShadow(context, it.second, (x - (FontWrapper.getWidth(it.second) * (1.0 - animatedPosition))).toFloat(), (y + titleBarHeight + FontWrapper.fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
                    Alignment.MIDDLE -> FontWrapper.textShadow(context, it.second, x.toFloat() + panelWidth.toFloat() / 2F - FontWrapper.getWidth(it.second).toFloat() / 2F, (y + titleBarHeight + FontWrapper.fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
                    Alignment.RIGHT -> FontWrapper.textShadow(context, it.second, (x + panelWidth - FontWrapper.getWidth(it.second) * animatedPosition).toFloat(), (y + titleBarHeight + FontWrapper.fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
                }
                index += animatedPosition
            }
        }
    }

    override fun isVisible(): Boolean {
        Registries.STATUS_EFFECT.forEach { statusEffect ->
            var animation = animations.putIfAbsent(statusEffect, 0.0)
            if (animation == null || animation.isNaN()) animation = 0.0 else {
                if ((mc.player as ILivingEntity).tarasande_forceHasStatusEffect(statusEffect)) {
                    animation += animator.speedIn.value * RenderUtil.deltaTime
                } else {
                    animation -= animator.speedOut.value * RenderUtil.deltaTime
                }
            }
            animations[statusEffect] = animation.coerceIn(0.0, 1.0)
        }

        return animations.any { it.value > 0.01 }
    }
}
