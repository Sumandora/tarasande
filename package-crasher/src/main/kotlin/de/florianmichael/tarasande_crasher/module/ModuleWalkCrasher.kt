package de.florianmichael.tarasande_crasher.module

import de.florianmichael.tarasande_crasher.forcePacket
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import kotlin.math.cos
import kotlin.math.sin

class ModuleWalkCrasher : Module("Walk crasher", "Crashes the server by sending random velocity", "Crasher") {
    private val speed = ValueNumber(this, "Speed", 0.0, 10.0, 100.0, 1.0)

    override fun onEnable() {
        super.onEnable()
        mc.player!!.setVelocity(speed.value * cos(Math.toRadians(mc.player!!.yaw + 90.0)), 0.0, speed.value * sin(Math.toRadians(mc.player!!.yaw + 90.0)))
        if (!mc.player!!.isOnGround) {
            forcePacket(PlayerMoveC2SPacket.OnGroundOnly(true))
            mc.player!!.isSprinting = true
            mc.player!!.setVelocity(mc.player!!.velocity.x, -0.05, mc.player!!.vehicle!!.z)
        } else if (mc.player!!.horizontalCollision) {
            mc.player!!.setVelocity(mc.player!!.velocity.x, 1.0, mc.player!!.velocity.z)
        }
        mc.player!!.yaw = 0F
    }
}
