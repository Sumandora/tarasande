package de.florianmichael.viasnapshot.protocol.protocol1_16to20w14infinite.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16Types;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_14;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.packets.EntityPackets;
import de.florianmichael.viasnapshot.ViaSnapshot;
import de.florianmichael.viasnapshot.protocol.protocol1_16to20w14infinite.ClientboundPackets20w14infinite;
import de.florianmichael.viasnapshot.protocol.protocol1_16to20w14infinite.Protocol1_16to20w14infinite;
import de.florianmichael.viasnapshot.protocol.protocol1_16to20w14infinite.metadata.MetadataRewriter1_16to20w14infinite;

import java.util.UUID;

public class EntityPackets20w14infinite {

    private static final PacketHandler DIMENSION_HANDLER = wrapper -> {
        int dimension = wrapper.read(Type.INT);
        String dimensionType;
        String dimensionName;
        switch (dimension) {
            case -1 -> dimensionName = dimensionType = "minecraft:the_nether";
            case 0 -> dimensionName = dimensionType = "minecraft:overworld";
            case 1 -> dimensionName = dimensionType = "minecraft:the_end";
            default -> {
                dimensionType = "minecraft:overworld";
                dimensionName = dimensionType + dimension;
            }
        }

        wrapper.write(Type.STRING, dimensionType); // dimension type
        wrapper.write(Type.STRING, dimensionName); // dimension
    };
    private static final String[] WORLD_NAMES = {"minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"};

    public static void register(Protocol1_16to20w14infinite protocol) {
        MetadataRewriter1_16to20w14infinite metadataRewriter = protocol.get(MetadataRewriter1_16to20w14infinite.class);
        metadataRewriter.registerTrackerWithData(ClientboundPackets20w14infinite.SPAWN_ENTITY, Entity1_16Types.FALLING_BLOCK);
        metadataRewriter.registerTracker(ClientboundPackets20w14infinite.SPAWN_MOB);
        metadataRewriter.registerTracker(ClientboundPackets20w14infinite.SPAWN_PLAYER, Entity1_16Types.PLAYER);
        metadataRewriter.registerMetadataRewriter(ClientboundPackets20w14infinite.ENTITY_METADATA, Types1_14.METADATA_LIST);
        metadataRewriter.registerRemoveEntities(ClientboundPackets20w14infinite.DESTROY_ENTITIES);

        // Spawn lightning -> Spawn entity
        protocol.registerClientbound(ClientboundPackets20w14infinite.SPAWN_GLOBAL_ENTITY, ClientboundPackets1_16.SPAWN_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        int entityId = packetWrapper.passthrough(Type.VAR_INT); // entity id
                        packetWrapper.user().getEntityTracker(Protocol1_16to20w14infinite.class).addEntity(entityId, Entity1_16Types.LIGHTNING_BOLT);

                        packetWrapper.write(Type.UUID, UUID.randomUUID()); // uuid
                        packetWrapper.write(Type.VAR_INT, Entity1_16Types.LIGHTNING_BOLT.getId()); // entity type

                        packetWrapper.read(Type.BYTE); // remove type

                        packetWrapper.passthrough(Type.DOUBLE); // x
                        packetWrapper.passthrough(Type.DOUBLE); // y
                        packetWrapper.passthrough(Type.DOUBLE); // z
                        packetWrapper.write(Type.BYTE, (byte) 0); // yaw
                        packetWrapper.write(Type.BYTE, (byte) 0); // pitch
                        packetWrapper.write(Type.INT, 0); // data
                        packetWrapper.write(Type.SHORT, (short) 0); // velocity
                        packetWrapper.write(Type.SHORT, (short) 0); // velocity
                        packetWrapper.write(Type.SHORT, (short) 0); // velocity
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets20w14infinite.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(DIMENSION_HANDLER);
                map(Type.LONG); // Seed
                map(Type.UNSIGNED_BYTE); // Gamemode
                handler(wrapper -> {
                    wrapper.write(Type.BYTE, (byte) -1); // Previous gamemode, set to none

                    String levelType = wrapper.read(Type.STRING);
                    wrapper.write(Type.BOOLEAN, false); // debug
                    wrapper.write(Type.BOOLEAN, levelType.equals("flat"));
                    wrapper.write(Type.BOOLEAN, true); // keep all playerdata
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets20w14infinite.JOIN_GAME, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // Entity ID
                map(Type.UNSIGNED_BYTE); //  Gamemode
                handler(wrapper -> {
                    wrapper.write(Type.BYTE, (byte) -1); // Previous gamemode, set to none
                    wrapper.write(Type.STRING_ARRAY, WORLD_NAMES); // World list - only used for command completion
                    wrapper.write(Type.NBT, EntityPackets.DIMENSIONS_TAG); // Dimension registry
                });
                handler(DIMENSION_HANDLER); // Dimension
                map(Type.LONG); // Seed
                map(Type.UNSIGNED_BYTE); // Max players
                handler(wrapper -> {
                    wrapper.user().getEntityTracker(Protocol1_16to20w14infinite.class).addEntity(wrapper.get(Type.INT, 0), Entity1_16Types.PLAYER);

                    final String type = wrapper.read(Type.STRING);// level type
                    wrapper.passthrough(Type.VAR_INT); // View distance
                    wrapper.passthrough(Type.BOOLEAN); // Reduced debug info
                    wrapper.passthrough(Type.BOOLEAN); // Show death screen

                    wrapper.write(Type.BOOLEAN, false); // Debug
                    wrapper.write(Type.BOOLEAN, type.equals("flat"));
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets20w14infinite.ENTITY_PROPERTIES, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.passthrough(Type.VAR_INT);
                    int size = wrapper.passthrough(Type.INT);
                    int actualSize = size;
                    for (int i = 0; i < size; i++) {
                        // Attributes have been renamed and are now namespaced identifiers
                        String key = wrapper.read(Type.STRING);
                        String attributeIdentifier = Via.getManager().getProtocolManager().getProtocol(Protocol1_16To1_15_2.class).getMappingData().getAttributeMappings().get(key);
                        if (attributeIdentifier == null) {
                            attributeIdentifier = "minecraft:" + key;
                            if (!com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.MappingData.isValid1_13Channel(attributeIdentifier)) {
                                if (!Via.getConfig().isSuppressConversionWarnings()) {
                                    ViaSnapshot.getPlatform().getLogger().warning("Invalid attribute: " + key);
                                }
                                actualSize--;
                                wrapper.read(Type.DOUBLE);
                                int modifierSize = wrapper.read(Type.VAR_INT);
                                for (int j = 0; j < modifierSize; j++) {
                                    wrapper.read(Type.UUID);
                                    wrapper.read(Type.DOUBLE);
                                    wrapper.read(Type.BYTE);
                                }
                                continue;
                            }
                        }

                        wrapper.write(Type.STRING, attributeIdentifier);

                        wrapper.passthrough(Type.DOUBLE);
                        int modifierSize = wrapper.passthrough(Type.VAR_INT);
                        for (int j = 0; j < modifierSize; j++) {
                            wrapper.passthrough(Type.UUID);
                            wrapper.passthrough(Type.DOUBLE);
                            wrapper.passthrough(Type.BYTE);
                        }
                    }
                    if (size != actualSize) {
                        wrapper.set(Type.INT, 0, actualSize);
                    }
                });
            }
        });
    }
}
