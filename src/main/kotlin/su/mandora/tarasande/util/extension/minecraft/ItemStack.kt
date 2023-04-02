package su.mandora.tarasande.util.extension.minecraft

import net.minecraft.item.ItemStack

fun ItemStack.safeCount(): Int {
    // I remember dark times, where getting minus items was actually possible
    // I don't know whether these days belong to the past or if it is still possible
    // to this day, I'm pretty sure that by hacking in Items, it will be possible
    // Lets use this function, to make sure, we don't break at such moments

    return if(count <= 0) Int.MAX_VALUE else count
}