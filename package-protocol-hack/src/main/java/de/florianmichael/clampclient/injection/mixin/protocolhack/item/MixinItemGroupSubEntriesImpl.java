package de.florianmichael.clampclient.injection.mixin.protocolhack.item;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net.minecraft.item.ItemGroup$EntriesImpl")
public class MixinItemGroupSubEntriesImpl {

    // TODO | Recode to VersionListEnum

//    @Redirect(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isEnabled(Lnet/minecraft/resource/featuretoggle/FeatureSet;)Z"))
//    public boolean removeUnknownItems(Item instance, FeatureSet featureSet) {
//        if (ItemSplitter.INSTANCE.getSplitter().containsKey(instance) && ProtocolHackValues.INSTANCE.getFilterItemGroups().getValue()) {
//            if (VersionList.isOlderOrEqualTo(ItemSplitter.INSTANCE.getSplitter().get(instance))) {
//                return false;
//            }
//        }
//        return instance.isEnabled(featureSet);
//    }
}
