package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.viaversion;

import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets.InventoryPackets;
import com.viaversion.viaversion.rewriter.ItemRewriter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryPackets.class)
public class MixinInventoryPackets1_13_2 extends ItemRewriter<Protocol1_14To1_13_2> {

    protected MixinInventoryPackets1_13_2(Protocol1_14To1_13_2 protocol) {
        super(protocol);
    }

    @Inject(method = "registerPackets", at = @At("TAIL"))
    public void removeUnnecessaryHandler(CallbackInfo ci) {
        protocol.registerServerbound(ServerboundPackets1_14.SELECT_TRADE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);
            }
        });
    }

}
