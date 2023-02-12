package de.florianmichael.viabeta.protocol.beta.protocolb1_5_0_2tob1_4_0_1;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;
import de.florianmichael.viabeta.protocol.beta.protocolb1_5_0_2tob1_4_0_1.type.Typeb1_4;
import de.florianmichael.viabeta.protocol.beta.protocolb1_6_0_6tob1_5_0_2.ClientboundPacketsb1_5;
import de.florianmichael.viabeta.protocol.beta.protocolb1_6_0_6tob1_5_0_2.ServerboundPacketsb1_5;
import de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.type.Typeb1_7_0_3;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.types.Type1_3_1_2;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.types.impl.MetaType1_3_1_2;
import de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2.type.Type1_4_2;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.Type1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;

import java.util.List;

public class Protocolb1_5_0_2tob1_4_0_1 extends AbstractProtocol<ClientboundPacketsb1_4, ClientboundPacketsb1_5, ServerboundPacketsb1_4, ServerboundPacketsb1_5> {

    public Protocolb1_5_0_2tob1_4_0_1() {
        super(ClientboundPacketsb1_4.class, ClientboundPacketsb1_5.class, ServerboundPacketsb1_4.class, ServerboundPacketsb1_5.class);
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(State.LOGIN, ClientboundPacketsb1_4.HANDSHAKE.getId(), ClientboundPacketsb1_5.HANDSHAKE.getId(), new PacketHandlers() {
            @Override
            public void register() {
                map(Typeb1_7_0_3.STRING, Type1_6_4.STRING); // server hash
            }
        });
        this.registerClientbound(State.LOGIN, ClientboundPacketsb1_4.DISCONNECT.getId(), ClientboundPacketsb1_5.DISCONNECT.getId(), new PacketHandlers() {
            @Override
            public void register() {
                map(Typeb1_7_0_3.STRING, Type1_6_4.STRING); // reason
            }
        });
        this.registerClientbound(ClientboundPacketsb1_4.JOIN_GAME, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // entity id
                map(Typeb1_7_0_3.STRING, Type1_6_4.STRING); // username
                read(Typeb1_7_0_3.STRING); // password
                map(Type.LONG); // seed
                map(Type.BYTE); // dimension id
            }
        });
        this.registerClientbound(ClientboundPacketsb1_4.CHAT_MESSAGE, new PacketHandlers() {
            @Override
            public void register() {
                map(Typeb1_7_0_3.STRING, Type1_6_4.STRING); // message
            }
        });
        this.registerClientbound(ClientboundPacketsb1_4.SPAWN_PLAYER, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // entity id
                map(Typeb1_7_0_3.STRING, Type1_6_4.STRING); // username
                handler(wrapper -> {
                    String name = wrapper.get(Type1_6_4.STRING, 0);
                    name = name.substring(0, Math.min(name.length(), 16));
                    wrapper.set(Type1_6_4.STRING, 0, name);
                });
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                map(Type.UNSIGNED_SHORT); // item
            }
        });
        this.registerClientbound(ClientboundPacketsb1_4.SPAWN_MOB, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // entity id
                map(Type.UNSIGNED_BYTE); // type id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                map(Typeb1_4.METADATA_LIST, Type1_3_1_2.METADATA_LIST); // metadata
                handler(wrapper -> rewriteMetadata(wrapper.get(Type1_3_1_2.METADATA_LIST, 0)));
            }
        });
        this.registerClientbound(ClientboundPacketsb1_4.SPAWN_PAINTING, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // entity id
                map(Typeb1_7_0_3.STRING, Type1_6_4.STRING); // motive
                map(Type1_7_6_10.POSITION_INT); // position
                map(Type.INT); // rotation
            }
        });
        this.registerClientbound(ClientboundPacketsb1_4.ENTITY_METADATA, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // entity id
                map(Typeb1_4.METADATA_LIST, Type1_3_1_2.METADATA_LIST); // metadata
                handler(wrapper -> rewriteMetadata(wrapper.get(Type1_3_1_2.METADATA_LIST, 0)));
            }
        });
        this.registerClientbound(ClientboundPacketsb1_4.UPDATE_SIGN, new PacketHandlers() {
            @Override
            public void register() {
                map(Type1_7_6_10.POSITION_SHORT); // position
                map(Typeb1_7_0_3.STRING, Type1_6_4.STRING); // line 1
                map(Typeb1_7_0_3.STRING, Type1_6_4.STRING); // line 2
                map(Typeb1_7_0_3.STRING, Type1_6_4.STRING); // line 3
                map(Typeb1_7_0_3.STRING, Type1_6_4.STRING); // line 4
            }
        });
        this.registerClientbound(ClientboundPacketsb1_4.DISCONNECT, new PacketHandlers() {
            @Override
            public void register() {
                map(Typeb1_7_0_3.STRING, Type1_6_4.STRING); // reason
            }
        });

        this.registerServerbound(State.LOGIN, ServerboundPacketsb1_5.HANDSHAKE.getId(), ServerboundPacketsb1_4.HANDSHAKE.getId(), new PacketHandlers() {
            @Override
            public void register() {
                map(Type1_6_4.STRING, Typeb1_7_0_3.STRING); // username
            }
        });
        this.registerServerbound(State.LOGIN, ServerboundPacketsb1_4.LOGIN.getId(), ServerboundPacketsb1_5.LOGIN.getId(), new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // protocol id
                map(Type1_6_4.STRING, Typeb1_7_0_3.STRING); // username
                create(Typeb1_7_0_3.STRING, "Password"); // password
                map(Type.LONG); // seed
                map(Type.BYTE); // dimension id
            }
        });
        this.registerServerbound(ServerboundPacketsb1_5.CHAT_MESSAGE, new PacketHandlers() {
            @Override
            public void register() {
                map(Type1_6_4.STRING, Typeb1_7_0_3.STRING); // message
            }
        });
        this.registerServerbound(ServerboundPacketsb1_5.CLICK_WINDOW, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.BYTE); // window id
                map(Type.SHORT); // slot
                map(Type.BYTE); // button
                map(Type.SHORT); // action
                read(Type.BYTE); // mode
                map(Type1_4_2.NBTLESS_ITEM); // item
            }
        });
        this.registerServerbound(ServerboundPacketsb1_5.UPDATE_SIGN, new PacketHandlers() {
            @Override
            public void register() {
                map(Type1_7_6_10.POSITION_SHORT); // position
                map(Type1_6_4.STRING, Typeb1_7_0_3.STRING); // line 1
                map(Type1_6_4.STRING, Typeb1_7_0_3.STRING); // line 2
                map(Type1_6_4.STRING, Typeb1_7_0_3.STRING); // line 3
                map(Type1_6_4.STRING, Typeb1_7_0_3.STRING); // line 4
            }
        });
        this.registerServerbound(ServerboundPacketsb1_5.DISCONNECT, new PacketHandlers() {
            @Override
            public void register() {
                map(Type1_6_4.STRING, Typeb1_7_0_3.STRING); // reason
            }
        });
    }

    private void rewriteMetadata(final List<Metadata> metadataList) {
        for (Metadata metadata : metadataList) {
            metadata.setMetaType(MetaType1_3_1_2.byId(metadata.metaType().typeId()));
        }
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new PreNettySplitter(userConnection, Protocolb1_5_0_2tob1_4_0_1.class, ClientboundPacketsb1_4::getPacket));
    }
}
