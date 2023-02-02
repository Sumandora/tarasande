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
