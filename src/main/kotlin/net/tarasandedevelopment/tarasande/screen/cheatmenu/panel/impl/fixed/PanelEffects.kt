package net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.fixed

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffectUtil
import net.minecraft.util.Formatting
import net.minecraft.util.math.MathHelper
import net.minecraft.util.registry.Registry
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Panel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import java.awt.Color

class PanelEffects(x: Double, y: Double, screenCheatMenu: ScreenCheatMenu) : Panel("Effects", x, y, 75.0, MinecraftClient.getInstance().textRenderer.fontHeight.toDouble(), background = false, resizable = false, fixed = true) {

    private val animations = HashMap<StatusEffect, Double>()
    private val prevInstances = HashMap<StatusEffect, StatusEffectInstance>()

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val activeStatusEffects = ArrayList<Triple<StatusEffect, String, Int>>()

        for (statusEffect in Registry.STATUS_EFFECT) {
            val animation = animations[statusEffect]!!
            if (animation > 0.0) {
                val statusEffectInstance = if (MinecraftClient.getInstance().player?.hasStatusEffect(statusEffect)!!) MinecraftClient.getInstance().player?.getStatusEffect(statusEffect) else prevInstances.getOrDefault(statusEffect, null)
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

        var index = 0.0
        activeStatusEffects.sortedBy { MinecraftClient.getInstance().textRenderer.getWidth(it.second) }.reversed().forEach {
            val animation = animations[it.first]!!
            val color = Color((it.third shr 16) and 0xFF, (it.third shr 8) and 0xFF, (it.third shr 0) and 0xFF, (animation * 255).toInt())
            RenderSystem.enableBlend()
            alignedString(matrices, it.second, color.rgb, ((index + animation) * MinecraftClient.getInstance().textRenderer.fontHeight).toFloat())
            index += animation
        }
    }

    override fun isVisible(): Boolean {
        Registry.STATUS_EFFECT.forEach { statusEffect ->
            var animation = animations.putIfAbsent(statusEffect, 0.0)
            if (animation == null || animation.isNaN()) animation = 0.0 else {
                if (MinecraftClient.getInstance().player?.hasStatusEffect(statusEffect)!!) {
                    animation += 0.005 * RenderUtil.deltaTime
                } else {
                    animation -= 0.005 * RenderUtil.deltaTime
                }
            }
            animations[statusEffect] = MathHelper.clamp(animation, 0.0, 1.0)
        }

        return animations.any { it.value > 0.01 }
    }

}