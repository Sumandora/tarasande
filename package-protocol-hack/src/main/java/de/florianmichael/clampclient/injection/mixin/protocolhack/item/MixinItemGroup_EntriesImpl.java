package de.florianmichael.clampclient.injection.mixin.protocolhack.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.resource.featuretoggle.FeatureSet;
import de.florianmichael.tarasande_protocol_hack.TarasandeProtocolHack;
import de.florianmichael.tarasande_protocol_hack.util.values.ProtocolHackValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.item.ItemGroup$EntriesImpl")
public class MixinItemGroup_EntriesImpl {

    @Redirect(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isEnabled(Lnet/minecraft/resource/featuretoggle/FeatureSet;)Z"))
    public boolean removeUnknownItems(Item instance, FeatureSet featureSet) {
        if ((TarasandeProtocolHack.Companion.getDisplayItems().contains(instance) && ProtocolHackValues.INSTANCE.getFilterItemGroups().getValue()) || MinecraftClient.getInstance().isInSingleplayer()) {
            return instance.isEnabled(featureSet);
        }
        return false;
    }
}
