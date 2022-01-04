package su.mandora.tarasande.base.screen.wheel.wheeltree

import net.minecraft.client.MinecraftClient
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.Manager
import su.mandora.tarasande.base.value.Value
import su.mandora.tarasande.mixin.accessor.IVec3d
import su.mandora.tarasande.parkourbot.pathbuilder.Goal
import su.mandora.tarasande.screen.menu.utils.IElement
import su.mandora.tarasande.screen.menu.valuecomponent.*
import su.mandora.tarasande.value.*

class ManagerWheelTree : Manager<WheelTreeEntry>() {

    init {
        add(
            WheelTreeSubMenu("Parkour Bot", arrayOf(
                WheelTreeSubMenu("Start", arrayOf(
                    WheelTreeRunnable("Forwards") {
                        TarasandeMain.get().parkourBot?.start(Goal.FORWARDS)
                    },
                    WheelTreeRunnable("Upwards") {
                        TarasandeMain.get().parkourBot?.start(Goal.UPWARDS)
                    },
                    WheelTreeRunnable("Downwards") {
                        TarasandeMain.get().parkourBot?.start(Goal.DOWNWARDS)
                    }
                )),
                WheelTreeRunnable("Stop") {
                    TarasandeMain.get().parkourBot?.stop()
                },
            )),
            WheelTreeSubMenu("Cool SubMenu", arrayOf(
                WheelTreeRunnable("Console Log") {
                    println("Hello World!")
                },
                WheelTreeRunnable("Funny Motion") {
                    (MinecraftClient.getInstance().player?.velocity as IVec3d).setY(10.0)
                },
            )),
            WheelTreeRunnable("Something else") {
                println("Really something else")
            }
        )
    }

    fun getEntries(wheelTreeEntry: WheelTreeEntry?): Array<WheelTreeEntry>? {
        if(wheelTreeEntry == null) return list.toTypedArray()
        return iterateEntries(list.toTypedArray(), wheelTreeEntry)
    }

    private fun iterateEntries(wheelTreeEntries: Array<WheelTreeEntry>, searchedItem: WheelTreeEntry): Array<WheelTreeEntry>? {
        for(wheelTreeEntry in wheelTreeEntries) {
            if(searchedItem == wheelTreeEntry) return (wheelTreeEntry as WheelTreeSubMenu).subMenus
            if(wheelTreeEntry is WheelTreeSubMenu) {
                val subMenuEntries = iterateEntries(wheelTreeEntry.subMenus, searchedItem)
                if(subMenuEntries != null) return subMenuEntries
            }
        }
        return null
    }

}

open class WheelTreeEntry(val name: String)
class WheelTreeSubMenu(name: String, val subMenus: Array<WheelTreeEntry>) : WheelTreeEntry(name)
class WheelTreeRunnable(name: String, val runnable: Runnable) : WheelTreeEntry(name)