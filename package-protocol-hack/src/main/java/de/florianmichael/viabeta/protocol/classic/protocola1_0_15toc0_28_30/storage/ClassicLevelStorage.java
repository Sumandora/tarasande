package de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.storage;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.*;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.util.MathUtil;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.api.model.ChunkCoord;
import de.florianmichael.viabeta.protocol.alpha.protocola1_0_16_2toa1_0_15.ClientboundPacketsa1_0_15;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.Protocola1_0_15toc0_30;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.model.ChunkCoordSpiral;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.model.ClassicLevel;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.provider.ClassicWorldHeightProvider;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.model.NibbleArray1_1;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.type.impl.Chunk_1_1Type;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public class ClassicLevelStorage extends StoredObject {

    private ByteArrayOutputStream netBuffer = new ByteArrayOutputStream(64 * 64 * 64);

    private ClassicLevel classicLevel;

    private int chunkXCount;
    private int sectionYCount;
    private int chunkZCount;

    private int subChunkXLength;
    private int subChunkYLength;
    private int subChunkZLength;

    private int sectionBitmask;

    private int chunksPerTick = ViaBeta.getConfig().getChunksPerTick();

    private final Set<ChunkCoord> loadedChunks = new HashSet<>();
    private long lastPosPacket;

    public ClassicLevelStorage(final UserConnection user) {
        super(user);
    }

    public void addDataPart(final byte[] part, final int partSize) {
        if (this.netBuffer == null) throw new IllegalStateException("Level is already fully loaded");
        this.netBuffer.write(part, 0, partSize);
    }

    public void finish(final int sizeX, final int sizeY, final int sizeZ) {
        try {
            final DataInputStream dis = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(this.netBuffer.toByteArray()), 64 * 1024));
            final byte[] blocks = new byte[dis.readInt()];
            dis.readFully(blocks);
            dis.close();
            this.netBuffer = null;
            this.classicLevel = new ClassicLevel(sizeX, sizeY, sizeZ, blocks);
        } catch (Throwable e) {
            throw new IllegalStateException("Failed to load level", e);
        }

        final short maxChunkSectionCount = Via.getManager().getProviders().get(ClassicWorldHeightProvider.class).getMaxChunkSectionCount(this.getUser());

        this.chunkXCount = sizeX >> 4;
        if (sizeX % 16 != 0) this.chunkXCount++;
        this.sectionYCount = sizeY >> 4;
        if (sizeY % 16 != 0) this.sectionYCount++;
        if (this.sectionYCount > maxChunkSectionCount) this.sectionYCount = maxChunkSectionCount;
        this.chunkZCount = sizeZ >> 4;
        if (sizeZ % 16 != 0) this.chunkZCount++;
        this.subChunkXLength = Math.min(16, sizeX);
        this.subChunkYLength = Math.min(16, sizeY);
        this.subChunkZLength = Math.min(16, sizeZ);
        this.sectionBitmask = 0;
        for (int i = 0; i < this.sectionYCount; i++) this.sectionBitmask = (this.sectionBitmask << 1) | 1;

        if (this.chunksPerTick <= 0) {
            this.chunksPerTick = MathUtil.clamp(32 / this.sectionYCount, 1, 8);
        }
    }

    public void tickChunks(final ChunkCoord center) throws Exception {
        if (!this.getUser().get(ClassicPositionTracker.class).spawned) return;
        if (System.currentTimeMillis() - this.lastPosPacket < 50) {
            return;
        }
        this.lastPosPacket = System.currentTimeMillis();
        this.sendChunks(center, ViaBeta.getConfig().getClassicChunkRange(), this.chunksPerTick);
    }

    public void sendChunks(final ChunkCoord center, final int radius) throws Exception {
        this.sendChunks(center, radius, Integer.MAX_VALUE);
    }

    public void sendChunks(final ChunkCoord center, final int radius, int limit) throws Exception {
        final ChunkCoordSpiral spiral = new ChunkCoordSpiral(center, new ChunkCoord(radius, radius));
        for (ChunkCoord coord : spiral) {
            if (!this.shouldSend(coord)) continue;
            if (limit-- <= 0) return;
            this.sendChunk(coord);
        }
    }

    public void sendChunk(final ChunkCoord coord) throws Exception {
        if (!this.shouldSend(coord)) return;
        final ClassicBlockRemapper remapper = this.getUser().get(ClassicBlockRemapper.class);

        this.classicLevel.calculateLight(coord.chunkX * 16, coord.chunkZ * 16, this.subChunkXLength, this.subChunkZLength);

        final ChunkSection[] modernSections = new ChunkSection[Math.max(8, this.sectionYCount)];
        for (int sectionY = 0; sectionY < this.sectionYCount; sectionY++) {
            final ChunkSection section = modernSections[sectionY] = new ChunkSectionImpl(true);
            section.palette(PaletteType.BLOCKS).addId(0);
            final NibbleArray1_1 skyLight = new NibbleArray1_1(16 * 16 * 16, 4);

            for (int y = 0; y < this.subChunkYLength; y++) {
                final int totalY = y + (sectionY * 16);
                for (int x = 0; x < this.subChunkXLength; x++) {
                    final int totalX = x + (coord.chunkX * 16);
                    for (int z = 0; z < this.subChunkZLength; z++) {
                        final int totalZ = z + (coord.chunkZ * 16);
                        section.palette(PaletteType.BLOCKS).setIdAt(x, y, z, remapper.getMapper().get(this.classicLevel.getBlock(totalX, totalY, totalZ)).toCompressedData());
                        skyLight.set(x, y, z, this.classicLevel.isLit(totalX, totalY, totalZ) ? 15 : 9);
                    }
                }
            }

            section.getLight().setSkyLight(skyLight.getHandle());
        }

        this.loadedChunks.add(coord);

        final Chunk viaChunk = new BaseChunk(coord.chunkX, coord.chunkZ, true, false, this.sectionBitmask, modernSections, new int[256], new ArrayList<>());
        final PacketWrapper chunkData = PacketWrapper.create(ClientboundPacketsa1_0_15.CHUNK_DATA, this.getUser());
        chunkData.write(new Chunk_1_1Type(this.getUser().get(ClientWorld.class)), viaChunk);
        chunkData.send(Protocola1_0_15toc0_30.class);
    }

    private boolean shouldSend(final ChunkCoord coord) {
        if (!this.hasReceivedLevel()) return false;
        boolean isInBounds = (coord.chunkX >= 0 && coord.chunkX < chunkXCount) && coord.chunkZ >= 0 && coord.chunkZ < chunkZCount;
        return isInBounds && !this.isChunkLoaded(coord);
    }

    public boolean isChunkLoaded(final ChunkCoord coord) {
        return this.loadedChunks.contains(coord);
    }

    public boolean isChunkLoaded(final Position position) {
        return this.isChunkLoaded(new ChunkCoord(position.x() >> 4, position.z() >> 4));
    }

    public boolean hasReceivedLevel() {
        return this.classicLevel != null;
    }

    public ClassicLevel getClassicLevel() {
        return this.classicLevel;
    }

}
