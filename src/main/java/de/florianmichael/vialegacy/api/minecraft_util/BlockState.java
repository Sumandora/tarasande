package de.florianmichael.vialegacy.api.minecraft_util;

public class BlockState {

	private final int id;
	private final byte metadata;

	public BlockState(int id, byte metadata) {
		this.id = id;
		this.metadata = metadata;
	}

	public int getId() {
		return id;
	}

	public byte getMetadata() {
		return metadata;
	}

	public static int extractId(int raw) {
		return raw >> 4;
	}

	public static int extractData(int raw) {
		return raw & 0xF;
	}

	public static int stateToRaw(int id, int data) {
		return (id << 4) | (data & 0xF);
	}
}
