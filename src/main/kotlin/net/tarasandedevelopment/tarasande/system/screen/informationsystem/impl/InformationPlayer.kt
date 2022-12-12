package net.tarasandedevelopment.tarasande.system.screen.informationsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.dimension.DimensionType
import net.minecraft.world.dimension.DimensionTypes
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.string.StringUtil

class InformationName : Information("Player", "Name") {

    override fun getMessage() = MinecraftClient.getInstance().session.username!!
}

class InformationXYZ : Information("Player", "XYZ") {
    private val decimalPlacesX = ValueNumber(this, "Decimal places: x", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesY = ValueNumber(this, "Decimal places: y", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesZ = ValueNumber(this, "Decimal places: z", 0.0, 1.0, 5.0, 1.0)

    override fun getMessage(): String? {
        val player = MinecraftClient.getInstance().player ?: return null

        val dimension = MinecraftClient.getInstance().world?.dimension ?: return null
        val nether = MinecraftClient.getInstance().world?.registryManager?.get(RegistryKeys.DIMENSION_TYPE)?.get(DimensionTypes.OVERWORLD) ?: return null
        val scaleFactor = DimensionType.getCoordinateScaleFactor(dimension, nether)
        val pos = player.pos.multiply(scaleFactor, 1.0, scaleFactor)

        return StringUtil.round(pos.x, this.decimalPlacesX.value.toInt()) + " " +
                StringUtil.round(pos.y, this.decimalPlacesY.value.toInt()) + " " +
                StringUtil.round(pos.z, this.decimalPlacesZ.value.toInt())
    }
}

class InformationNetherXYZ : Information("Player", "Nether XYZ") {
    private val decimalPlacesX = ValueNumber(this, "Decimal places: x", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesY = ValueNumber(this, "Decimal places: y", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesZ = ValueNumber(this, "Decimal places: z", 0.0, 1.0, 5.0, 1.0)

    override fun getMessage(): String? {
        val player = MinecraftClient.getInstance().player ?: return null

        val dimension = MinecraftClient.getInstance().world?.dimension ?: return null
        val nether = MinecraftClient.getInstance().world?.registryManager?.get(RegistryKeys.DIMENSION_TYPE)?.get(DimensionTypes.THE_NETHER) ?: return null
        val scaleFactor = DimensionType.getCoordinateScaleFactor(dimension, nether)
        val pos = player.pos.multiply(scaleFactor, 1.0, scaleFactor)

        return StringUtil.round(pos.x, this.decimalPlacesX.value.toInt()) + " " +
                StringUtil.round(pos.y, this.decimalPlacesY.value.toInt()) + " " +
                StringUtil.round(pos.z, this.decimalPlacesZ.value.toInt())
    }
}

class InformationVelocity : Information("Player", "Velocity") {
    private val decimalPlacesX = ValueNumber(this, "Decimal places: x", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesY = ValueNumber(this, "Decimal places: y", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesZ = ValueNumber(this, "Decimal places: z", 0.0, 1.0, 5.0, 1.0)

    override fun getMessage(): String? {
        val player = MinecraftClient.getInstance().player ?: return null

        return player.velocity.let {
            StringUtil.round(it.x, decimalPlacesX.value.toInt()) + " " +
                    StringUtil.round(it.y, decimalPlacesY.value.toInt()) + " " +
                    StringUtil.round(it.z, decimalPlacesZ.value.toInt())
        }
    }
}

class InformationRotation : Information("Player", "Rotation") {
    private val decimalPlacesYaw = ValueNumber(this, "Decimal places: yaw", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesPitch = ValueNumber(this, "Decimal places: pitch", 0.0, 1.0, 5.0, 1.0)

    override fun getMessage(): String? {
        val player = MinecraftClient.getInstance().player ?: return null

        return StringUtil.round(player.yaw.toDouble(), this.decimalPlacesYaw.value.toInt()) + " " + StringUtil.round(player.pitch.toDouble(), this.decimalPlacesPitch.value.toInt())
    }
}

class InformationFakeRotation : Information("Player", "Fake Rotation") {
    private val decimalPlacesYaw = ValueNumber(this, "Decimal places: yaw", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesPitch = ValueNumber(this, "Decimal places: pitch", 0.0, 1.0, 5.0, 1.0)

    override fun getMessage(): String? {
        return RotationUtil.fakeRotation?.let { StringUtil.round(it.yaw.toDouble(), this.decimalPlacesYaw.value.toInt()) + " " + StringUtil.round(it.pitch.toDouble(), this.decimalPlacesPitch.value.toInt()) }
    }
}