package de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.api.rewriter.LegacyItemRewriter;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;
import de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1.rewriter.ItemRewriter;
import de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1.storage.PlayerAirTimeStorage;
import de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1.task.PlayerAirTimeUpdateTask;
import de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1.type.Typeb1_8_0_1;
import de.florianmichael.viabeta.protocol.protocol1_1to1_0_0_1.ClientboundPackets1_0;
import de.florianmichael.viabeta.protocol.protocol1_1to1_0_0_1.ServerboundPackets1_0;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.type.Type1_2_4_5;
import de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2.type.Type1_4_2;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;

public class Protocol1_0_0_1tob1_8_0_1 extends AbstractProtocol<ClientboundPacketsb1_8, ClientboundPackets1_0, ServerboundPacketsb1_8, ServerboundPackets1_0> {

    private final LegacyItemRewriter<Protocol1_0_0_1tob1_8_0_1> itemRewriter = new ItemRewriter(this);

    public Protocol1_0_0_1tob1_8_0_1() {
        super(ClientboundPacketsb1_8.class, ClientboundPackets1_0.class, ServerboundPacketsb1_8.class, ServerboundPackets1_0.class);
    }

    @Override
    protected void registerPackets() {
        this.itemRewriter.register();

        this.registerClientbound(ClientboundPacketsb1_8.SET_EXPERIENCE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    float experience = (float) wrapper.read(Type.BYTE);
                    final byte experienceLevel = wrapper.read(Type.BYTE);
                    final short experienceTotal = wrapper.read(Type.SHORT);
                    experience = (experience - 1.0f) / (10 * experienceLevel);
                    wrapper.write(Type.FLOAT, experience); // experience bar
                    wrapper.write(Type.SHORT, (short) experienceLevel); // level
                    wrapper.write(Type.SHORT, experienceTotal); // total experience
                });
            }
        });
        this.registerClientbound(ClientboundPacketsb1_8.SET_SLOT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // window id
                map(Type.SHORT); // slot
                map(Type1_4_2.NBTLESS_ITEM, Type1_2_4_5.COMPRESSED_NBT_ITEM); // item
            }
        });
        this.registerClientbound(ClientboundPacketsb1_8.WINDOW_ITEMS, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // window id
                map(Type1_4_2.NBTLESS_ITEM_ARRAY, Type1_2_4_5.COMPRESSED_NBT_ITEM_ARRAY); // item
            }
        });

        this.registerServerbound(ServerboundPackets1_0.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_UBYTE); // position
                map(Type.UNSIGNED_BYTE); // direction
                map(Type1_2_4_5.COMPRESSED_NBT_ITEM, Type1_4_2.NBTLESS_ITEM);
            }
        });
        this.registerServerbound(ServerboundPackets1_0.CLICK_WINDOW, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // window id
                map(Type.SHORT); // slot
                map(Type.BYTE); // button
                map(Type.SHORT); // action
                map(Type.BYTE); // mode
                map(Type1_2_4_5.COMPRESSED_NBT_ITEM, Type1_4_2.NBTLESS_ITEM); // item
            }
        });
        this.registerServerbound(ServerboundPackets1_0.CREATIVE_INVENTORY_ACTION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.SHORT); // slot
                map(Type1_2_4_5.COMPRESSED_NBT_ITEM, Typeb1_8_0_1.CREATIVE_ITEM); // item
                handler(wrapper -> itemRewriter.handleItemToServer(wrapper.get(Typeb1_8_0_1.CREATIVE_ITEM, 0)));
            }
        });
        this.cancelServerbound(ServerboundPackets1_0.CLICK_WINDOW_BUTTON);
    }

    @Override
    public void register(ViaProviders providers) {
        super.register(providers);

        Via.getPlatform().runRepeatingSync(new PlayerAirTimeUpdateTask(), 1L);
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new PreNettySplitter(userConnection, Protocol1_0_0_1tob1_8_0_1.class, ClientboundPacketsb1_8::getPacket));

        userConnection.put(new PlayerAirTimeStorage(userConnection));
    }

    @Override
    public LegacyItemRewriter<Protocol1_0_0_1tob1_8_0_1> getItemRewriter() {
        return this.itemRewriter;
    }
}
