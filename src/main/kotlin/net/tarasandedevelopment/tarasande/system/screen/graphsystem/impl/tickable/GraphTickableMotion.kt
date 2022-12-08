package net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.tickable

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.GraphTickable
import net.tarasandedevelopment.tarasande.util.extension.minecraft.minus


class GraphTickableMotion : GraphTickable("Player", "Motion", 200, false) {
    override fun tick(): Number? {
        if (MinecraftClient.getInstance().player == null) return null
        return (MinecraftClient.getInstance().player?.pos!! - Vec3d(MinecraftClient.getInstance().player?.prevX!!, MinecraftClient.getInstance().player?.prevY!!, MinecraftClient.getInstance().player?.prevZ!!)).horizontalLength()
    }
}

