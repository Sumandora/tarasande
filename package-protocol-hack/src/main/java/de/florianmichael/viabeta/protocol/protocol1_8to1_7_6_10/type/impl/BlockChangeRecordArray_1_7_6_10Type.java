package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl;

import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord1_8;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.ViaBeta;
import io.netty.buffer.ByteBuf;

import java.io.*;
import java.util.logging.Level;

public class BlockChangeRecordArray_1_7_6_10Type extends Type<BlockChangeRecord[]> {

    public BlockChangeRecordArray_1_7_6_10Type() {
        super(BlockChangeRecord[].class);
    }

    @Override
    public BlockChangeRecord[] read(ByteBuf buffer) throws Exception {
        final int length = buffer.readUnsignedShort();
        final int dataLength = buffer.readInt();
        final byte[] data = new byte[dataLength];
        buffer.readBytes(data);
        final DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(data));
        final BlockChangeRecord[] blockChangeRecords = new BlockChangeRecord[length];
        try {
            for (int i = 0; i < length; i++) {
                final short position = dataInputStream.readShort();
                final short blockId = dataInputStream.readShort();
                blockChangeRecords[i] = new BlockChangeRecord1_8(position >> 12 & 15, position & 255, position >> 8 & 15, blockId);
            }
        } catch (IOException e) {
            ViaBeta.getPlatform().getLogger().log(Level.WARNING, "MultiBlockChange Record Array length mismatch: Expected " + dataLength + " bytes", e);
        }
        return blockChangeRecords;
    }

    @Override
    public void write(ByteBuf buffer, BlockChangeRecord[] records) throws Exception {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        for (BlockChangeRecord record : records) {
            dataOutputStream.writeShort((short) (record.getSectionX() << 12 | record.getSectionZ() << 8 | record.getY(-1)));
            dataOutputStream.writeShort((short) record.getBlockId());
        }
        final byte[] data = byteArrayOutputStream.toByteArray();
        buffer.writeShort(records.length);
        buffer.writeInt(data.length);
        buffer.writeBytes(data);
    }

}
