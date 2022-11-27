package net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.inventory

import com.google.common.collect.Lists
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.LecternScreen
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket
import net.minecraft.screen.slot.SlotActionType
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.ScreenExtensionButton

class ScreenExtensionButtonLecternCrash : ScreenExtensionButton<LecternScreen>("Lectern Crash", LecternScreen::class.java) {

    override fun onClick(current: LecternScreen) {
        val screenHandler = MinecraftClient.getInstance().player!!.currentScreenHandler

        val defaultedList = screenHandler.slots
        val i = defaultedList.size

        val list = Lists.newArrayListWithCapacity<ItemStack>(i)

        for (slot in defaultedList) {
            list.add(slot.stack.copy())
        }

        val int2ObjectMap = Int2ObjectOpenHashMap<ItemStack>()

        for (slot2 in 0 until i) {
            val stack = list[slot2]
            val stackCopy = defaultedList[slot2].stack

            if (!ItemStack.areEqual(stack, stackCopy))
                int2ObjectMap[slot2] = stackCopy.copy()
        }

        MinecraftClient.getInstance().networkHandler!!.connection.send(ClickSlotC2SPacket(screenHandler.syncId, screenHandler.revision, 0, 0, SlotActionType.QUICK_MOVE, screenHandler.cursorStack.copy(), int2ObjectMap))
    }
}