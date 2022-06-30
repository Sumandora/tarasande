package su.mandora.tarasande.screen.menu.information

import net.minecraft.client.MinecraftClient
import su.mandora.tarasande.base.screen.menu.information.Information
import su.mandora.tarasande.util.math.rotation.RotationUtil
import kotlin.math.round

class InformationXYZ : Information("Player", "XYZ") {
    override fun getMessage(): String? {
        if (MinecraftClient.getInstance().player == null) return null
        return (round(MinecraftClient.getInstance().player?.x!! * 10) / 10.0).toString() + " " + (round(MinecraftClient.getInstance().player?.y!! * 10) / 10.0) + " " + (round(MinecraftClient.getInstance().player?.z!! * 10) / 10.0)
    }
}

class InformationNetherXYZ : Information("Player", "Nether XYZ") {
    override fun getMessage(): String? {
        if (MinecraftClient.getInstance().player == null) return null
        return (round(MinecraftClient.getInstance().player?.x!! / 8.0 * 10) / 10.0).toString() + " " + (round(MinecraftClient.getInstance().player?.y!! * 10) / 10.0) + " " + (round(MinecraftClient.getInstance().player?.z!! / 8.0 * 10) / 10.0)
    }
}

class InformationRotation : Information("Player", "Rotation") {
    override fun getMessage(): String? {
        if (MinecraftClient.getInstance().player == null) return null
        return (round(MinecraftClient.getInstance().player?.yaw!! * 10) / 10.0).toString() + " " + (round(MinecraftClient.getInstance().player?.pitch!! * 10) / 10.0)
    }
}

class InformationFakeRotation : Information("Player", "Fake Rotation") {
    override fun getMessage(): String? {
        if (RotationUtil.fakeRotation == null) return null
        return (round(RotationUtil.fakeRotation?.yaw!! * 10) / 10.0).toString() + " " + (round(RotationUtil.fakeRotation?.pitch!! * 10) / 10.0)
    }
}