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

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.chunks.*;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl;
import de.florianmichael.vialegacy.protocols.protocol1_2_1_3to1_1.Protocol1_2_1_3to1_1;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChunkTracker extends StoredObject {
	
	private final Map<ChunkCordIntPair, ChunkData> loadedChunks;
	
	public ChunkTracker(UserConnection user) {
		super(user);
		this.loadedChunks = new HashMap<ChunkCordIntPair, ChunkData>();
	}
	
	public int getBlockId(int x, int y, int z) {
		if(x < 0xfe17b800 || z < 0xfe17b800 || x >= 0x1e84800 || z > 0x1e84800) {
            return 0;
        }
		if(y < 0 || y >= 128) {
			return 0;
		}
		ChunkData chunk = getChunkFromPosition(x, z);
		if(chunk == null) {
			throw new NullPointerException("Tried to access unloaded chunk: " + (x >> 4) + " " + (z >> 4));
		}
		return chunk.getBlockId(x & 0xF, y, z & 0xF);
	}
	
	public void setBlockId(int x, int y, int z, int blockId) {
		if(x < 0xfe17b800 || z < 0xfe17b800 || x >= 0x1e84800 || z > 0x1e84800) {
            return;
        }
		if(y < 0 || y >= 128) {
			return;
		}
		ChunkData chunk = getChunkFromPosition(x, z);
		if(chunk == null) {
			throw new NullPointerException("Tried to set a block in an unloaded chunk: " + (x >> 4) + " " + (z >> 4));
		}
		chunk.setBlockId(x & 0xF, y, z & 0xF, blockId);
	}
	
	public int getBlockMetadata(int x, int y, int z) {
		if(x < 0xfe17b800 || z < 0xfe17b800 || x >= 0x1e84800 || z > 0x1e84800) {
            return 0;
        }
		if(y < 0 || y >= 128) {
			return 0;
		}
		ChunkData chunk = getChunkFromPosition(x, z);
		if(chunk == null) {
			throw new NullPointerException("Tried to access unloaded chunk: " + (x >> 4) + " " + (z >> 4));
		}
		return chunk.getMetadata(x & 0xF, y, z & 0xF);
	}
	
	public void setBlockMetadata(int x, int y, int z, int metadata) {
		if(x < 0xfe17b800 || z < 0xfe17b800 || x >= 0x1e84800 || z > 0x1e84800) {
            return;
        }
		if(y < 0 || y >= 128) {
			return;
		}
		ChunkData chunk = getChunkFromPosition(x, z);
		if(chunk == null) {
			throw new NullPointerException("Tried to set block metadata in an unloaded chunk: " + (x >> 4) + " " + (z >> 4));
		}
		chunk.setMetadata(x & 0xF, y, z & 0xF, metadata);
	}
	
	public ChunkData getChunkAt(int chunkX, int chunkZ) {
		return loadedChunks.get(new ChunkCordIntPair(chunkX, chunkZ));
	}
	
	public ChunkData getChunkFromPosition(int x, int z) {
		return getChunkAt(x >> 4, z >> 4);
	}
	
	public boolean isChunkLoaded(int chunkX, int chunkZ) {
		return loadedChunks.containsKey(new ChunkCordIntPair(chunkX, chunkZ));
	}
	
	public void loadChunk(int chunkX, int chunkZ, ChunkData chunk) throws Exception {
		loadedChunks.put(new ChunkCordIntPair(chunkX, chunkZ), chunk);
		this.updateChunk(chunkX, chunkZ, true);
	}
	
	public void unloadChunk(int chunkX, int chunkZ) {
		loadedChunks.remove(new ChunkCordIntPair(chunkX, chunkZ));
	}
	
	public Map<ChunkCordIntPair, ChunkData> getLoadedChunks() {
		return loadedChunks;
	}

	public void updateChunk(int chunkX, int chunkZ, boolean initialize) throws Exception {
		if(initialize) {
			// Send pre chunk packet.
			PacketWrapper pw = new PacketWrapperImpl(0x32, null, getUser());
			pw.write(Type.INT, chunkX);
			pw.write(Type.INT, chunkZ);
			pw.write(Type.UNSIGNED_BYTE, (short) 1);
			pw.send(Protocol1_2_1_3to1_1.class);
		}
		
		// Send map chunk data packet.
		int bitmask = 0;
		ChunkData data = getChunkAt(chunkX, chunkZ);
		ChunkSection[] sections = new ChunkSection[16];
		for(int y = 0; y < 128; y++) {
			ChunkSection section = sections[y >> 4];
			if(section == null) {
				section = new ChunkSectionImpl(true);
				sections[y >> 4] = section;
				ChunkSectionLight light = new ChunkSectionLightImpl();

				light.setBlockLight(new byte[2048]);
				light.setSkyLight(new byte[2048]);
				section.setLight(light);
			}
			for(int z = 0; z < 16; z++) {
				for(int x = 0; x < 16; x++) {
					int id = data.getBlockId(x, y, z);
					if(id > 0) {
						bitmask |= (1 << (y >> 4));
					}
					section.setBlockWithData(x, y & 0xF, z, id, data.getMetadata(x, y, z));
					section.getLight().getBlockLightNibbleArray().set(x, y & 0xF, z, data.getBlocklight(x, y, z));
					section.getLight().getSkyLightNibbleArray().set(x, y & 0xF, z, data.getSkylight(x, y, z));
				}
			}
		}
		
		Chunk chunk = new BaseChunk(chunkX, chunkZ, initialize, false, bitmask, sections, new int[256],
				new ArrayList<>());
		PacketWrapper pw = new PacketWrapperImpl(0x33, Unpooled.buffer(), getUser());
		ChunkPacketWriter.writeChunkPacket(pw, chunk);
		pw.send(Protocol1_2_1_3to1_1.class);
	}

	public void updateBlocks(int x, short y, int z, int xSize, int ySize, int zSize, byte[] data, int size) throws Exception {
		int chunkX = x >> 4;
		int chunkZ = z >> 4;
		int xEnd = (x + xSize) - 1 >> 4;
		int zEnd = (z + zSize) - 1 >> 4;
		int byteOffset = 0;
		int yStart = y;
		int i3 = y + ySize;
		if (yStart < 0) {
			yStart = 0;
		}
		if (i3 > ChunkData.MAX_HEIGHT) {
			i3 = ChunkData.MAX_HEIGHT;
		}
		for (int xPos = chunkX; xPos <= xEnd; xPos++) {
			int relX = x - xPos * 16;
			int l3 = (x + xSize) - xPos * 16;
			if (relX < 0) {
				relX = 0;
			}
			if (l3 > 16) {
				l3 = 16;
			}
			for (int zPos = chunkZ; zPos <= zEnd; zPos++) {
				int relZ = z - zPos * 16;
				int k4 = (z + zSize) - zPos * 16;
				if (relZ < 0) {
					relZ = 0;
				}
				if (k4 > 16) {
					k4 = 16;
				}
				ChunkData chunk = getChunkAt(xPos, zPos);
				boolean wasLoaded = true;
				if(chunk == null) {
					chunk = new ChunkData(xPos, zPos);
					loadChunk(chunkX, chunkZ, chunk);
					wasLoaded = false;
				}
				byteOffset = chunk.updateChunk(data, relX, yStart, relZ, l3, i3, k4, byteOffset);
				updateChunk(xPos, zPos, !wasLoaded);
			}
		}
	}
}
