package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.sidebar

import net.minecraft.client.gui.screen.ConfirmScreen
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.injection.accessor.IServerResourcePackProvider
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.SidebarEntryToggleable
import su.mandora.event.EventDispatcher

class SidebarEntryToggleableResourcePackSpoofer : SidebarEntryToggleable("Resource pack spoofer", "Spoofer") {

    private val ignoreInvalidProtocol = ValueMode(this, "Ignore invalid protocol", false, "Ignore", "Ignore and send response")
    private val mode = ValueMode(this, "Mode", false, "Accept", "Decline")
    private val sendHTTPRequest = object : ValueBoolean(this, "Send HTTP request", true) {
        override fun isEnabled() = mode.isSelected(0)
    }
    private val spoofHTTPRequestResponse = object : ValueBoolean(this, "Spoof HTTP request response", false) {
        override fun isEnabled() = sendHTTPRequest.value
    }
    private val acceptMode = object : ValueMode(this, "Accept mode", false, "Successful loaded", "Fail download") {
        override fun isEnabled() = !sendHTTPRequest.isEnabled() || spoofHTTPRequestResponse.value
    }

    init {
        EventDispatcher.add(EventPacket::class.java) {
            if (it.type == EventPacket.Type.RECEIVE && it.packet is ResourcePackSendS2CPacket) {
                sendPackets(it.packet.url, it.packet.shA1)
                it.cancelled = true
            }
        }
    }

    private fun sendPackets(url: String, sha1: String) {
        val url = ClientPlayNetworkHandler.resolveUrl(url)

        if (ignoreInvalidProtocol.anySelected()) {
            if (url == null) {
                if (ignoreInvalidProtocol.isSelected(1)) {
                    mc.networkHandler?.sendPacket(ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD))
                }
                return
            }
        }

        when {
            mode.isSelected(0) -> {
                mc.networkHandler?.sendPacket(ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.ACCEPTED))
                if (sendHTTPRequest.value) {
                    (mc.serverResourcePackProvider as IServerResourcePackProvider).tarasande_setSpoofLoading(true)
                    if (spoofHTTPRequestResponse.value) {
                        mc.serverResourcePackProvider.download(url, sha1, true)
                        when {
                            acceptMode.isSelected(0) -> mc.networkHandler?.sendPacket(ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED))
                            acceptMode.isSelected(1) -> mc.networkHandler?.sendPacket(ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD))
                        }
                    } else {
                        mc.networkHandler?.feedbackAfterDownload(mc.serverResourcePackProvider.download(url, sha1, true))
                    }
                    (mc.serverResourcePackProvider as IServerResourcePackProvider).tarasande_setSpoofLoading(false)
                } else {
                    when {
                        acceptMode.isSelected(0) -> mc.networkHandler?.sendPacket(ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED))
                        acceptMode.isSelected(1) -> mc.networkHandler?.sendPacket(ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD))
                    }
                }
            }

            mode.isSelected(1) -> mc.networkHandler?.sendPacket(ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.DECLINED))
        }
    }
}
