/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 7/16/22, 12:37 AM
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

package com.mcf.davidee.nbtedit;

import com.google.common.base.Strings;
import com.mcf.davidee.nbtedit.nbt.NamedNBT;
import net.minecraft.nbt.*;

public class NBTStringHelper {

    public static final char SECTION_SIGN = '§';

    public static String getNBTName(NamedNBT namedNBT) {
        String name = namedNBT.getName();
        NbtElement obj = namedNBT.getNBT();

        String s = toString(obj);
        return Strings.isNullOrEmpty(name) ? "" + s : name + ": " + s;
    }

    public static String getNBTNameSpecial(NamedNBT namedNBT) {
        String name = namedNBT.getName();
        NbtElement obj = namedNBT.getNBT();

        String s = toString(obj);
        return Strings.isNullOrEmpty(name) ? "" + s : name + ": " + s + SECTION_SIGN + 'r';
    }

    public static NbtElement newTag(byte type) {
        switch (type) {
//		case 0:
//			return NbtE.INSTANCE;
            case 1:
                return NbtByte.of((byte) 0);
            case 2:
                return NbtShort.of((short) 0);
            case 3:
                return NbtInt.of(0);
            case 4:
                return NbtLong.of(0);
            case 5:
                return NbtFloat.of(0);
            case 6:
                return NbtDouble.of(0);
            case 7:
                return new NbtByteArray(new byte[0]);
            case 8:
                return NbtString.of("");
            case 9:
                return new NbtList();
            case 10:
                return new NbtCompound();
            case 11:
                return new NbtIntArray(new int[0]);
            case 12:
                return new NbtLongArray(new long[0]);
            default:
                return null;
        }
    }

    public static String toString(NbtElement base) {
        switch (base.getType()) {
            case 1:
                return "" + ((NbtByte) base).byteValue();
            case 2:
                return "" + ((NbtShort) base).shortValue();
            case 3:
                return "" + ((NbtInt) base).intValue();
            case 4:
                return "" + ((NbtLong) base).longValue();
            case 5:
                return "" + ((NbtFloat) base).floatValue();
            case 6:
                return "" + ((NbtDouble) base).doubleValue();
            case 7:
                return base.toString();
            case 8:
                return ((NbtString) base).asString();
            case 9:
                return "(TagList)";
            case 10:
                return "(TagCompound)";
            case 11:
            case 12:
                return base.toString();
            default:
                return "?";
        }
    }

    public static String getButtonName(byte id) {
        switch (id) {
            case 1:
                return "Byte";
            case 2:
                return "Short";
            case 3:
                return "Int";
            case 4:
                return "Long";
            case 5:
                return "Float";
            case 6:
                return "Double";
            case 7:
                return "Byte[]";
            case 8:
                return "String";
            case 9:
                return "List";
            case 10:
                return "Compound";
            case 11:
                return "Int[]";
            case 12:
                return "Long[]";
            case 13:
                return "Edit";
            case 14:
                return "Delete";
            case 15:
                return "Copy";
            case 16:
                return "Cut";
            case 17:
                return "Paste";
            default:
                return "Unknown";
        }
    }
}
