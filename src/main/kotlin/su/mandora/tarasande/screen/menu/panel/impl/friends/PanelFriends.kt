package su.mandora.tarasande.screen.menu.panel.impl.friends

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.screen.menu.panel.Panel
import su.mandora.tarasande.util.render.RenderUtil

class PanelFriends(x: Double, y: Double) : Panel("Friends", x, y, 150.0, 100.0) {

    private val elementPlayerList = ArrayList<ElementPlayer>()

    override fun init() {
        for (it in elementPlayerList) {
            it.init()
        }
    }

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        matrices?.push()
        matrices?.translate(x + 2, y + MinecraftClient.getInstance().textRenderer.fontHeight + 2, 0.0)
        val x = x + 2
        var y = y + MinecraftClient.getInstance().textRenderer.fontHeight + 2
        for (it in elementPlayerList) {
            it.width = panelWidth - 4
            if (y + it.getHeight() + 2 >= this.y - scrollOffset) it.render(matrices, (mouseX - x).toInt(), (mouseY - y - scrollOffset).toInt(), delta)

            matrices?.translate(0.0, it.getHeight() + 2, 0.0)
            y += it.getHeight() + 2

            if (y > this.y - scrollOffset + panelHeight) break
        }
        matrices?.pop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val hovered = RenderUtil.isHovered(mouseX, mouseY, x, y + MinecraftClient.getInstance().textRenderer.fontHeight.toDouble(), x + panelWidth, y + panelHeight)
        val x = x + 2
        var y = y + MinecraftClient.getInstance().textRenderer.fontHeight + 2
        elementPlayerList.forEach {
            it.mouseClicked(if (hovered) mouseX - x else -1.0, if (hovered) mouseY - y - scrollOffset else -1.0, button)
            y += it.getHeight() + 2
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        val x = x + 2
        var y = y + MinecraftClient.getInstance().textRenderer.fontHeight + 2
        elementPlayerList.forEach {
            it.mouseReleased(mouseX - x, mouseY - y - scrollOffset, button)
            y += it.getHeight() + 2
        }
        super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        val x = x + 2
        var y = y + MinecraftClient.getInstance().textRenderer.fontHeight + 2
        elementPlayerList.forEach {
            if (it.mouseScrolled(mouseX - x, mouseY - y - scrollOffset, amount)) return true
            y += it.getHeight() + 2
        }
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        elementPlayerList.forEach {
            if (it.keyPressed(keyCode, scanCode, modifiers)) return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun charTyped(chr: Char, modifiers: Int) {
        elementPlayerList.forEach { it.charTyped(chr, modifiers) }
        super.charTyped(chr, modifiers)
    }

    override fun tick() {
        elementPlayerList.removeIf {
            if (MinecraftClient.getInstance().networkHandler?.playerList?.none { p -> p.profile == it.gameProfile }!!) {
                it.onClose()
                return@removeIf true
            }
            false
        }
        for (player in MinecraftClient.getInstance().networkHandler?.playerList!!) {
            if (player != null && player.profile != MinecraftClient.getInstance().player?.gameProfile && elementPlayerList.none { it.gameProfile == player.profile } && player.profile.name.isNotEmpty()) {
                val elementPlayer = ElementPlayer(player.profile, 0.0)
                elementPlayer.init()
                elementPlayerList.add(elementPlayer)
            }
        }
        elementPlayerList.sortBy { TarasandeMain.get().friends?.isFriend(it.gameProfile) != true } // friends to top
        elementPlayerList.forEach { it.tick() }
        super.tick()
    }

    override fun onClose() {
        elementPlayerList.forEach { it.onClose() }
        super.onClose()
    }

    override fun getMaxScrollOffset(): Double {
        var height = 0.0
        elementPlayerList.forEach { height += it.getHeight() + 2 }
        return if (height > 0.0) height - 2
        else 0.0
    }
}