package de.florianmichael.viabeta.api.rewriter;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.*;
import com.viaversion.viaversion.libs.fastutil.ints.*;
import de.florianmichael.viabeta.api.model.ChunkCoord;
import de.florianmichael.viabeta.api.model.IdAndData;

import java.util.*;

public abstract class AbstractChunkTracker extends StoredObject {

    private final Map<ChunkCoord, Chunk> chunks = new HashMap<>();
    private final IntSet toTrack = new IntOpenHashSet();
    private final boolean trackAll;
    private final Int2IntMap replacements = new Int2IntOpenHashMap();

    public AbstractChunkTracker(final UserConnection user, final int... toTrack) {
        super(user);

        for (final int trackedBlock : toTrack) {
            this.toTrack.add(trackedBlock);
        }
        this.trackAll = this.toTrack.contains(0);
    }

    public void trackAndRemap(final Chunk chunk) {
        final ChunkCoord chunkCoord = new ChunkCoord(chunk.getX(), chunk.getZ());
        if (chunk.isFullChunk() && chunk.getBitmask() == 0) {
            this.chunks.remove(chunkCoord);
            return;
        }

        final Chunk copyChunk = new BaseChunk(chunk.getX(), chunk.getZ(), true, false, 0xFFFF, new ChunkSection[chunk.getSections().length], null, new ArrayList<>());
        if (!chunk.isFullChunk()) {
            if (this.chunks.containsKey(chunkCoord)) {
                copyChunk.setSections(this.chunks.get(chunkCoord).getSections());
            } else {
                return;
            }
        } else {
            this.chunks.put(chunkCoord, copyChunk);
        }

        // Track
        if (!this.toTrack.isEmpty()) {
            for (int i = 0; i < chunk.getSections().length; i++) {
                final ChunkSection section = chunk.getSections()[i];
                if (section == null) continue;
                copyChunk.getSections()[i] = null;

                final DataPalette palette = section.palette(PaletteType.BLOCKS);
                if (!this.hasRemappableBlocks(palette)) continue;

                final ChunkSection copySection = copyChunk.getSections()[i] = new ChunkSectionImpl(false);
                final DataPalette copyPalette = copySection.palette(PaletteType.BLOCKS);
                copyPalette.addId(0);

                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            final int flatBlock = palette.idAt(x, y, z);
                            if (this.trackAll || this.toTrack.contains(flatBlock >> 4)) {
                                copyPalette.setIdAt(x, y, z, flatBlock);
                            }
                        }
                    }
                }
            }
        }

        // Remap
        for (int i = 0; i < chunk.getSections().length; i++) {
            final ChunkSection section = chunk.getSections()[i];
            if (section == null) continue;
            final DataPalette palette = section.palette(PaletteType.BLOCKS);

            for (Int2IntMap.Entry entry : this.replacements.int2IntEntrySet()) {
                palette.replaceId(entry.getIntKey(), entry.getIntValue());
            }

            if (!this.hasRemappableBlocks(palette)) continue;

            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        final int flatBlock = palette.idAt(x, y, z);
                        if (this.trackAll || this.toTrack.contains(flatBlock >> 4)) {
                            final IdAndData block = IdAndData.fromCompressedData(flatBlock);
                            this.remapBlock(block, x + (chunk.getX() << 4), y + (i * 16), z + (chunk.getZ() << 4));
                            final int newFlatBlock = block.toCompressedData();
                            if (newFlatBlock != flatBlock) {
                                palette.setIdAt(x, y, z, newFlatBlock);
                            }
                        }
                    }
                }
            }

            this.postRemap(palette);
        }
    }

    public void trackAndRemap(final Position position, final IdAndData block) {
        final int x = position.x();
        final int y = position.y();
        final int z = position.z();
        final Chunk chunk = this.chunks.get(new ChunkCoord(x >> 4, z >> 4));

        if (chunk != null && y >= 0 && y >> 4 < chunk.getSections().length) {
            ChunkSection section = chunk.getSections()[y >> 4];
            if (this.trackAll || this.toTrack.contains(block.id)) {
                if (section == null) {
                    section = chunk.getSections()[y >> 4] = new ChunkSectionImpl(false);
                    section.palette(PaletteType.BLOCKS).addId(0);
                }
                section.palette(PaletteType.BLOCKS).setIdAt(x & 15, y & 15, z & 15, block.toCompressedData());
            } else if (section != null) {
                section.palette(PaletteType.BLOCKS).setIdAt(x & 15, y & 15, z & 15, 0);
            }
        }

        if (this.replacements.containsKey(block.toCompressedData())) {
            final int newFlatBlock = this.replacements.get(block.toCompressedData());
            block.id = newFlatBlock >> 4;
            block.data = newFlatBlock & 15;
        }
        if (this.trackAll || this.toTrack.contains(block.id)) {
            this.remapBlock(block, x, y, z);
        }
    }

    public void remapBlockParticle(final IdAndData block) {
        if (this.replacements.containsKey(block.toCompressedData())) {
            final int newFlatBlock = this.replacements.get(block.toCompressedData());
            block.id = newFlatBlock >> 4;
            block.data = newFlatBlock & 15;
        }
        if (this.trackAll || this.toTrack.contains(block.id)) {
            this.remapBlock(block, 0, -16, 0);
        }
    }

    public void clear() {
        this.chunks.clear();
    }

    public boolean isChunkLoaded(final int chunkX, final int chunkZ) {
        return this.chunks.containsKey(new ChunkCoord(chunkX, chunkZ));
    }

    public IdAndData getBlockNotNull(final Position position) {
        return this.getBlockNotNull(position.x(), position.y(), position.z());
    }

    public IdAndData getBlockNotNull(final int x, final int y, final int z) {
        IdAndData block = this.getBlock(x, y, z);
        if (block == null) block = new IdAndData(0, 0);
        return block;
    }

    public IdAndData getBlock(final Position position) {
        return this.getBlock(position.x(), position.y(), position.z());
    }

    public IdAndData getBlock(final int x, final int y, final int z) {
        final Chunk chunk = this.chunks.get(new ChunkCoord(x >> 4, z >> 4));
        if (chunk != null) {
            if (y < 0 || y >> 4 > chunk.getSections().length - 1) return null;
            final ChunkSection section = chunk.getSections()[y >> 4];
            if (section != null) {
                return IdAndData.fromCompressedData(section.palette(PaletteType.BLOCKS).idAt(x & 15, y & 15, z & 15));
            }
        }
        return null;
    }

    protected void registerReplacement(final IdAndData from, final IdAndData to) {
        this.replacements.put(from.toCompressedData(), to.toCompressedData());
    }

    protected void remapBlock(final IdAndData block, final int x, final int y, final int z) {
    }

    protected void postRemap(final DataPalette palette) {
    }

    private boolean hasRemappableBlocks(final DataPalette palette) {
        if (this.trackAll) return true;
        if (this.toTrack.isEmpty()) return false;

        boolean hasTrackableBlocks = false;
        for (int i = 0; i < palette.size(); i++) {
            if (this.toTrack.contains(palette.idByIndex(i) >> 4)) {
                hasTrackableBlocks = true;
            }
        }
        return hasTrackableBlocks;
    }

}
