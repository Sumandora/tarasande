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

package de.florianmichael.clampclient.injection.mixin.protocolhack;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.tarasandedevelopment.tarasande_protocol_hack.platform.ProtocolHackValues;
import net.tarasandedevelopment.tarasande_protocol_hack.util.inventory.ItemSplitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

@Mixin(ItemGroup.class)
public class MixinItemGroup {

    @Shadow private Collection<ItemStack> displayStacks;

    @Redirect(method = "updateEntries", at = @At(value = "FIELD", target = "Lnet/minecraft/item/ItemGroup;displayStacks:Ljava/util/Collection;"))
    public void filterItems(ItemGroup instance, Collection<ItemStack> value) {
        displayStacks = value;

        for (ItemStack itemStack : value) {
            if (ItemSplitter.INSTANCE.getSplitter().containsKey(instance) && ProtocolHackValues.INSTANCE.getFilterItemGroups().getValue()) {
                final ProtocolVersion version = ItemSplitter.INSTANCE.getSplitter().get(instance);

                if (VersionList.isOlderOrEqualTo(version)) {
                    displayStacks.remove(itemStack);
                }
                return;
            }

        }
    }
}
