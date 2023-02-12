package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.sidebar

import net.minecraft.client.gui.screen.ConfirmScreen
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.injection.accessor.IConfirmScreen
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
    private val sendHTTPRequest = ValueBoolean(this, "Send HTTP request", true, isEnabled = { mode.isSelected(0) })
    private val spoofHTTPRequestResponse = ValueBoolean(this, "Spoof HTTP request response", false, isEnabled = { sendHTTPRequest.value })
    private val acceptMode = ValueMode(this, "Accept mode", false, "Successful loaded", "Fail download", isEnabled = { !sendHTTPRequest.isEnabled() || spoofHTTPRequestResponse.value })

    private var lastUrl: String? = null
    private var lastSHA1: String? = null

    init {
        EventDispatcher.add(EventPacket::class.java) {
            if (it.type == EventPacket.Type.RECEIVE && it.packet is ResourcePackSendS2CPacket) {
                lastUrl = it.packet.url
                lastSHA1 = it.packet.shA1

                if (enabled.value) {
                    sendPackets()
                    it.cancelled = true
                }
            }
        }

        ManagerScreenExtension.add(object : ScreenExtensionButtonList<ConfirmScreen>(ConfirmScreen::class.java) {
            init {
                add("Spoof resource pack", visible = { return@add (mc.currentScreen as IConfirmScreen).tarasande_isResourcePacksScreen() }) {
                    sendPackets()
                }
            }
        })
    }

    private fun sendPackets() {
        val url = ClientPlayNetworkHandler.resolveUrl(lastUrl)

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
                        mc.serverResourcePackProvider.download(url, lastSHA1, true)
                        when {
                            acceptMode.isSelected(0) -> mc.networkHandler?.sendPacket(ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED))
                            acceptMode.isSelected(1) -> mc.networkHandler?.sendPacket(ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD))
                        }
                    } else {
                        mc.networkHandler?.feedbackAfterDownload(mc.serverResourcePackProvider.download(url, lastSHA1, true))
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
