package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl

import io.netty.buffer.Unpooled
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.impl.button.PanelButton
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.EntrySidebarPanelToggleable
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionSidebar
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.accountmanager.ScreenBetterSlotListAccountManager
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.ManagerEntrySidebarPanel
import su.mandora.event.EventDispatcher

class EntrySidebarPanelToggleableClientBrandSpoofer(sidebar: ManagerEntrySidebarPanel) : EntrySidebarPanelToggleable(sidebar, "Client brand spoofer", "Spoofer") {
    private val clientBrand = ValueText(this, "Client brand", "vanilla")

    init {
        EventDispatcher.add(EventPacket::class.java) {
            if (!state.value) return@add

            if (it.type == EventPacket.Type.SEND && it.packet is CustomPayloadC2SPacket) {
                if (it.packet.channel == CustomPayloadC2SPacket.BRAND) {
                    it.packet.data = PacketByteBuf(Unpooled.buffer()).writeString(clientBrand.value)
                }
            }
        }
    }
}

class ScreenExtensionSidebarMultiplayerScreen : ScreenExtensionSidebar<MultiplayerScreen>(MultiplayerScreen::class.java) {

    val screenBetterSlotListAccountManager = ScreenBetterSlotListAccountManager()

    init {
        sidebar.apply {
            add(
                EntrySidebarPanelToggleableClientBrandSpoofer(this)
            )
        }
    }

    override fun createElements(screen: MultiplayerScreen): MutableList<Element> {
        return super.createElements(screen).apply {
            this.add(PanelButton.createButton(3, 3, 98, 25, "Account Manager") {
                MinecraftClient.getInstance().setScreen(screenBetterSlotListAccountManager.apply { prevScreen = MinecraftClient.getInstance().currentScreen })
            })
        }
    }
}
