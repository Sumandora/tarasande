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
