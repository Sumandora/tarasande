package de.florianmichael.vialegacy.protocols.protocol1_5_1to1_4_7;

import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;

public enum ServerboundPackets1_4_7 implements ServerboundPacketType {

	KEEP_ALIVE(0x00),
	CHAT_MESSAGE(0x03),
	INTERACT_ENTITY(0x07),
	PLAYER_MOVEMENT(0x0A),
	PLAYER_POSITION(0x0B),
	PLAYER_ROTATION(0x0C),
	PLAYER_POSITION_AND_ROTATION(0x0D),
	PLAYER_DIGGING(0x0E),
	PLAYER_BLOCK_PLACEMENT(0x0F),
	HELD_ITEM_CHANGE(0x10),
	ANIMATION(0x12),
	ENTITY_ACTION(0x13),
	STEER_VEHICLE(0x1B),
	CLOSE_WINDOW(0x65),
	CLICK_WINDOW(0x66),
	WINDOW_CONFIRMATION(0x6A),
	CREATIVE_INVENTORY_ACTION(0x6B),
	CLICK_WINDOW_BUTTON(0x6C),
	UPDATE_SIGN(0x82),
	PLAYER_ABILITIES(0xCA),
	TAB_COMPLETE(0xCB),
	CLIENT_SETTINGS(0xCC),
	CLIENT_STATUS(0xCD),
	PLUGIN_MESSAGE(0xFA);

	private final int id;

	ServerboundPackets1_4_7(final int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return name();
	}
}
