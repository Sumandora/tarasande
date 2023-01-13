package net.tarasandedevelopment.tarasande.system.screen.panelsystem.impl.fixed

import com.mojang.blaze3d.systems.RenderSystem
import de.florianmichael.ezeasing.EzEasing
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffectUtil
import net.minecraft.registry.Registries
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.injection.accessor.ILivingEntity
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.util.extension.javaruntime.withAlpha
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.util.render.helper.Alignment
import java.awt.Color

class PanelEffects : Panel("Effects", 75.0, FontWrapper.fontHeight().toDouble()) {

    private val animations = HashMap<StatusEffect, Double>()
    private val prevInstances = HashMap<StatusEffect, StatusEffectInstance>()

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
                        string += ": " + Formatting.GRAY.toString() + StatusEffectUtil.durationToString(statusEffectInstance, 1.0F)
                        activeStatusEffects.add(Triple(statusEffect, string, statusEffect.color))
                    }
                }
            }
        }

        var index = 0.0
        activeStatusEffects.sortedBy { FontWrapper.getWidth(it.second) }.reversed().forEach {
            val animation = animations[it.first]!!
            if (animation > speedIn.min) { // hack
                val color = Color(it.third).withAlpha((animation * 255).toInt())
                RenderSystem.enableBlend()
                val animatedPosition = easing.ease(animation.toFloat())
                when (alignment) {
                    Alignment.LEFT -> FontWrapper.textShadow(matrices, it.second, (x - (FontWrapper.getWidth(it.second) * (1.0 - animatedPosition))).toFloat(), (y + titleBarHeight + FontWrapper.fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
                    Alignment.MIDDLE -> FontWrapper.textShadow(matrices, it.second, x.toFloat() + panelWidth.toFloat() / 2.0F - FontWrapper.getWidth(it.second).toFloat() / 2.0F, (y + titleBarHeight + FontWrapper.fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
                    Alignment.RIGHT -> FontWrapper.textShadow(matrices, it.second, (x + panelWidth - FontWrapper.getWidth(it.second) * animatedPosition).toFloat(), (y + titleBarHeight + FontWrapper.fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
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
                    animation += speedIn.value * RenderUtil.deltaTime
                } else {
                    animation -= speedOut.value * RenderUtil.deltaTime
                }
            }
            animations[statusEffect] = animation.coerceIn(0.0, 1.0)
        }

        return animations.any { it.value > 0.01 }
    }
}
