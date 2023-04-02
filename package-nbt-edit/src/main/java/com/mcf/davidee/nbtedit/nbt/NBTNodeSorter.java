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
