package su.mandora.tarasande.system.feature.clickmethodsystem.impl

import su.mandora.tarasande.mc
import su.mandora.tarasande.system.feature.clickmethodsystem.ClickMethod

class ClickMethodCooldown : ClickMethod("Cooldown", false) {

    override fun getClicks(targetedCPS: Double) = if (mc.player!!.getAttackCooldownProgress(0.25F) < 1.0F) 0 else 1

    override fun reset(targetedCPS: Double) {
        // nothing there
    }
}
