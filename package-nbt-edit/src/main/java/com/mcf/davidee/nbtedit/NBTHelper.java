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

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

public class NBTHelper {
	
	public static NbtCompound nbtRead(DataInputStream in) throws IOException {
		return NbtIo.read(in);
	}
	
	public static void nbtWrite(NbtCompound compound, DataOutput out) throws IOException {
		NbtIo.write(compound, out);
	}
	
	public static Map<String, NbtElement> getMap(NbtCompound tag){
		return getPrivateValue(NbtCompound.class, tag, 4);
	}
	
	public static NbtElement getTagAt(NbtList tag, int index) {
		return tag.get(index);
	}

	public static <T, E> T getPrivateValue(Class <? super E > classToAccess, E instance, int fieldIndex)
	{
		try
		{
			Field f = classToAccess.getDeclaredFields()[fieldIndex];
			f.setAccessible(true);
			return (T) f.get(instance);
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
