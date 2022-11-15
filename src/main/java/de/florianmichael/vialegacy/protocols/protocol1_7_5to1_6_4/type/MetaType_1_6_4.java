/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 24.06.22, 13:55
 *
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.0--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license.
 */

package de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.type;

import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.type.TypeRegistry1_7_6_10;

public enum MetaType_1_6_4 implements MetaType {
	
	Byte(0, Type.BYTE),
	Short(1, Type.SHORT),
	Int(2, Type.INT),
	Float(3, Type.FLOAT),
	String(4, Type.STRING),
	Slot(5, TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM),
	Position(6, Type.VECTOR),
	NonExistent(-1, Type.NOTHING);
	
	private final int typeID;
	private final Type type;

	public static MetaType_1_6_4 byId(int id) {
		return values()[id];
	}

	MetaType_1_6_4(int typeID, Type type) {
		this.typeID = typeID;
		this.type = type;
	}
	
	@Override
	public Type type() {
		return type;
	}

	@Override
	public int typeId() {
		return typeID;
	}
}
