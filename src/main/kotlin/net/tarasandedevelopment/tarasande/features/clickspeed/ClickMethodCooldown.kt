package net.tarasandedevelopment.tarasande.features.clickspeed

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.base.features.clickspeed.ClickMethod

class ClickMethodCooldown : ClickMethod("Cooldown", false) {

    override fun getClicks(targetedCPS: Double): Int = if (MinecraftClient.getInstance().player!!.getAttackCooldownProgress(0.5F) <= 0.9F) 0 else 1

    override fun reset(targetedCPS: Double) = Unit // nothing there
}
