package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModulePerfectHorseJump : Module("Perfect horse jump", "Forces perfect horse jumps", ModuleCategory.MOVEMENT) {

    private val jumpPower = ValueNumber(this, "Jump power", 0.1, 1.0, 1.0, 1E-2)
    private val jumpPowerCounter = ValueNumber(this, "Jump power: counter", 1.0, 9.0, 9.0, 1E-2)

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                mc.player?.also {
                    it.mountJumpStrength = this.jumpPower.value.toFloat()
                    it.field_3938 = this.jumpPowerCounter.value.toInt()
                }
            }
        }
    }
}
