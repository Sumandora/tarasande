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

package de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10;

import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;

public enum ServerboundPackets1_7_6_10 implements ServerboundPacketType {

	KEEP_ALIVE, // 0x00
	CHAT_MESSAGE, // 0x01
	INTERACT_ENTITY, // 0x02
	PLAYER_MOVEMENT, // 0x03
	PLAYER_POSITION, // 0x04
	PLAYER_ROTATION, // 0x05
	PLAYER_POSITION_AND_ROTATION, // 0x06
	PLAYER_DIGGING, // 0x07
	PLAYER_BLOCK_PLACEMENT, // 0x08
	HELD_ITEM_CHANGE, // 0x09
	ANIMATION, // 0x0A
	ENTITY_ACTION, // 0x0B
	STEER_VEHICLE, // 0x0C
	CLOSE_WINDOW, // 0x0D
	CLICK_WINDOW, // 0x0E
	WINDOW_CONFIRMATION, // 0x0F
	CREATIVE_INVENTORY_ACTION, // 0x10
	CLICK_WINDOW_BUTTON, // 0x11
	UPDATE_SIGN, // 0x12
	PLAYER_ABILITIES, // 0x13
	TAB_COMPLETE, // 0x14
	CLIENT_SETTINGS, // 0x15
	CLIENT_STATUS, // 0x16
	PLUGIN_MESSAGE; // 0x17

	@Override
	public int getId() {
		return ordinal();
	}

	@Override
	public String getName() {
		return name();
	}

}