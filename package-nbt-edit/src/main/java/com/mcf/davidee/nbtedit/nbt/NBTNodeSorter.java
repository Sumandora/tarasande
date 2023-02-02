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

package com.mcf.davidee.nbtedit.nbt;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.Comparator;

public class NBTNodeSorter implements Comparator<Node<NamedNBT>>{

	@Override
	public int compare(Node<NamedNBT> a, Node<NamedNBT> b) {
		NbtElement n1 = a.getObject().getNBT(), n2 = b.getObject().getNBT();
		String s1 = a.getObject().getName(), s2 = b.getObject().getName();
		if (n1 instanceof NbtCompound || n1 instanceof NbtList){
			if (n2 instanceof NbtCompound || n2 instanceof NbtList){
				int dif = n1.getType() - n2.getType();
				return (dif == 0) ? s1.compareTo(s2) : dif;
			}
			return 1;
		}
		if (n2 instanceof NbtCompound || n2 instanceof NbtList)
			return -1;
		int dif =n1.getType() - n2.getType();
		return (dif == 0) ? s1.compareTo(s2) : dif;
	}

}
