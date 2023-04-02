package com.mcf.davidee.nbtedit;

import com.mcf.davidee.nbtedit.nbt.NBTNodeSorter;
import com.mcf.davidee.nbtedit.nbt.NBTTree;
import com.mcf.davidee.nbtedit.nbt.NamedNBT;
import com.mcf.davidee.nbtedit.nbt.SaveStates;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * The NBTEdit Mod class - woo singletons. Sorry if you're reading through this mod, its formatting is very
 * odd and the code very messy. Hopefully someday this can be ported to GuiLib to provide much more flexibility.
 * 
 * TODO: Beg for the old clientSideRequired and serverSideRequired flags. Accepting all remote versions is stupid.
 */
public class NBTEdit {
	
	private static final String SEP = System.getProperty("line.separator");
	public static final NBTNodeSorter SORTER = new NBTNodeSorter();
	public static final char SECTION_SIGN = '\u00A7';
	
	private static FileHandler logHandler = null;
	private static Logger logger = Logger.getLogger("NBTEdit");
	
	public static NamedNBT clipboard = null;
	public static boolean opOnly = true;

	private static NBTEdit instance = new NBTEdit() {
		{
			init();
		}
	};
	
	public static void log(Level l, String s){
		logger.log(l, s);
	}
	
	public static void throwing(String cls, String mthd, Throwable thr){
		logger.throwing(cls, mthd, thr);
	}
	
	public static void logTag(NbtCompound tag){
		NBTTree tree = new NBTTree(tag);
		String sb = "";
		for (String s : tree.toStrings()){
			sb += SEP + "\t\t\t"+ s;
		}
		NBTEdit.log(Level.FINE, sb);
	}
	
	public static SaveStates getSaveStates(){
		return instance.saves;
	}
	
	private static SaveStates saves;

	public static void init() {
		logger.setLevel(Level.ALL);
		
		try {
			File logfile = new File(MinecraftClient.getInstance().runDirectory, "NBTEdit.log");
			if ((logfile.exists() || logfile.createNewFile()) && logfile.canWrite() && logHandler == null)
			{
				logHandler = new FileHandler(logfile.getPath());
				logHandler.setFormatter(new LogFormatter());
				logger.addHandler(logHandler);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		logger.fine("NBTEdit Initalized");
		saves = new SaveStates(new File(new File(MinecraftClient.getInstance().runDirectory,"saves"),"NBTEdit.dat"));
	}
	
}
