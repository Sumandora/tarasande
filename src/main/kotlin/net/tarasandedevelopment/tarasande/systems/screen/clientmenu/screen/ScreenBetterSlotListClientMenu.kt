package net.tarasandedevelopment.tarasande.systems.screen.clientmenu.screen

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotList
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotListEntry
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotListWidget
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.ElementCategory
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.ElementMenu
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import java.awt.Color

class ScreenBetterSlotListClientMenu(parent: Screen) : ScreenBetterSlotList(46, FontWrapper.fontHeight() * 2 + 5) {

    init {
        this.prevScreen = parent
    }

    override fun init() {
        this.provideElements(object : ScreenBetterSlotListWidget.ListProvider {
            override fun get(): List<ScreenBetterSlotListEntry> {
                val list = mutableListOf<ScreenBetterSlotListEntry>()

                val elements = TarasandeMain.managerClientMenu().list

                for (menu in elements.sortedWith(Comparator.comparing<ElementMenu, Boolean> { it.category != ElementCategory.GENERAL }.thenBy { it.category })) {
                    if (!menu.visible()) continue
                    val titleEntry = ScreenBetterSlotListEntryClientMenuTitle(menu.category)
                    if (!list.contains(titleEntry) && TarasandeMain.instance.clientValues.clientMenuShowCategories.value) {
                        list.add(titleEntry)
                    }
                    list.add(ScreenBetterSlotListEntryClientMenu(menu))
                }

                return list
            }
        })
        super.init()
        if (TarasandeMain.instance.clientValues.clientMenuBackButtons.value) {
            addDrawableChild(ButtonWidget(5, this.height - 25, 20, 20, Text.of("<-")) { RenderSystem.recordRenderCall { close() } })
        }
        this.addDrawableChild(ButtonWidget(this.width / 2 - 49, this.height - 27, 98, 20, Text.of("Client values")) {
            MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(this, "Client values", TarasandeMain.instance.clientValues))
        })
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        this.renderTitle(matrices, TarasandeMain.instance.name.let {
            it[0].uppercaseChar().toString() + it.substring(1) + " Menu"
        })
    }

    class ScreenBetterSlotListEntryClientMenu(private val element: ElementMenu) : ScreenBetterSlotListEntry() {

        override fun dontSelectAnything() = true

        override fun onSingleClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int) {
            super.onSingleClickEntry(mouseX, mouseY, mouseButton)
            MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            element.onClickInternal(mouseButton)
        }

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            FontWrapper.textShadow(matrices, element.name, entryWidth.toFloat() / 2F, entryHeight / 2 - (FontWrapper.fontHeight() / 2F), this.element.elementColor(), centered = true)
        }
    }

    @Suppress("EqualsOrHashCode")
    class ScreenBetterSlotListEntryClientMenuTitle(val title: String) : ScreenBetterSlotListEntry() {

        override fun dontSelectAnything() = true

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            FontWrapper.textShadow(matrices, title, entryWidth.toFloat() / 2F, entryHeight / 2 - (FontWrapper.fontHeight() / 2F), Color.gray.rgb, scale = 1.5F, centered = true)
        }

        override fun equals(other: Any?): Boolean {
            return other is ScreenBetterSlotListEntryClientMenuTitle && other.title == title
        }
    }
}
