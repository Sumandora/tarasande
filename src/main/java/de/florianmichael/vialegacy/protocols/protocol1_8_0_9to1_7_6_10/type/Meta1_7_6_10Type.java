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

package de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type;

import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.type.Type;

public enum Meta1_7_6_10Type implements MetaType {

	Byte(0, Type.BYTE),
	Short(1, Type.SHORT),
	Int(2, Type.INT),
	Float(3, Type.FLOAT),
	String(4, Type.STRING),
	Slot(5, Types1_7_6_10.COMPRESSED_NBT_ITEM),
	Position(6, Type.VECTOR),
	NonExistent(-1, Type.NOTHING);

	private final int typeID;
	private final Type type;

	public static Meta1_7_6_10Type byId(int id) {
		return values()[id];
	}

	Meta1_7_6_10Type(int typeID, Type type) {
		this.typeID = typeID;
		this.type = type;
	}

	public int typeId() {
		return this.typeID;
	}

	public Type type() {
		return this.type;
	}
}
