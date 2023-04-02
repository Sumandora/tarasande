package com.mcf.davidee.nbtedit.nbt;

import net.minecraft.nbt.NbtElement;

public class NamedNBT {
	
	protected String name;
	protected NbtElement nbt;
	
	public NamedNBT(NbtElement nbt) {
		this("", nbt);
	}
	
	public NamedNBT(String name, NbtElement nbt) {
		this.name = name;
		this.nbt = nbt;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public NbtElement getNBT() {
		return nbt;
	}
	
	public void setNBT(NbtElement nbt) {
		this.nbt = nbt;
	}
	
	public NamedNBT copy() {
		return new NamedNBT(name, nbt.copy());
	}
	
}
