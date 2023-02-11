package de.florianmichael.viasnapshot.protocol.protocol1_14to3D_Shareware.packets;

import com.viaversion.viabackwards.api.rewriters.ItemRewriter;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import de.florianmichael.viasnapshot.protocol.protocol1_14to3D_Shareware.ClientboundPackets3D_Shareware;
import de.florianmichael.viasnapshot.protocol.protocol1_14to3D_Shareware.Protocol1_14to3D_Shareware;

public class BlockItemPackets3D_Shareware extends ItemRewriter<ClientboundPackets3D_Shareware, ServerboundPackets1_14, Protocol1_14to3D_Shareware> {

    public BlockItemPackets3D_Shareware(Protocol1_14to3D_Shareware protocol) {
        super(protocol);
    }

    @Override
    protected void registerPackets() {
        this.registerSetCooldown(ClientboundPackets3D_Shareware.COOLDOWN);
        this.registerWindowItems(ClientboundPackets3D_Shareware.WINDOW_ITEMS, Type.FLAT_VAR_INT_ITEM_ARRAY);
        this.registerSetSlot(ClientboundPackets3D_Shareware.SET_SLOT, Type.FLAT_VAR_INT_ITEM);
        this.registerEntityEquipment(ClientboundPackets3D_Shareware.ENTITY_EQUIPMENT, Type.FLAT_VAR_INT_ITEM);
        this.registerAdvancements(ClientboundPackets3D_Shareware.ADVANCEMENTS, Type.FLAT_VAR_INT_ITEM);
        this.registerClickWindow(ServerboundPackets1_14.CLICK_WINDOW, Type.FLAT_VAR_INT_ITEM);
        this.registerCreativeInvAction(ServerboundPackets1_14.CREATIVE_INVENTORY_ACTION, Type.FLAT_VAR_INT_ITEM);
        this.registerSpawnParticle(ClientboundPackets3D_Shareware.SPAWN_PARTICLE, Type.FLAT_VAR_INT_ITEM, Type.FLOAT);

        this.protocol.registerClientbound(ClientboundPackets3D_Shareware.TRADE_LIST, new PacketRemapper() {
            public void registerMap() {
                this.handler((wrapper) -> {
                    wrapper.passthrough(Type.VAR_INT);
                    int size = wrapper.passthrough(Type.UNSIGNED_BYTE);

                    for (int i = 0; i < size; ++i) {
                        BlockItemPackets3D_Shareware.this.handleItemToClient(wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
                        BlockItemPackets3D_Shareware.this.handleItemToClient(wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
                        if (wrapper.passthrough(Type.BOOLEAN)) {
                            BlockItemPackets3D_Shareware.this.handleItemToClient(wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
                        }

                        wrapper.passthrough(Type.BOOLEAN);
                        wrapper.passthrough(Type.INT);
                        wrapper.passthrough(Type.INT);
                        wrapper.passthrough(Type.INT);
                        wrapper.passthrough(Type.INT);
                        wrapper.passthrough(Type.FLOAT);
                    }

                });
            }
        });
    }

}
