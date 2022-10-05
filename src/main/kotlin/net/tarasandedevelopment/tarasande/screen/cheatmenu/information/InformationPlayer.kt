package net.tarasandedevelopment.tarasande.screen.cheatmenu.information

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.information.Information
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import net.tarasandedevelopment.tarasande.value.ValueNumber

class InformationXYZ : Information("Player", "XYZ") {
    private val decimalPlacesX = ValueNumber(this, "Decimal places: x", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesY = ValueNumber(this, "Decimal places: y", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesZ = ValueNumber(this, "Decimal places: z", 0.0, 1.0, 5.0, 1.0)

    override fun getMessage(): String? {
        if (MinecraftClient.getInstance().player == null) return null

        return (StringUtil.round(MinecraftClient.getInstance().player?.x!!, this.decimalPlacesX.value.toInt())) + " " +
                (StringUtil.round(MinecraftClient.getInstance().player?.y!!, this.decimalPlacesY.value.toInt()) + " " +
                        StringUtil.round(MinecraftClient.getInstance().player?.z!!, this.decimalPlacesZ.value.toInt()))
    }
}

class InformationNetherXYZ : Information("Player", "Nether XYZ") {
    private val decimalPlacesX = ValueNumber(this, "Decimal places: x", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesY = ValueNumber(this, "Decimal places: y", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesZ = ValueNumber(this, "Decimal places: z", 0.0, 1.0, 5.0, 1.0)

    override fun getMessage(): String? {
        if (MinecraftClient.getInstance().player == null) return null

        return (StringUtil.round(MinecraftClient.getInstance().player?.x!! / 8.0, this.decimalPlacesX.value.toInt())) + " " +
                (StringUtil.round(MinecraftClient.getInstance().player?.y!! / 8.0, this.decimalPlacesY.value.toInt()) + " " +
                        StringUtil.round(MinecraftClient.getInstance().player?.z!! / 8.0, this.decimalPlacesZ.value.toInt()))
    }
}

class InformationRotation : Information("Player", "Rotation") {
    private val decimalPlacesYaw = ValueNumber(this, "Decimal places: yaw", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesPitch = ValueNumber(this, "Decimal places: pitch", 0.0, 1.0, 5.0, 1.0)

    override fun getMessage(): String? {
        if (MinecraftClient.getInstance().player == null) return null

        return StringUtil.round(MinecraftClient.getInstance().player?.yaw!!.toDouble(), this.decimalPlacesYaw.value.toInt()) + " " + StringUtil.round(MinecraftClient.getInstance().player?.pitch!!.toDouble(), this.decimalPlacesPitch.value.toInt())
    }
}

class InformationFakeRotation : Information("Player", "Fake Rotation") {
    private val decimalPlacesYaw = ValueNumber(this, "Decimal places: yaw", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesPitch = ValueNumber(this, "Decimal places: pitch", 0.0, 1.0, 5.0, 1.0)

    override fun getMessage(): String? {
        if (RotationUtil.fakeRotation == null) return null
        return StringUtil.round(RotationUtil.fakeRotation?.yaw!!.toDouble(), this.decimalPlacesYaw.value.toInt()) + " " + StringUtil.round(RotationUtil.fakeRotation?.pitch!!.toDouble(), this.decimalPlacesPitch.value.toInt())
    }
}