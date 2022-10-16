package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.viaversion;

import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets.InventoryPackets;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.storage.EntityTracker1_14;
import com.viaversion.viaversion.rewriter.ItemRewriter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryPackets.class)
public class MixinInventoryPackets1_14 extends ItemRewriter<Protocol1_14To1_13_2> {

    protected MixinInventoryPackets1_14(Protocol1_14To1_13_2 protocol) {
        super(protocol);
    }

    @Inject(method = "registerPackets", at = @At("RETURN"))
    public void fixTradeListRemapping(CallbackInfo ci) {
        protocol.registerClientbound(ClientboundPackets1_13.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // Channel
                handler(wrapper -> {
                    String channel = wrapper.get(Type.STRING, 0);
                    if (channel.equals("minecraft:trader_list") || channel.equals("trader_list")) {
                        wrapper.setId(0x27);
                        wrapper.resetReader();
                        wrapper.read(Type.STRING); // Remove channel

                        int windowId = wrapper.read(Type.INT);
                        EntityTracker1_14 tracker = wrapper.user().getEntityTracker(Protocol1_14To1_13_2.class);
                        tracker.setLatestTradeWindowId(windowId);
                        wrapper.write(Type.VAR_INT, windowId);

                        int size = wrapper.passthrough(Type.UNSIGNED_BYTE);
                        for (int i = 0; i < size; i++) {
                            // Input Item
                            handleItemToClient(wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
                            // Output Item
                            handleItemToClient(wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));

                            boolean secondItem = wrapper.passthrough(Type.BOOLEAN); // Has second item
                            if (secondItem) {
                                // Second Item
                                handleItemToClient(wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
                            }

                            wrapper.passthrough(Type.BOOLEAN); // Trade disabled
                            wrapper.passthrough(Type.INT); // Number of tools uses
                            wrapper.passthrough(Type.INT); // Maximum number of trade uses

                            wrapper.clearInputBuffer();

                            wrapper.write(Type.INT, 0);
                            wrapper.write(Type.INT, 0);
                            wrapper.write(Type.FLOAT, 0f);
                        }
                        wrapper.write(Type.VAR_INT, 0);
                        wrapper.write(Type.VAR_INT, 0);
                        wrapper.write(Type.BOOLEAN, false);
                    } else if (channel.equals("minecraft:book_open") || channel.equals("book_open")) {
                        int hand = wrapper.read(Type.VAR_INT);
                        wrapper.clearPacket();
                        wrapper.setId(0x2D);
                        wrapper.write(Type.VAR_INT, hand);
                    }
                });
            }
        });
    }
}
