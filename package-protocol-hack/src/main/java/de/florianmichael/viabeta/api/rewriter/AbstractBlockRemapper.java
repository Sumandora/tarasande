package de.florianmichael.viabeta.api.rewriter;

import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.chunks.*;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;
import de.florianmichael.viabeta.api.model.IdAndData;

public abstract class AbstractBlockRemapper {

    private final Int2IntMap REPLACEMENTS = new Int2IntOpenHashMap();

    protected void registerReplacement(final int from, final int to) {
        this.REPLACEMENTS.put(from, to);
    }

    protected void registerReplacement(final IdAndData from, final IdAndData to) {
        this.REPLACEMENTS.put(from.toCompressedData(), to.toCompressedData());
    }

    public void remapChunk(final Chunk chunk) {
        for (int i = 0; i < chunk.getSections().length; i++) {
            final ChunkSection section = chunk.getSections()[i];
            if (section == null) continue;
            final DataPalette palette = section.palette(PaletteType.BLOCKS);

            for (Int2IntMap.Entry entry : this.REPLACEMENTS.int2IntEntrySet()) {
                palette.replaceId(entry.getIntKey(), entry.getIntValue());
            }
        }
    }

    public void remapBlockChangeRecords(final BlockChangeRecord[] blockChangeRecords) {
        for (final BlockChangeRecord record : blockChangeRecords) {
            final int id = record.getBlockId();
            if (this.REPLACEMENTS.containsKey(id)) {
                record.setBlockId(this.REPLACEMENTS.get(id));
            }
        }
    }

    public void remapBlock(final IdAndData block) {
        if (this.REPLACEMENTS.containsKey(block.toCompressedData())) {
            final int replacement = this.REPLACEMENTS.get(block.toCompressedData());
            block.id = replacement >> 4;
            block.data = replacement & 15;
        }
    }

    public int remapBlock(final int id) {
        return this.REPLACEMENTS.getOrDefault(id, id);
    }

}
