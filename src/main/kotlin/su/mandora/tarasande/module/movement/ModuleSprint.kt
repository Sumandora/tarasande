package su.mandora.tarasande.module.movement

import net.minecraft.block.AirBlock
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.item.Item
import net.minecraft.item.Items
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.value.*
import java.util.function.Consumer

class ModuleSprint : Module("Sprint", "Automatically sprints", ModuleCategory.MOVEMENT) {

    val block = object : ValueBlock(this, "Block Value", Blocks.DIRT, Blocks.COBBLESTONE) {
        override fun filter(block: Block): Boolean {
            return block !is AirBlock
        }
    }
    val boolean = ValueBoolean(this, "Boolean Value", false)
    val color = ValueColor(this, "Color Value", 0.0F, 1.0F, 1.0F, -1.0F)
    val keyBindValue = ValueKeyBind(this, "KeyBind Value", GLFW.GLFW_KEY_G)
    val mode = ValueMode(this, "Mode Value", true, "Setting 1", "Setting 2", "Setting 3")
    val number = object : ValueNumber(this, "Number Value", 0.0, 1.0, 5.0, 0.3) {
        override fun isVisible(): Boolean {
            return boolean.value
        }
    }
    val numberRange = ValueNumberRange(this, "Number Range Value", 0.0, 0.25, 0.75, 1.0, 0.15)
    val text = ValueText(this, "Text Value", "Yoooo!")
    val item = object : ValueItem(this, "Item Value", Items.DIAMOND_SWORD, Items.DIRT) {
        override fun filter(item: Item): Boolean {
            return item != Items.BARRIER
        }
    }

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventKeyBindingIsPressed && event.keyBinding == mc.options?.keySprint) {
            event.pressed = true
        }
    }

}