package su.mandora.tarasande.screen.menu.panel.impl.friends

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.screen.menu.panel.Panel
import su.mandora.tarasande.util.render.RenderUtil

class PanelFriends(x: Double, y: Double) : Panel("Friends", x, y, 150.0, 100.0) {

    private val playerElementList = ArrayList<PlayerElement>()

    override fun init() {
        for (it in playerElementList) {
            it.init()
        }
    }

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        matrices?.push()
        matrices?.translate(x + 2, y + MinecraftClient.getInstance().textRenderer.fontHeight + 2, 0.0)
        val x = x + 2
        var y = y + MinecraftClient.getInstance().textRenderer.fontHeight + 2
        for (it in playerElementList) {
            it.width = panelWidth - 4
            if (y + it.getHeight() + 2 >= this.y - scrollOffset)
                it.render(matrices, (mouseX - x).toInt(), (mouseY - y - scrollOffset).toInt(), delta)

            matrices?.translate(0.0, it.getHeight() + 2, 0.0)
            y += it.getHeight() + 2

            if (y > this.y - scrollOffset + panelHeight)
                break
        }
        matrices?.pop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val hovered = RenderUtil.isHovered(mouseX, mouseY, x, y + MinecraftClient.getInstance().textRenderer.fontHeight.toDouble(), x + panelWidth, y + panelHeight)
        val x = x + 2
        var y = y + MinecraftClient.getInstance().textRenderer.fontHeight + 2
        playerElementList.forEach {
            it.mouseClicked(if (hovered) mouseX - x else -1.0, if (hovered) mouseY - y - scrollOffset else -1.0, button)
            y += it.getHeight() + 2
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        val x = x + 2
        var y = y + MinecraftClient.getInstance().textRenderer.fontHeight + 2
        playerElementList.forEach {
            it.mouseReleased(mouseX - x, mouseY - y - scrollOffset, button)
            y += it.getHeight() + 2
        }
        super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        val x = x + 2
        var y = y + MinecraftClient.getInstance().textRenderer.fontHeight + 2
        playerElementList.forEach {
            if (it.mouseScrolled(mouseX - x, mouseY - y - scrollOffset, amount))
                return true
            y += it.getHeight() + 2
        }
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        playerElementList.forEach {
            if (it.keyPressed(keyCode, scanCode, modifiers))
                return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun charTyped(chr: Char, modifiers: Int) {
        playerElementList.forEach { it.charTyped(chr, modifiers) }
        super.charTyped(chr, modifiers)
    }

    override fun tick() {
        playerElementList.removeIf {
            if (!MinecraftClient.getInstance().world?.players?.contains(it.player)!!) {
                it.onClose()
                return@removeIf true
            }
            false
        }
        for (player in MinecraftClient.getInstance().world?.players!!) {
            if (player.gameProfile != null && player != MinecraftClient.getInstance().player && playerElementList.none { it.player == player }) {
                val playerElement = PlayerElement(player, 0.0)
                playerElement.init()
                playerElementList.add(playerElement)
            }
        }


        playerElementList.forEach { it.tick() }
        super.tick()
    }

    override fun onClose() {
        playerElementList.forEach { it.onClose() }
        super.onClose()
    }

    override fun getMaxScrollOffset(): Double {
        var height = 0.0
        playerElementList.forEach { height += it.getHeight() + 2 }
        return if (height > 0.0)
            height - 2
        else
            0.0
    }
}