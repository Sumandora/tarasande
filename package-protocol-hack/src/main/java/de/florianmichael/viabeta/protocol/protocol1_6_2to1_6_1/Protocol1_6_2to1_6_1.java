package de.florianmichael.viabeta.protocol.protocol1_6_2to1_6_1;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockFace;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.api.data.ItemList1_6;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.ClientboundPackets1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.ServerboundPackets1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.Type1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;

import java.nio.charset.StandardCharsets;

public class Protocol1_6_2to1_6_1 extends AbstractProtocol<ClientboundPackets1_6_1, ClientboundPackets1_6_4, ServerboundPackets1_6_4, ServerboundPackets1_6_4> {

    public Protocol1_6_2to1_6_1() {
        super(ClientboundPackets1_6_1.class, ClientboundPackets1_6_4.class, ServerboundPackets1_6_4.class, ServerboundPackets1_6_4.class);
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(ClientboundPackets1_6_1.JOIN_GAME, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final PacketWrapper brand = PacketWrapper.create(ClientboundPackets1_6_4.PLUGIN_MESSAGE, wrapper.user());
                    brand.write(Type1_6_4.STRING, "MC|Brand");
                    final byte[] brandBytes = "legacy".getBytes(StandardCharsets.UTF_8);
                    brand.write(Type.SHORT, (short) brandBytes.length); // data length
                    brand.write(Type.REMAINING_BYTES, brandBytes); // data

                    wrapper.send(Protocol1_6_2to1_6_1.class);
                    brand.send(Protocol1_6_2to1_6_1.class);
                    wrapper.cancel();
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_1.ENTITY_PROPERTIES, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                handler(wrapper -> {
                    final int amount = wrapper.passthrough(Type.INT); // count
                    for (int i = 0; i < amount; i++) {
                        wrapper.passthrough(Type1_6_4.STRING); // id
                        wrapper.passthrough(Type.DOUBLE); // baseValue
                        wrapper.write(Type.SHORT, (short) 0); // modifier count
                    }
                });
            }
        });

        this.registerServerbound(ServerboundPackets1_6_4.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_UBYTE); // position
                map(Type.UNSIGNED_BYTE); // direction
                map(Type1_7_6_10.COMPRESSED_ITEM); // item
                map(Type.UNSIGNED_BYTE); // offset x
                map(Type.UNSIGNED_BYTE); // offset y
                map(Type.UNSIGNED_BYTE); // offset z
                handler(wrapper -> {
                    final Position pos = wrapper.get(Type1_7_6_10.POSITION_UBYTE, 0);
                    final short direction = wrapper.get(Type.UNSIGNED_BYTE, 0);
                    final Item item = wrapper.get(Type1_7_6_10.COMPRESSED_ITEM, 0);

                    if (item != null && item.identifier() == ItemList1_6.sign.itemID && direction != 255 && direction != 0) { // If placed item is a sign then cancel and send a OPEN_SIGN_EDITOR packet
                        final PacketWrapper openSignEditor = PacketWrapper.create(ClientboundPackets1_6_4.OPEN_SIGN_EDITOR, wrapper.user());
                        openSignEditor.write(Type.BYTE, (byte) 0); // magic value
                        BlockFace face;
                        switch (direction) {
                            case 2 -> face = BlockFace.NORTH;
                            case 3 -> face = BlockFace.SOUTH;
                            case 4 -> face = BlockFace.WEST;
                            case 5 -> face = BlockFace.EAST;
                            default -> face = BlockFace.TOP;
                        };
                        openSignEditor.write(Type1_7_6_10.POSITION_INT, pos.getRelative(face));
                        openSignEditor.send(Protocol1_6_2to1_6_1.class);
                    }
                });
            }
        });
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new PreNettySplitter(userConnection, Protocol1_6_2to1_6_1.class, ClientboundPackets1_6_1::getPacket));
    }
}
