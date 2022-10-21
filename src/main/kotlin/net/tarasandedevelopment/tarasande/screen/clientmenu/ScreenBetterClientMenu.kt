package net.tarasandedevelopment.tarasande.screen.clientmenu

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.ElementMenu
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.ElementMenuTitle
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotList
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotListEntry
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotListWidget
import net.tarasandedevelopment.tarasande.util.render.RenderUtil

class ScreenBetterClientMenu(parent: Screen) : ScreenBetterSlotList(46, MinecraftClient.getInstance().textRenderer.fontHeight * 2 + 5) {

    init {
        this.prevScreen = parent
    }

    override fun init() {
        this.provideElements(object : ScreenBetterSlotListWidget.ListProvider {
            override fun get(): List<ScreenBetterSlotListEntry> {
                return TarasandeMain.get().managerClientMenu.list.
                filter {
                        e -> TarasandeMain.get().managerClientMenu.clientMenuCategories.value || e !is ElementMenuTitle
                }.map {
                        e -> EntryClientMenu(e)
                }
            }
        })
        super.init()
        this.addDrawableChild(ButtonWidget(5, this.height - 25, 20, 20, Text.of("<-")) {
            close()
        })
        this.addDrawableChild(ButtonWidget(this.width / 2 - 49, this.height - 27, 98, 20, Text.of("Settings")) {
            MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(this, "Settings", TarasandeMain.get().managerClientMenu))
        })
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        this.renderTitle(matrices, TarasandeMain.get().name.let {
            it[0].uppercaseChar().toString() + it.substring(1) + " Menu"
        })
    }

    class EntryClientMenu(val element: ElementMenu) : ScreenBetterSlotListEntry() {

        override fun dontSelectAnything() = true

        override fun onSingleClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int) {
            super.onSingleClickEntry(mouseX, mouseY, mouseButton)
            MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            element.onClickInternal(mouseButton)
        }

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            matrices.push()
            matrices.scale(element.elementTextSize(), element.elementTextSize(), element.elementTextSize())
            RenderUtil.textCenter(matrices, element.name, (entryWidth.toFloat() / 2F) / element.elementTextSize(), ((entryHeight / 2F) / element.elementTextSize()) - (MinecraftClient.getInstance().textRenderer.fontHeight / 2F), this.element.elementColor())
            matrices.pop()
        }
    }
}
