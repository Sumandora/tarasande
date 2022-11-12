package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.player

import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.events.EventUpdate
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class ModuleAntiFall : Module("Anti fall", "Tries to force a setback when you are falling", ModuleCategory.PLAYER) {

    private val fallDistance = ValueNumber(this, "Fall distance", 0.0, 6.0, 20.0, 0.1)
    private val void = ValueBoolean(this, "Void", false)
    private val mode = ValueMode(this, "Mode", false, "Setback", "Jump")
    private val jumpMultiplier = object : ValueNumber(this, "Jump multiplier", 0.0, 1.0, 10.0, 0.1) {
        override fun isEnabled() = mode.isSelected(1)
    }
    private val boost = object : ValueNumber(this, "Boost", 0.0, 1.0, 10.0, 0.1) {
        override fun isEnabled() = mode.isSelected(1)
    }
    private val repeating = ValueBoolean(this, "Repeating", false)

    private var lastOnGroundPos: Vec3d? = null
    private var wasOnGround = false

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                if (mc.player?.isOnGround == true) {
                    lastOnGroundPos = mc.player?.pos
                    wasOnGround = true
                } else if (mc.player?.fallDistance!! > fallDistance.value && (!void.value || run { (PlayerUtil.predictFallDistance() ?: return@run false) > fallDistance.value })) {
                    if ((wasOnGround || repeating.value) && mc.player?.input?.sneaking == false) {
                        when {
                            mode.isSelected(0) -> {
                                if (lastOnGroundPos != null) {
                                    mc.player?.setPosition(lastOnGroundPos?.add(0.0, 1.0, 0.0))
                                    mc.player?.velocity = Vec3d(0.0, 0.0, 0.0)
                                }
                            }

                            mode.isSelected(1) -> {
                                mc.player?.jump()
                                mc.player?.velocity = mc.player?.velocity?.multiply(boost.value, jumpMultiplier.value, boost.value)
                            }
                        }
                        mc.player?.fallDistance = 0.0f
                    }
                    wasOnGround = false
                }
            }
        }
    }
}