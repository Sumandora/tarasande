package de.florianmichael.viasnapshot.protocol.protocol1_14to3D_Shareware;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.data.BackwardsMappings;
import com.viaversion.viabackwards.api.rewriters.SoundRewriter;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.types.Chunk1_14Type;
import de.florianmichael.viasnapshot.protocol.protocol1_14to3D_Shareware.packets.BlockItemPackets3D_Shareware;
import de.florianmichael.viasnapshot.protocol.protocol1_14to3D_Shareware.packets.EntityPackets3D_Shareware;
import de.florianmichael.viasnapshot.protocol.protocol1_14to3D_Shareware.storage.ChunkCenterTracker3D_Shareware;
import de.florianmichael.viasnapshot.util.ItemBackwardsMappings;

@SuppressWarnings("DataFlowIssue")
public class Protocol1_14to3D_Shareware extends BackwardsProtocol<ClientboundPackets3D_Shareware, ClientboundPackets1_14, ServerboundPackets3D_Shareware, ServerboundPackets1_14> {

    public static final BackwardsMappings MAPPINGS = new ItemBackwardsMappings("3D_Shareware", "1.14");
    public static final int SERVERSIDE_VIEW_DISTANCE = 64;

    private BlockItemPackets3D_Shareware blockItemPackets;

    public Protocol1_14to3D_Shareware() {
        super(ClientboundPackets3D_Shareware.class, ClientboundPackets1_14.class, ServerboundPackets3D_Shareware.class, ServerboundPackets1_14.class);
    }

    @Override
    protected void registerPackets() {
        executeAsyncAfterLoaded(Protocol1_14To1_13_2.class, MAPPINGS::load);

        blockItemPackets = new BlockItemPackets3D_Shareware(this);
        blockItemPackets.register();
        new EntityPackets3D_Shareware(this).registerPackets();
        final SoundRewriter<ClientboundPackets3D_Shareware> soundRewriter = new SoundRewriter<>(this);
        soundRewriter.registerSound(ClientboundPackets3D_Shareware.SOUND);
        soundRewriter.registerSound(ClientboundPackets3D_Shareware.ENTITY_SOUND);
        soundRewriter.registerNamedSound(ClientboundPackets3D_Shareware.NAMED_SOUND);
        soundRewriter.registerStopSound(ClientboundPackets3D_Shareware.STOP_SOUND);

        this.registerClientbound(ClientboundPackets3D_Shareware.CHUNK_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final Chunk chunk = wrapper.passthrough(new Chunk1_14Type());
                    ChunkCenterTracker3D_Shareware entityTracker = wrapper.user().get(ChunkCenterTracker3D_Shareware.class);
                    final int diffX = Math.abs(entityTracker.getChunkCenterX() - chunk.getX());
                    final int diffZ = Math.abs(entityTracker.getChunkCenterZ() - chunk.getZ());
                    if (entityTracker.isForceSendCenterChunk() || diffX >= SERVERSIDE_VIEW_DISTANCE || diffZ >= SERVERSIDE_VIEW_DISTANCE) {
                        final PacketWrapper fakePosLook = wrapper.create(ClientboundPackets1_14.UPDATE_VIEW_POSITION); // Set center chunk
                        fakePosLook.write(Type.VAR_INT, chunk.getX());
                        fakePosLook.write(Type.VAR_INT, chunk.getZ());
                        fakePosLook.send(Protocol1_14to3D_Shareware.class);
                        entityTracker.setChunkCenterX(chunk.getX());
                        entityTracker.setChunkCenterZ(chunk.getZ());
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets3D_Shareware.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    ChunkCenterTracker3D_Shareware entityTracker = wrapper.user().get(ChunkCenterTracker3D_Shareware.class);
                    // The client may reset the center chunk if dimension is changed
                    entityTracker.setForceSendCenterChunk(true);
                });
            }
        });
    }

    @Override
    public void init(UserConnection userConnection) {
        userConnection.put(new ChunkCenterTracker3D_Shareware(userConnection));
    }

    @Override
    public BackwardsMappings getMappingData() {
        return MAPPINGS;
    }

    @Override
    public BlockItemPackets3D_Shareware getItemRewriter() {
        return this.blockItemPackets;
    }
}
