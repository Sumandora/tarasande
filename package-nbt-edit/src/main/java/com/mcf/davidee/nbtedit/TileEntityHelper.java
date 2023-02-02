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

import net.minecraft.block.entity.BlockEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TileEntityHelper {
	
	public static <T extends BlockEntity> void copyData(T from, T into) throws Exception{
		Class<?> clazz = from.getClass();
		Set<Field> fields = asSet(clazz.getFields(),clazz.getDeclaredFields());
		Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		for (Field field : fields){
			field.setAccessible(true);
			modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			field.set(into, field.get(from));
		}
	}
	
	public static Set<Field> asSet(Field[] a, Field[] b){
		HashSet<Field> s = new HashSet<Field>();
		Collections.addAll(s, a);
		Collections.addAll(s, b);
		return s;
	}
	
}
