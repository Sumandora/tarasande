package net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.tickable

import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.GraphTickable
import net.tarasandedevelopment.tarasande.util.extension.minecraft.minus


class GraphTickableMotion : GraphTickable("Player", "Motion", 200, false) {
    override fun tick(): Number? {
        if (mc.player == null) return null
        return (mc.player?.pos!! - Vec3d(mc.player?.prevX!!, mc.player?.prevY!!, mc.player?.prevZ!!)).horizontalLength()
    }
}

