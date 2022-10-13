package net.tarasandedevelopment.tarasande.screen.cheatmenu.command

import net.minecraft.client.MinecraftClient
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import net.minecraft.util.Hand
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.command.Command
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.elements.impl.terminal.PanelElementsTerminal

// 2b2t Dupe Glitch
class CommandCowDupe : Command("cowdupe") {

    override fun execute(args: Array<String>, panel: PanelElementsTerminal): Boolean {
        if (MinecraftClient.getInstance().player!!.inventory.mainHandStack.isOf(Items.SHEARS)) {
            for (i in 0..150) {
                if (MinecraftClient.getInstance().targetedEntity != null) {
                    MinecraftClient.getInstance().networkHandler?.sendPacket(PlayerInteractEntityC2SPacket.interact(MinecraftClient.getInstance().targetedEntity, MinecraftClient.getInstance().player!!.isSneaking, Hand.MAIN_HAND))
                    panel.add("Finished shearing targeted entity.")
                }
            }
        } else panel.add("You need to hold shears to do the glitch.")
        return true
    }
}