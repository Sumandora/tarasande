package net.tarasandedevelopment.tarasande.util.dummy

import net.minecraft.client.network.ClientDynamicRegistryType
import net.minecraft.client.world.ClientWorld
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.util.math.BlockPos
import net.tarasandedevelopment.tarasande.util.unsafe.UnsafeProvider

val combinedRegistryManager: DynamicRegistryManager.Immutable = ClientDynamicRegistryType.createCombinedDynamicRegistries().combinedRegistryManager

class ClientWorldDummy : ClientWorld(null, null, null, null, 1, 1, null, null, false, 0L) {

    init {
        error("Don't call the ClientWorldDummy constructor, use create() instead")
    }

    override fun calculateAmbientDarkness() {
    }

    override fun initWeatherGradients() {
    }

    override fun setSpawnPos(pos: BlockPos?, angle: Float) {
    }

    override fun getRegistryManager() = combinedRegistryManager

    override fun getSpawnPos() = BlockPos(0, 0, 0)

    override fun getSpawnAngle() = 0F

    override fun getTimeOfDay() = 0L

    override fun getTime() = 0L

    companion object {
        // Don't actually create an instance to prevent crashes
        fun create() = UnsafeProvider.unsafe.allocateInstance(ClientWorldDummy::class.java) as ClientWorldDummy
    }
}
