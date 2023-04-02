package su.mandora.tarasande.system.screen.graphsystem.impl.tickable

import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.graphsystem.GraphTickable
import su.mandora.tarasande.util.extension.minecraft.minus


class GraphTickableMotion : GraphTickable("Player", "Motion", 200, false) {
    override fun tick(): Number? {
        if (mc.player == null) return null
        return (mc.player?.pos!! - Vec3d(mc.player?.prevX!!, mc.player?.prevY!!, mc.player?.prevZ!!)).horizontalLength()
    }
}

