package de.florianmichael.viabeta.protocol.beta.protocolb1_6_0_6tob1_5_0_2;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.api.data.BlockList1_6;
import de.florianmichael.viabeta.api.model.IdAndData;
import de.florianmichael.viabeta.api.data.ItemList1_6;
import de.florianmichael.viabeta.protocol.beta.protocolb1_6_0_6tob1_5_0_2.task.TimeTrackTask;
import de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.ClientboundPacketsb1_7;
import de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.ServerboundPacketsb1_7;
import de.florianmichael.viabeta.protocol.beta.protocolb1_6_0_6tob1_5_0_2.storage.WorldTimeStorage;
import de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2.type.Type1_4_2;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage.ChunkTracker;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage.PlayerInfoStorage;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.Type1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;

@SuppressWarnings("DataFlowIssue")
public class Protocolb1_6_0_6tob1_5_0_2 extends AbstractProtocol<ClientboundPacketsb1_5, ClientboundPacketsb1_7, ServerboundPacketsb1_5, ServerboundPacketsb1_7> {

    public Protocolb1_6_0_6tob1_5_0_2() {
        super(ClientboundPacketsb1_5.class, ClientboundPacketsb1_7.class, ServerboundPacketsb1_5.class, ServerboundPacketsb1_7.class);
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(ClientboundPacketsb1_5.TIME_UPDATE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.LONG); // time
                handler(wrapper -> wrapper.user().get(WorldTimeStorage.class).time = wrapper.get(Type.LONG, 0));
            }
        });
        this.registerClientbound(ClientboundPacketsb1_5.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                create(Type.BYTE, (byte) 0); // dimension id
            }
        });
        this.registerClientbound(ClientboundPacketsb1_5.SPAWN_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.BYTE); // type id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                create(Type.INT, 0); // data
            }
        });

        this.registerServerbound(ServerboundPacketsb1_7.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                read(Type.BYTE); // dimension id
            }
        });
        this.registerServerbound(ServerboundPacketsb1_7.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_UBYTE); // position
                map(Type.UNSIGNED_BYTE); // direction
                map(Type1_4_2.NBTLESS_ITEM); // item
                handler(wrapper -> {
                    final PlayerInfoStorage playerInfoStorage = wrapper.user().get(PlayerInfoStorage.class);
                    Position pos = wrapper.get(Type1_7_6_10.POSITION_UBYTE, 0);
                    IdAndData block = wrapper.user().get(ChunkTracker.class).getBlockNotNull(pos);
                    final Item item = wrapper.get(Type1_4_2.NBTLESS_ITEM, 0);
                    if (block.id == BlockList1_6.bed.blockID) {
                        final byte[][] headBlockToFootBlock = {{0, 1}, {-1, 0}, {0, -1}, {1, 0}};
                        final boolean isFoot = (block.data & 8) != 0;
                        if (!isFoot) {
                            final int bedDirection = block.data & 3;
                            pos = new Position(pos.x() + headBlockToFootBlock[bedDirection][0], pos.y(), pos.z() + headBlockToFootBlock[bedDirection][1]);
                            block = wrapper.user().get(ChunkTracker.class).getBlockNotNull(pos);
                            if (block.id != BlockList1_6.bed.blockID) return;
                        }

                        final boolean isOccupied = (block.data & 4) != 0;
                        if (isOccupied) {
                            final PacketWrapper chat = PacketWrapper.create(ClientboundPacketsb1_7.CHAT_MESSAGE, wrapper.user());
                            chat.write(Type1_6_4.STRING, "This bed is occupied");
                            chat.send(Protocolb1_6_0_6tob1_5_0_2.class);
                            return;
                        }

                        int dayTime = (int) (wrapper.user().get(WorldTimeStorage.class).time % 24000L);
                        float dayPercent = (dayTime + 1.0F) / 24000F - 0.25F;
                        if (dayPercent < 0.0F) dayPercent++;
                        if (dayPercent > 1.0F) dayPercent--;

                        final float tempDayPercent = dayPercent;
                        dayPercent = 1.0F - (float) ((Math.cos((double) dayPercent * Math.PI) + 1.0D) / 2D);
                        dayPercent = tempDayPercent + (dayPercent - tempDayPercent) / 3F;
                        float skyRotation = (float) (1.0F - (Math.cos(dayPercent * Math.PI * 2.0F) * 2.0F + 0.5F));
                        if (skyRotation < 0.0F) skyRotation = 0.0F;
                        if (skyRotation > 1.0F) skyRotation = 1.0F;

                        final boolean isDayTime = (int) (skyRotation * 11F) < 4;

                        if (isDayTime) {
                            final PacketWrapper chat = PacketWrapper.create(ClientboundPacketsb1_7.CHAT_MESSAGE, wrapper.user());
                            chat.write(Type1_6_4.STRING, "You can only sleep at night");
                            chat.send(Protocolb1_6_0_6tob1_5_0_2.class);
                            return;
                        }

                        if (Math.abs(playerInfoStorage.posX - (double) pos.x()) > 3D || Math.abs(playerInfoStorage.posY - (double) pos.y()) > 2D || Math.abs(playerInfoStorage.posZ - (double) pos.z()) > 3D) {
                            return;
                        }

                        final PacketWrapper useBed = PacketWrapper.create(ClientboundPacketsb1_7.USE_BED, wrapper.user());
                        useBed.write(Type.INT, playerInfoStorage.entityId); // entity id
                        useBed.write(Type.BYTE, (byte) 0); // magic value (always 0)
                        useBed.write(Type1_7_6_10.POSITION_BYTE, pos); // position
                        useBed.send(Protocolb1_6_0_6tob1_5_0_2.class);
                    } else if (block.id == BlockList1_6.jukebox.blockID) {
                        if (block.data > 0) {
                            final PacketWrapper effect = PacketWrapper.create(ClientboundPacketsb1_7.EFFECT, wrapper.user());
                            effect.write(Type.INT, 1005); // effect id
                            effect.write(Type1_7_6_10.POSITION_UBYTE, pos); // position
                            effect.write(Type.INT, 0); // data
                            effect.send(Protocolb1_6_0_6tob1_5_0_2.class);
                        } else if (item != null && (item.identifier() == ItemList1_6.record13.itemID || item.identifier() == ItemList1_6.recordCat.itemID)) {
                            final PacketWrapper effect = PacketWrapper.create(ClientboundPacketsb1_7.EFFECT, wrapper.user());
                            effect.write(Type.INT, 1005); // effect id
                            effect.write(Type1_7_6_10.POSITION_UBYTE, pos); // position
                            effect.write(Type.INT, item.identifier()); // data
                            effect.send(Protocolb1_6_0_6tob1_5_0_2.class);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void register(ViaProviders providers) {
        super.register(providers);

        Via.getPlatform().runRepeatingSync(new TimeTrackTask(), 1L);
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new PreNettySplitter(userConnection, Protocolb1_6_0_6tob1_5_0_2.class, ClientboundPacketsb1_5::getPacket));

        userConnection.put(new WorldTimeStorage(userConnection));
    }
}
