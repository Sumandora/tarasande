/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.vialegacy.protocols.protocol1_2_1_3to1_1.chunk;

import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSectionLight;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSectionLightImpl;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.CustomByteType;
import de.florianmichael.vialegacy.protocols.protocol1_4_6_7to1_4_4_5.storage.DimensionStorage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.zip.Deflater;

public class ChunkPacketWriter {
	
	public static void writeChunkPacket(PacketWrapper pw, Chunk chunk) throws Exception {
		pw.write(Type.INT, chunk.getX());
		pw.write(Type.INT, chunk.getZ());
		pw.write(Type.BOOLEAN, chunk.isFullChunk());
		pw.write(Type.SHORT, (short) chunk.getBitmask());
		pw.write(Type.SHORT, (short) 0);

		ByteBuf dataToCompress = Unpooled.buffer();

		// Half byte per block data
		ByteBuf blockData = Unpooled.buffer();

		for (int i = 0; i < chunk.getSections().length; i++) {
			if ((chunk.getBitmask() & 1 << i) == 0)
				continue;
			ChunkSection section = chunk.getSections()[i];
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					int previousData = 0;
					for (int x = 0; x < 16; x++) {
						int block = section.getFlatBlock(x, y, z);
						dataToCompress.writeByte(block >> 4);

						int data = block & 0xF;
						if (x % 2 == 0) {
							previousData = data;
						} else {
							blockData.writeByte((data << 4) | previousData);
						}
					}
				}
			}
		}
		dataToCompress.writeBytes(blockData);
		blockData.release();

		for (int i = 0; i < chunk.getSections().length; i++) {
			if ((chunk.getBitmask() & 1 << i) == 0)
				continue;
			ChunkSectionLight sec = new ChunkSectionLightImpl();
			byte[] newBytes = new byte[dataToCompress.readableBytes()];
			dataToCompress.readBytes(newBytes);
			sec.setBlockLight(newBytes);
			chunk.getSections()[i].setLight(sec);
		}

		boolean skyLight = pw.user().get(DimensionStorage.class).getDimension() == 0;
		if (skyLight) {
			for (int i = 0; i < chunk.getSections().length; i++) {
				if ((chunk.getBitmask() & 1 << i) == 0)
					continue;
				ChunkSectionLight sec = new ChunkSectionLightImpl();
				byte[] newBytes = new byte[dataToCompress.readableBytes()];
				dataToCompress.readBytes(newBytes);
				sec.setSkyLight(newBytes);
				chunk.getSections()[i].setLight(sec);
			}
		}

		if (chunk.isFullChunk() && chunk.isBiomeData()) {
			for (int biome : chunk.getBiomeData()) {
				dataToCompress.writeByte((byte) biome);
			}
		}

		dataToCompress.readerIndex(0);
		byte[] data = new byte[dataToCompress.readableBytes()];
		dataToCompress.readBytes(data);
		dataToCompress.release();

		Deflater deflater = new Deflater(0);
		byte[] compressedData;
		int compressedSize;
		try {
			deflater.setInput(data, 0, data.length);
			deflater.finish();
			compressedData = new byte[data.length];
			compressedSize = deflater.deflate(compressedData);
		} finally {
			deflater.end();
		}

		pw.write(Type.INT, compressedSize);
		pw.write(Type.INT, 0);
		pw.write(new CustomByteType(compressedSize), compressedData);
	}
}
