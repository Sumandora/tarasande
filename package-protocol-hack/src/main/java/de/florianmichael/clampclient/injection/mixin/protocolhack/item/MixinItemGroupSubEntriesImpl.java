package de.florianmichael.clampclient.injection.mixin.protocolhack.item;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.item.Item;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.tarasandedevelopment.tarasande_protocol_hack.platform.ProtocolHackValues;
import net.tarasandedevelopment.tarasande_protocol_hack.util.inventory.ItemSplitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.item.ItemGroup$EntriesImpl")
public class MixinItemGroupSubEntriesImpl {

    @Redirect(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isEnabled(Lnet/minecraft/resource/featuretoggle/FeatureSet;)Z"))
    public boolean removeUnknownItems(Item instance, FeatureSet featureSet) {
        if (ItemSplitter.INSTANCE.getSplitter().containsKey(instance) && ProtocolHackValues.INSTANCE.getFilterItemGroups().getValue()) {
            if (VersionList.isOlderOrEqualTo(ItemSplitter.INSTANCE.getSplitter().get(instance))) {
                return false;
            }
        }
        return instance.isEnabled(featureSet);
    }
}
