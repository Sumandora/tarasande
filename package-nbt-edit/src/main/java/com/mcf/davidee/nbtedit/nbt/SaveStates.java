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

import com.mcf.davidee.nbtedit.NBTEdit;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

// This save format can definitely be improved. Also, this can be extended to provide infinite save slots - just
// need to add some scrollbar (use GuiLib!).
public class SaveStates {

	private File file;
	private SaveState[] tags;

	public SaveStates(File file){
		this.file = file;
		tags = new SaveState[7];
		for (int i =0; i < 7; ++i)
			tags[i] = new SaveState("Slot " + (i+1));
	}
	
	public void read() throws IOException{
		if (file.exists() && file.canRead()){
			NbtCompound root = NbtIo.read(file);
			for (int i =0; i < 7; ++i){
				String name = "slot" + (i+1);
				if (root.contains(name))
					tags[i].tag = root.get(name);
				if (root.contains(name+"Name"))
					tags[i].name = root.getString(name+"Name");
			}
		}
	}
	
	public void write() throws IOException{
		NbtCompound root = new NbtCompound();
		for (int i = 0; i <7; ++i){
			root.put("slot" + (i+1), tags[i].tag);
			root.putString("slot" + (i+1)+"Name", tags[i].name);
		}
		NbtIo.write(root, file);
	}
	
	public void save(){
		try {
			write();
			NBTEdit.log(Level.FINE,"NBTEdit saved successfully.");
		}
		catch(IOException e){
			NBTEdit.log(Level.WARNING, "Unable to write NBTEdit save.");
			NBTEdit.throwing("SaveStates", "save", e);
		}
	}
	
	public void load(){
		try {
			read();
			NBTEdit.log(Level.FINE,"NBTEdit save loaded successfully.");
		}
		catch(IOException e){
			NBTEdit.log(Level.WARNING, "Unable to read NBTEdit save.");
			NBTEdit.throwing("SaveStates", "load", e);
		}
	}
	
	public SaveState getSaveState(int index){
		return tags[index];
	}

	public static final class SaveState{
		public String name;
		public NbtElement tag;

		public SaveState(String name){
			this.name = name;
			this.tag = new NbtCompound();
		}
	}
}
