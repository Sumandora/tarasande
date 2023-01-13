package net.tarasandedevelopment.tarasande.system.feature.clickmethodsystem.impl

import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.feature.clickmethodsystem.ClickMethod

class ClickMethodCooldown : ClickMethod("Cooldown", false) {

    override fun getClicks(targetedCPS: Double) = if (mc.player!!.getAttackCooldownProgress(0.5F) <= 0.9F) 0 else 1

    override fun reset(targetedCPS: Double) {
        // nothing there
    }
}
