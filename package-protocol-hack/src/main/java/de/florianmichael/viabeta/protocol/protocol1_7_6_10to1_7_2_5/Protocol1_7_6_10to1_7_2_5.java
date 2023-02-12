package de.florianmichael.viabeta.protocol.protocol1_7_6_10to1_7_2_5;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.protocols.base.BaseProtocol1_7;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.protocol.protocol1_7_6_10to1_7_2_5.provider.GameProfileFetcher;
import de.florianmichael.viabeta.protocol.protocol1_7_6_10to1_7_2_5.rewriter.TranslationRewriter;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.model.GameProfile;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Protocol1_7_6_10to1_7_2_5 extends AbstractProtocol<ClientboundPackets1_7_2, ClientboundPackets1_7_2, ServerboundPackets1_7_2, ServerboundPackets1_7_2> {

    public Protocol1_7_6_10to1_7_2_5() {
        super(ClientboundPackets1_7_2.class, ClientboundPackets1_7_2.class, ServerboundPackets1_7_2.class, ServerboundPackets1_7_2.class);
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(State.LOGIN, ClientboundLoginPackets.GAME_PROFILE.getId(), ClientboundLoginPackets.GAME_PROFILE.getId(), new PacketHandlers() {
            @Override
            public void register() {
                map(Type.STRING, Type.STRING, BaseProtocol1_7::addDashes); // uuid
                map(Type.STRING); // name
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.SPAWN_PLAYER, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.VAR_INT); // entity id
                map(Type.STRING, Type.STRING, BaseProtocol1_7::addDashes); // uuid
                map(Type.STRING); // name
                create(Type.VAR_INT, 0); // properties count
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                map(Type.SHORT); // item in hand
                map(Type1_7_6_10.METADATA_LIST); // metadata
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.CHAT_MESSAGE, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.STRING, Type.STRING, TranslationRewriter::toClient); // message
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.BLOCK_ENTITY_DATA, new PacketHandlers() {
            @Override
            public void register() {
                map(Type1_7_6_10.POSITION_SHORT); // position
                map(Type.UNSIGNED_BYTE); // type
                map(Type1_7_6_10.COMPRESSED_NBT); // data
                handler(wrapper -> {
                    final GameProfileFetcher gameProfileFetcher = Via.getManager().getProviders().get(GameProfileFetcher.class);
                    if (gameProfileFetcher == null) {
                        throw new IllegalStateException("Please provide the GameProfileFetcher");
                    }

                    final Position pos = wrapper.get(Type1_7_6_10.POSITION_SHORT, 0);
                    final short type = wrapper.get(Type.UNSIGNED_BYTE, 0);
                    final CompoundTag tag = wrapper.get(Type1_7_6_10.COMPRESSED_NBT, 0);
                    if (type != 4/*skull*/) return;
                    final ByteTag skullType = tag.get("SkullType");
                    if (skullType == null || skullType.asByte() != 3/*player_skull*/) return;

                    final StringTag extraType = tag.remove("ExtraType");

                    if (!ViaBeta.getConfig().isLegacySkullLoading()) return;

                    final String skullName = extraType == null ? "" : extraType.getValue();
                    final CompoundTag newTag = tag.clone();

                    if (gameProfileFetcher.isUUIDLoaded(skullName)) { // short cut if skull is already loaded
                        final UUID uuid = gameProfileFetcher.getMojangUUID(skullName);
                        if (gameProfileFetcher.isGameProfileLoaded(uuid)) {
                            final GameProfile skullProfile = gameProfileFetcher.getGameProfile(uuid);
                            if (skullProfile == null || skullProfile.isOffline()) return;

                            newTag.put("Owner", writeGameProfileToTag(skullProfile));
                            wrapper.set(Type1_7_6_10.COMPRESSED_NBT, 0, newTag);
                            return;
                        }
                    }

                    gameProfileFetcher.getMojangUUIDAsync(skullName).thenAccept(uuid -> {
                        final GameProfile skullProfile = gameProfileFetcher.getGameProfile(uuid);
                        if (skullProfile == null || skullProfile.isOffline()) return;

                        newTag.put("Owner", writeGameProfileToTag(skullProfile));
                        try {
                            final PacketWrapper updateSkull = PacketWrapper.create(ClientboundPackets1_7_2.BLOCK_ENTITY_DATA, wrapper.user());
                            updateSkull.write(Type1_7_6_10.POSITION_SHORT, pos);
                            updateSkull.write(Type.UNSIGNED_BYTE, type);
                            updateSkull.write(Type1_7_6_10.COMPRESSED_NBT, newTag);
                            updateSkull.send(Protocol1_7_6_10to1_7_2_5.class);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    });
                });
            }
        });
    }

    public static CompoundTag writeGameProfileToTag(final GameProfile gameProfile) {
        final CompoundTag ownerTag = new CompoundTag();

        if (gameProfile.userName != null && !gameProfile.userName.isEmpty()) ownerTag.put("Name", new StringTag(gameProfile.userName));
        if (gameProfile.uuid != null) ownerTag.put("Id", new StringTag(gameProfile.uuid.toString()));
        if (!gameProfile.properties.isEmpty()) {
            final CompoundTag propertiesTag = new CompoundTag();

            for (Map.Entry<String, List<GameProfile.Property>> entry : gameProfile.properties.entrySet()) {
                final ListTag propertiesList = new ListTag();

                for (GameProfile.Property property : entry.getValue()) {
                    final CompoundTag propertyTag = new CompoundTag();
                    propertyTag.put("Value", new StringTag(property.value));
                    if (property.signature != null) {
                        propertyTag.put("Signature", new StringTag(property.signature));
                    }
                    propertiesList.add(propertyTag);
                }

                propertiesTag.put(entry.getKey(), propertiesList);
            }

            ownerTag.put("Properties", propertiesTag);
        }

        return ownerTag;
    }

    @Override
    public void register(ViaProviders providers) {
        super.register(providers);

        providers.require(GameProfileFetcher.class);
    }
}
