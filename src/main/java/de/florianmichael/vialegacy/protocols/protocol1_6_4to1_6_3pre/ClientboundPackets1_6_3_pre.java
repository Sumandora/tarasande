package de.florianmichael.vialegacy.protocols.protocol1_6_4to1_6_3pre;

import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion;
import de.florianmichael.vialegacy.protocol.splitter.IPacketSplitter;
import de.florianmichael.vialegacy.api.LegacyClientboundPacketType;

public enum ClientboundPackets1_6_3_pre implements LegacyClientboundPacketType {

	KEEP_ALIVE(0x0, (buffer, transformer) -> buffer.readInt()),
	JOIN_GAME(0x01, (buffer, transformer) -> {
		buffer.readInt();

		transformer.readString(buffer);

		buffer.readByte();
		buffer.readByte();
		buffer.readByte();
		buffer.readByte();
		buffer.readByte();
	}),
	CHAT_MESSAGE(0x03, (buffer, transformer) -> transformer.readString(buffer)),
	TIME_UPDATE(0x04, (buffer, transformer) -> {
		buffer.readLong();
		buffer.readLong();
	}),
	ENTITY_EQUIPMENT(0x05, (buffer, transformer) -> {
		buffer.readInt();

		buffer.readShort();

		transformer.read1_7_10_ItemStack(buffer);
	}),
	SPAWN_POSITION(0x06, (buffer, transformer) -> {
		buffer.readInt();
		buffer.readInt();
		buffer.readInt();
	}),
	UPDATE_HEALTH(0x08, (buffer, transformer) -> {
		buffer.readFloat();
		buffer.readShort();
		buffer.readFloat();
	}),
	RESPAWN(0x09, ((buffer, transformer) -> {
		buffer.readInt();

		buffer.readByte();
		buffer.readByte();

		buffer.readShort();

		transformer.readString(buffer);
	})),
	PLAYER_POSITION(0x0D, ((buffer, transformer) -> {
		buffer.readDouble();
		buffer.readDouble();
		buffer.readDouble();
		buffer.readDouble();

		buffer.readFloat();
		buffer.readFloat();

		buffer.readUnsignedByte();
	})),
	HELD_ITEM_CHANGE(0x10, (((buffer, transformer) -> buffer.readShort()))),
	USE_BED(0x11, ((buffer, transformer) -> {
		buffer.readInt();

		buffer.readByte();
		buffer.readInt();

		buffer.readByte();
		buffer.readInt();
	})),
	ENTITY_ANIMATION(0x12, ((buffer, transformer) -> {
		buffer.readInt();
		buffer.readByte();
	})),
	SPAWN_PLAYER(0x14, ((buffer, transformer) -> {
		buffer.readInt();

		transformer.readString(buffer);

		buffer.readInt();
		buffer.readInt();
		buffer.readInt();

		buffer.readByte();
		buffer.readByte();
		buffer.readShort();

		transformer.read1_6_4_MetadataList(buffer);
	})),
	COLLECT_ITEM(0x16, ((buffer, transformer) -> {
		buffer.readInt();
		buffer.readInt();
	})),
	SPAWN_ENTITY(0x17, ((buffer, transformer) -> {
		buffer.readInt();

		buffer.readByte();

		buffer.readInt();
		buffer.readInt();
		buffer.readInt();

		buffer.readByte();
		buffer.readByte();

		final int x = buffer.readInt();

		if (x > 0) {
			buffer.readShort();
			buffer.readShort();
			buffer.readShort();
		}
	})),
	SPAWN_MOB(0x18, ((buffer, transformer) -> {
		buffer.readInt();

		buffer.readByte();

		buffer.readInt();
		buffer.readInt();
		buffer.readInt();

		buffer.readByte();
		buffer.readByte();
		buffer.readByte();

		buffer.readShort();
		buffer.readShort();
		buffer.readShort();

		transformer.read1_6_4_MetadataList(buffer);
	})),
	SPAWN_PAINTING(0x19, ((buffer, transformer) -> {
		buffer.readInt();

		transformer.readString(buffer);

		buffer.readInt();
		buffer.readInt();
		buffer.readInt();
		buffer.readInt();
	})),
	SPAWN_EXPERIENCE_ORB(0x1A, ((buffer, transformer) -> {
		buffer.readInt();
		buffer.readInt();
		buffer.readInt();
		buffer.readInt();

		buffer.readShort();
	})),
	ENTITY_VELOCITY(0x1C, ((buffer, transformer) -> {
		buffer.readInt();

		buffer.readShort();
		buffer.readShort();
		buffer.readShort();
	})),
	DESTROY_ENTITIES(0x1D, ((buffer, transformer) -> {
		final int x = buffer.readUnsignedByte();

		for (int i = 0; i < x; i++)
			buffer.readInt();
	})),
	ENTITY_MOVEMENT(0x1E, ((buffer, transformer) -> buffer.readInt())),
	ENTITY_POSITION(0x1F, ((buffer, transformer) -> {
		buffer.readInt();

		buffer.readByte();
		buffer.readByte();
		buffer.readByte();
	})),
	ENTITY_ROTATION(0x20, ((buffer, transformer) -> {
		buffer.readInt();

		buffer.readByte();
		buffer.readByte();
	})),
	ENTITY_POSITION_AND_ROTATION(0x21, ((buffer, transformer) -> {
		buffer.readInt();

		buffer.readByte();
		buffer.readByte();
		buffer.readByte();
		buffer.readByte();
		buffer.readByte();
	})),
	ENTITY_TELEPORT(0x22, ((buffer, transformer) -> {
		buffer.readInt();
		buffer.readInt();
		buffer.readInt();
		buffer.readInt();

		buffer.readByte();
		buffer.readByte();
	})),
	ENTITY_HEAD_LOOK(0x23, ((buffer, transformer) -> {
		buffer.readInt();

		buffer.readByte();
	})),
	ENTITY_STATUS(0x26, ((buffer, transformer) -> {
		buffer.readInt();

		buffer.readByte();
	})),
	ATTACH_ENTITY(0x27, ((buffer, transformer) -> {
		buffer.readInt();
		buffer.readInt();

		buffer.readUnsignedByte();
	})),
	ENTITY_METADATA(0x28, ((buffer, transformer) -> {
		buffer.readInt();

		transformer.read1_6_4_MetadataList(buffer);
	})),
	ENTITY_EFFECT(0x29, ((buffer, transformer) -> {
		buffer.readInt();

		buffer.readByte();
		buffer.readByte();

		buffer.readShort();
	})),
	REMOVE_ENTITY_EFFECT(0x2A, ((buffer, transformer) -> {
		buffer.readInt();

		buffer.readByte();
	})),
	SET_EXPERIENCE(0x2B, ((buffer, transformer) -> {
		buffer.readFloat();

		buffer.readShort();
		buffer.readShort();
	})),
	ENTITY_PROPERTIES(0x2C, ((buffer, transformer) -> {
		buffer.readInt();

		final int x = buffer.readInt();

		for (int i = 0; i < x; i++) {
			transformer.readString(buffer);

			buffer.readDouble();

			final int s = buffer.readUnsignedShort();

			for (int k = 0; k < s; k++) {
				buffer.readLong();
				buffer.readLong();

				buffer.readDouble();

				buffer.readByte();
			}
		}
	})),
	CHUNK_DATA(0x33, ((buffer, transformer) -> {
		buffer.readInt();
		buffer.readInt();

		buffer.readBoolean();

		buffer.readShort();
		buffer.readShort();

		final int x = buffer.readInt();

		for (int i = 0; i < x; i++)
			buffer.readByte();
	})),
	MULTI_BLOCK_CHANGE(0x34, ((buffer, transformer) -> {
		buffer.readInt();
		buffer.readInt();

		buffer.readShort();

		final int x = buffer.readInt();

		if (x > 0)
			for (int i = 0; i < x; i++)
				buffer.readByte();
	})),
	BLOCK_CHANGE(0x35, ((buffer, transformer) -> {
		buffer.readInt();
		buffer.readUnsignedByte();

		buffer.readInt();
		buffer.readShort();
		buffer.readUnsignedByte();
	})),
	BLOCK_ACTION(0x36, ((buffer, transformer) -> {
		buffer.readInt();

		buffer.readShort();

		buffer.readInt();

		buffer.readUnsignedByte();
		buffer.readUnsignedByte();

		buffer.readShort();
	})),
	BLOCK_BREAK_ANIMATION(0x37, ((buffer, transformer) -> {
		buffer.readInt();
		buffer.readInt();
		buffer.readInt();
		buffer.readInt();

		buffer.readUnsignedByte();
	})),
	MAP_BULK_CHUNK(0x38, ((buffer, transformer) -> {
		final int y = buffer.readShort();
		final int x = buffer.readInt();

		buffer.readBoolean();

		for (int i = 0; i < x; i++)
			buffer.readByte();

		for (int i = 0; i < y; i++) {
			buffer.readInt();
			buffer.readInt();

			buffer.readShort();
			buffer.readShort();
		}
	})),
	EXPLOSION(0x3C, ((buffer, transformer) -> {
		buffer.readDouble();
		buffer.readDouble();
		buffer.readDouble();

		buffer.readFloat();

		final int x = buffer.readInt();

		for (int i = 0; i < x; i++) {
			buffer.readByte();
			buffer.readByte();
			buffer.readByte();
		}

		buffer.readFloat();
		buffer.readFloat();
		buffer.readFloat();
	})),
	EFFECT(0x3D, ((buffer, transformer) -> {
		buffer.readInt();
		buffer.readInt();

		buffer.readByte();

		buffer.readInt();
		buffer.readInt();

		buffer.readBoolean();
	})),
	NAMED_SOUND(0x3E, ((buffer, transformer) -> {
		transformer.readString(buffer);

		buffer.readInt();
		buffer.readInt();
		buffer.readInt();

		buffer.readFloat();

		buffer.readUnsignedByte();
	})),
	SPAWN_PARTICLE(0x3F, ((buffer, transformer) -> {
		transformer.readString(buffer);

		buffer.readFloat();
		buffer.readFloat();
		buffer.readFloat();
		buffer.readFloat();
		buffer.readFloat();
		buffer.readFloat();
		buffer.readFloat();

		buffer.readInt();
	})),
	GAME_EVENT(0x46, ((buffer, transformer) -> {
		buffer.readByte();
		buffer.readByte();
	})),
	SPAWN_GLOBAL_ENTITY(0x47, ((buffer, transformer) -> {
		buffer.readInt();

		buffer.readByte();

		buffer.readInt();
		buffer.readInt();
		buffer.readInt();
	})),
	OPEN_WINDOW(0x64, ((buffer, transformer) -> {
		buffer.readByte();

		final int x = buffer.readByte();

		transformer.readString(buffer);
		buffer.readByte();
		buffer.readBoolean();

		if (x == 11)
			buffer.readInt();
	})),
	CLOSE_WINDOW(0x65, ((buffer, transformer) -> buffer.readByte())),
	SET_SLOT(0x67, ((buffer, transformer) -> {
		buffer.readByte();

		buffer.readShort();

		transformer.read1_7_10_ItemStack(buffer);
	})),
	CREATIVE_INVENTORY_ACTION(0x6B, (buffer, transformer) -> {
		buffer.readShort();

		transformer.read1_7_10_ItemStack(buffer);
	}),
	WINDOW_ITEMS(0x68, ((buffer, transformer) -> {
		buffer.readByte();

		final int x = buffer.readShort();

		for (int i = 0; i < x; i++)
			transformer.read1_7_10_ItemStack(buffer);
	})),
	WINDOW_PROPERTY(0x69, ((buffer, transformer) -> {
		buffer.readByte();

		buffer.readShort();
		buffer.readShort();
	})),
	WINDOW_CONFIRMATION(0x6A, ((buffer, transformer) -> {
		buffer.readByte();

		buffer.readShort();

		buffer.readByte();
	})),
	UPDATE_SIGN(0x82, ((buffer, transformer) -> {
		buffer.readInt();

		buffer.readShort();

		buffer.readInt();

		transformer.readString(buffer);
		transformer.readString(buffer);
		transformer.readString(buffer);
		transformer.readString(buffer);
	})),
	MAP_DATA(0x83, ((buffer, transformer) -> {
		buffer.readShort();
		buffer.readShort();

		final int x = buffer.readUnsignedShort();

		for (int i = 0; i < x; i++) {
			buffer.readByte();
		}
	})),
	BLOCK_ENTITY_DATA(0x84, ((buffer, transformer) -> {
		buffer.readInt();
		buffer.readShort();

		buffer.readInt();
		buffer.readByte();

		transformer.readNbt(buffer);
	})),
	OPEN_SIGN_EDITOR(0x85, ((buffer, transformer) -> {
		buffer.readByte();

		buffer.readInt();
		buffer.readInt();
		buffer.readInt();
	})),
	STATISTICS(0xC8, (buffer, transformer) -> {
		buffer.readInt();
		buffer.readInt();
	}),
	PLAYER_INFO(0xC9, ((buffer, transformer) -> {
		transformer.readString(buffer);

		buffer.readByte();

		buffer.readShort();
	})),
	PLAYER_ABILITIES(0xCA, ((buffer, transformer) -> {
		buffer.readUnsignedByte();

		buffer.readFloat();
		buffer.readFloat();
	})),
	TAB_COMPLETE(0xCB, ((buffer, transformer) -> transformer.readString(buffer))),
	SCOREBOARD_OBJECTIVE(0xCE, ((buffer, transformer) -> {
		transformer.readString(buffer);
		transformer.readString(buffer);

		buffer.readByte();
	})),
	UPDATE_SCORE(0xCF, ((buffer, transformer) -> {
		transformer.readString(buffer);

		final byte b = buffer.readByte();

		if (b != 1) {
			transformer.readString(buffer);
			buffer.readInt();
		}
	})),
	DISPLAY_SCOREBOARD(0xD0, ((buffer, transformer) -> {
		buffer.readByte();

		transformer.readString(buffer);
	})),
	TEAMS(0xD1, (buffer, transformer) -> {
		transformer.readString(buffer);

		final int b = buffer.readByte();

		if (b == 0 || b == 2) {
			transformer.readString(buffer);
			transformer.readString(buffer);
			transformer.readString(buffer);
			buffer.readByte();
		}
		if (b == 0 || b == 3 || b == 4) {
			final int x = buffer.readShort();

			for (int i = 0; i < x; i++)
				transformer.readString(buffer);
		}
	}),
	PLUGIN_MESSAGE(0xFA, ((buffer, transformer) -> {
		transformer.readString(buffer);

		final short s = buffer.readShort();

		for (int i = 0; i < s; i++)
			buffer.readByte();
	})),
	DISCONNECT(0xFF, ((buffer, transformer) -> transformer.readString(buffer)));

	private final int id;
	private final IPacketSplitter splitter;

	ClientboundPackets1_6_3_pre(final int id, final IPacketSplitter splitter) {
		this.id = id;
		this.splitter = splitter;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return name();
	}

	@Override
	public IPacketSplitter getSplitter() {
		return splitter;
	}
}
