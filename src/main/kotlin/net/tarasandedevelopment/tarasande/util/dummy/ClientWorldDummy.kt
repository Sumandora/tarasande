package net.tarasandedevelopment.tarasande.util.dummy

import net.minecraft.client.network.ClientDynamicRegistryType
import net.minecraft.client.world.ClientWorld
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.util.math.BlockPos
import net.minecraft.world.dimension.DimensionType
import net.minecraft.world.dimension.DimensionTypes

class ClientWorldDummy : ClientWorld(null, null, null, DimensionTypes, 1, 1, null, null, false, 0L) {

    override fun calculateAmbientDarkness() {
    }

    override fun initWeatherGradients() {
    }

    override fun setSpawnPos(pos: BlockPos?, angle: Float) {
    }

    override fun getRegistryManager() = ClientDynamicRegistryType.createCombinedDynamicRegistries().combinedRegistryManager

    override fun getSpawnPos() = BlockPos(0, 0, 0)

    override fun getSpawnAngle() = 0F

    override fun getTimeOfDay() = 0L

    override fun getTime() = 0L
}
