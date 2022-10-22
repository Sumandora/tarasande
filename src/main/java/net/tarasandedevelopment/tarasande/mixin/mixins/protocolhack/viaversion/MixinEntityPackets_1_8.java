package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.viaversion;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ItemRewriter;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.packets.EntityPackets;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantConditions")
@Mixin(EntityPackets.class)
public class MixinEntityPackets_1_8 {

    @Unique
    private final static ValueTransformer<Short, Integer> transformer = new ValueTransformer<>(Type.VAR_INT) {
        @Override
        public Integer transform(PacketWrapper wrapper, Short slot) throws Exception {
            int entityId = wrapper.get(Type.VAR_INT, 0);
            int receiverId = wrapper.user().getEntityTracker(Protocol1_9To1_8.class).clientEntityId();
            // Normally, 0 = hand and 1-4 = armor
            // ... but if the sent id is equal to the receiver's id, 0-3 will instead mark the armor slots
            // (In 1.9+, every client treats the received the same: 0=hand, 1=offhand, 2-5=armor)
            if (entityId == receiverId) {
                return slot.intValue() + 2;
            }
            return slot > 0 ? slot.intValue() + 1 : slot.intValue();
        }
    };

    @Inject(method = "register", at = @At("RETURN"), remap = false)
    private static void fixEntityEquipment(Protocol1_9To1_8 protocol, CallbackInfo ci) {
        protocol.registerClientbound(ClientboundPackets1_8.ENTITY_EQUIPMENT, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                // 1 - Slot ID
                map(Type.SHORT, transformer);

                // Checks if the packet is valid @author FlorianMichael
                handler((pw) -> {
                    final int slotId = pw.get(Type.VAR_INT, 1);
                    if (slotId < 0 || slotId >= EquipmentSlot.values().length) {
                        pw.cancel();
                    }
                });

                map(Type.ITEM); // 2 - Item
                // Item Rewriter
                handler(wrapper -> {
                    Item stack = wrapper.get(Type.ITEM, 0);
                    ItemRewriter.toClient(stack);
                });
                // Blocking
                handler(wrapper -> {
                    EntityTracker1_9 entityTracker = wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    int entityID = wrapper.get(Type.VAR_INT, 0);
                    Item stack = wrapper.get(Type.ITEM, 0);

                    if (stack != null) {
                        if (Protocol1_9To1_8.isSword(stack.identifier())) {
                            entityTracker.getValidBlocking().add(entityID);
                            return;
                        }
                    }
                    entityTracker.getValidBlocking().remove(entityID);
                });
            }
        });
    }
}
