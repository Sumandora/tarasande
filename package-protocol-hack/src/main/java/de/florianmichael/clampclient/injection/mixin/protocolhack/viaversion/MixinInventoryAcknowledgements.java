package de.florianmichael.clampclient.injection.mixin.protocolhack.viaversion;

import com.viaversion.viaversion.libs.fastutil.ints.IntArrayList;
import com.viaversion.viaversion.libs.fastutil.ints.IntList;
import com.viaversion.viaversion.libs.fastutil.ints.IntLists;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.storage.InventoryAcknowledgements;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryAcknowledgements.class)
public class MixinInventoryAcknowledgements {

    @Mutable
    @Shadow @Final private IntList ids;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void fixJavaIssue(CallbackInfo ci) {
        this.ids = IntLists.synchronize(new IntArrayList());
    }
}
