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

public class ChunkData {

	// Static constants
	public static final int MAX_HEIGHT = 128;
	
	// Chunk coordinates
	private final int chunkX;
	private final int chunkZ;

	// Chunk data
	private byte[] blocks;
	private NibbleArray1_1 metadata;
	private NibbleArray1_1 blocklight;
	private NibbleArray1_1 skylight;

	public ChunkData(int chunkX, int chunkZ) {
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;

		this.blocks = new byte[32768];
		this.metadata = new NibbleArray1_1(blocks.length);
		this.blocklight = new NibbleArray1_1(blocks.length);
		this.skylight = new NibbleArray1_1(blocks.length);
	}
	
	public int index(int x, int y, int z) {
		return x << 11 | z << 7 | y;
	}

	public int getChunkX() {
		return chunkX;
	}

	public int getBlockId(int idx) {
		return Byte.toUnsignedInt(blocks[idx]);
	}

	public int getBlockId(int x, int y, int z) {
		return getBlockId(index(x, y, z));
	}

	public void setBlockId(int idx, int blockId) {
		this.blocks[idx] = (byte) (blockId & 0xFF);
	}
	
	public void setBlockId(int x, int y, int z, int blockId) {
		this.setBlockId(index(x, y, z), blockId);
	}
	
	public int getMetadata(int x, int y, int z) {
		return metadata.get(x, y, z);
	}
	
	public void setMetadata(int x, int y, int z, int metadata) {
		this.metadata.set(x, y, z, metadata);
	}
	
	public int getBlocklight(int x, int y, int z) {
		return metadata.get(x, y, z);
	}
	
	public void setBlocklight(int x, int y, int z, int blocklight) {
		this.blocklight.set(x, y, z, blocklight);
	}
	
	public int getSkylight(int x, int y, int z) {
		return skylight.get(x, y, z);
	}
	
	public void setSkylight(int x, int y, int z, int skylight) {
		this.skylight.set(x, y, z, skylight);
	}

	public int getChunkZ() {
		return chunkZ;
	}

	public byte[] getBlocks() {
		return blocks;
	}

	public NibbleArray1_1 getMetadata() {
		return metadata;
	}

	public NibbleArray1_1 getBlocklight() {
		return blocklight;
	}

	public NibbleArray1_1 getSkylight() {
		return skylight;
	}
	
	public int updateChunk(byte chunkData[], int xPos, int yPos, int zPos, int endX, int endY, int endZ, int byteOffset) {
		for (int l1 = xPos; l1 < endX; l1++) {
			for (int l2 = zPos; l2 < endZ; l2++) {
				int l3 = l1 << 11 | l2 << 7 | yPos;
				int l4 = endY - yPos;
				System.arraycopy(chunkData, byteOffset, blocks, l3, l4);
				byteOffset += l4;
			}

		}

		for (int i2 = xPos; i2 < endX; i2++) {
			for (int i3 = zPos; i3 < endZ; i3++) {
				int i4 = (i2 << 11 | i3 << 7 | yPos) >> 1;
				int i5 = (endY - yPos) / 2;
				System.arraycopy(chunkData, byteOffset, metadata.data, i4, i5);
				byteOffset += i5;
			}

		}

		for (int j2 = xPos; j2 < endX; j2++) {
			for (int j3 = zPos; j3 < endZ; j3++) {
				int j4 = (j2 << 11 | j3 << 7 | yPos) >> 1;
				int j5 = (endY - yPos) / 2;
				System.arraycopy(chunkData, byteOffset, blocklight.data, j4, j5);
				byteOffset += j5;
			}
		}

		for (int k2 = xPos; k2 < endX; k2++) {
			for (int k3 = zPos; k3 < endZ; k3++) {
				int k4 = (k2 << 11 | k3 << 7 | yPos) >> 1;
				int k5 = (endY - yPos) / 2;
				System.arraycopy(chunkData, byteOffset, skylight.data, k4, k5);
				byteOffset += k5;
			}

		}
		return byteOffset;
	}
}
