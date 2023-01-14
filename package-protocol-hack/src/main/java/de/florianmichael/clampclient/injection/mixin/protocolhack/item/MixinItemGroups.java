package de.florianmichael.clampclient.injection.mixin.protocolhack.item;

import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.util.VersionListEnum;
import net.minecraft.item.ItemGroups;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemGroups.class)
public class MixinItemGroups {

    @Unique
    private static VersionListEnum protocolhack_version;

    @Redirect(method = "displayParametersMatch", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/featuretoggle/FeatureSet;equals(Ljava/lang/Object;)Z"))
    private static boolean adjustLastVersionMatchCheck(FeatureSet instance, Object o) {
        return instance.equals(o) && protocolhack_version == ViaLoadingBase.getTargetVersion();
    }

    @Inject(method = "updateDisplayParameters", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroups;updateEntries(Lnet/minecraft/resource/featuretoggle/FeatureSet;Z)V", shift = At.Shift.BEFORE))
    private static void trackLastVersion(FeatureSet enabledFeatures, boolean operatorEnabled, CallbackInfoReturnable<Boolean> cir) {
        protocolhack_version = ViaLoadingBase.getTargetVersion();
    }
}
