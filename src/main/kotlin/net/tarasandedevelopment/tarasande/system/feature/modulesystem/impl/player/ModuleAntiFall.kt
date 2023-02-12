package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import kotlin.math.abs
import kotlin.math.min

class ModuleAntiFall : Module("Anti fall", "Tries to force a setback when you are falling", ModuleCategory.PLAYER) {

    private val fallDistance = ValueNumber(this, "Fall distance", 0.0, 3.0, 10.0, 0.1)
    private val void = ValueBoolean(this, "Void", false)
    private val mode = ValueMode(this, "Mode", false, "Setback", "Jump")
    private val jumpMultiplier = ValueNumber(this, "Jump multiplier", 0.0, 1.0, 10.0, 0.1, isEnabled = { mode.isSelected(1) })
    private val boost = ValueNumber(this, "Boost", 0.0, 1.0, 10.0, 0.1, isEnabled = { mode.isSelected(1) })
    private val repeating = ValueBoolean(this, "Repeating", false)

    private var lastOnGroundPos: Vec3d? = null
    private var wasOnGround = false

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                if (mc.player?.isOnGround == true) {
                    lastOnGroundPos = mc.player?.pos
                    wasOnGround = true
                } else if (mc.player?.fallDistance!! > fallDistance.value && (!void.value || PlayerUtil.predictFallDistance() == null)) {
                    if ((wasOnGround || repeating.value) && !PlayerUtil.input.sneaking) {
                        when {
                            mode.isSelected(0) -> {
                                if (lastOnGroundPos != null) {
                                    mc.player?.setPosition(lastOnGroundPos?.add(0.0, abs(min(mc.player?.velocity?.y!!, 0.0)), 0.0))
                                    mc.player?.velocity = Vec3d(0.0, 0.0, 0.0)
                                }
                            }

                            mode.isSelected(1) -> {
                                mc.player?.jump()
                                mc.player?.velocity = mc.player?.velocity?.multiply(boost.value, jumpMultiplier.value, boost.value)
                            }
                        }
                        mc.player?.fallDistance = 0.0F
                    }
                    wasOnGround = false
                }
            }
        }
    }
}