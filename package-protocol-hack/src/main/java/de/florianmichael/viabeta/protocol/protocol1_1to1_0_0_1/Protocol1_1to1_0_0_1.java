package de.florianmichael.viabeta.protocol.protocol1_1to1_0_0_1;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.api.rewriter.LegacyItemRewriter;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;
import de.florianmichael.viabeta.protocol.protocol1_1to1_0_0_1.rewriter.ChatFilter;
import de.florianmichael.viabeta.protocol.protocol1_1to1_0_0_1.rewriter.ItemRewriter;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.ClientboundPackets1_1;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.ServerboundPackets1_1;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.Type1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;

public class Protocol1_1to1_0_0_1 extends AbstractProtocol<ClientboundPackets1_0, ClientboundPackets1_1, ServerboundPackets1_0, ServerboundPackets1_1> {

    private final LegacyItemRewriter<Protocol1_1to1_0_0_1> itemRewriter = new ItemRewriter(this);

    public Protocol1_1to1_0_0_1() {
        super(ClientboundPackets1_0.class, ClientboundPackets1_1.class, ServerboundPackets1_0.class, ServerboundPackets1_1.class);
    }

    @Override
    protected void registerPackets() {
        super.registerPackets();
        this.itemRewriter.register();

        this.registerServerbound(State.LOGIN, ServerboundPackets1_0.LOGIN.getId(), ServerboundPackets1_1.LOGIN.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // protocol id
                map(Type1_6_4.STRING); // username
                map(Type.LONG); // seed
                read(Type1_6_4.STRING); // level type
                map(Type.INT); // game mode
                map(Type.BYTE); // dimension id
                map(Type.BYTE); // difficulty
                map(Type.BYTE); // world height
                map(Type.BYTE); // max players
            }
        });
        this.registerServerbound(ServerboundPackets1_1.CHAT_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_6_4.STRING, Type1_6_4.STRING, ChatFilter::filter); // message
            }
        });
        this.registerServerbound(ServerboundPackets1_1.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // dimension id
                map(Type.BYTE); // difficulty
                map(Type.BYTE); // game mode
                map(Type.SHORT); // world height
                map(Type.LONG); // seed
                read(Type1_6_4.STRING); // level type
            }
        });
        this.cancelServerbound(ServerboundPackets1_1.PLUGIN_MESSAGE);

        this.registerClientbound(ClientboundPackets1_0.JOIN_GAME, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type1_6_4.STRING); // username
                map(Type.LONG); // seed
                create(Type1_6_4.STRING, "default_1_1"); // level type
                map(Type.INT); // game mode
                map(Type.BYTE); // dimension id
                map(Type.BYTE); // difficulty
                map(Type.BYTE); // world height
                map(Type.BYTE); // max players
            }
        });
        this.registerClientbound(ClientboundPackets1_0.CHAT_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_6_4.STRING, Type1_6_4.STRING, msg -> msg.replace("\u00C2", "")); // message
            }
        });
        this.registerClientbound(ClientboundPackets1_0.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // dimension id
                map(Type.BYTE); // difficulty
                map(Type.BYTE); // game mode
                map(Type.SHORT); // world height
                map(Type.LONG); // seed
                create(Type1_6_4.STRING, "default_1_1"); // level type
            }
        });
        this.registerClientbound(ClientboundPackets1_0.UPDATE_SIGN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_SHORT); // position
                map(Type1_6_4.STRING, Type1_6_4.STRING, msg -> msg.replace("Â", "")); // line 1
                map(Type1_6_4.STRING, Type1_6_4.STRING, msg -> msg.replace("Â", "")); // line 2
                map(Type1_6_4.STRING, Type1_6_4.STRING, msg -> msg.replace("Â", "")); // line 3
                map(Type1_6_4.STRING, Type1_6_4.STRING, msg -> msg.replace("Â", "")); // line 4
            }
        });
        this.registerClientbound(ClientboundPackets1_0.DISCONNECT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_6_4.STRING, Type1_6_4.STRING, reason -> reason.replace("Â", "")); // reason
            }
        });

        // C -> S Packet27Position is unused (no need to handle or remap)
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new PreNettySplitter(userConnection, Protocol1_1to1_0_0_1.class, ClientboundPackets1_0::getPacket));
    }

    @Override
    public LegacyItemRewriter<Protocol1_1to1_0_0_1> getItemRewriter() {
        return this.itemRewriter;
    }
}
