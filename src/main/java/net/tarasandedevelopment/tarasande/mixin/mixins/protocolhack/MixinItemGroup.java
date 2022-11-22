/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.tarasandedevelopment.tarasande.protocolhack.platform.ProtocolHackValues;
import net.tarasandedevelopment.tarasande.protocolhack.util.inventory.ItemSplitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemGroup.class)
public class MixinItemGroup {

    @Redirect(method = "appendStacks", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;appendStacks(Lnet/minecraft/item/ItemGroup;Lnet/minecraft/util/collection/DefaultedList;)V"))
    public void removeUseless(Item instance, ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (ItemSplitter.INSTANCE.getSplitter().containsKey(instance) && ProtocolHackValues.INSTANCE.getFilterItemGroups().getValue()) {
            final ProtocolVersion version = ItemSplitter.INSTANCE.getSplitter().get(instance);

            if (VersionList.isNewerTo(version)) {
                instance.appendStacks(group, stacks);
            }
            return;
        }
        instance.appendStacks(group, stacks); // in case an item has mo napping
    }
}
