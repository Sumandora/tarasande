package com.mcf.davidee.nbtedit.nbt;

import com.mcf.davidee.nbtedit.NBTEdit;
import com.mcf.davidee.nbtedit.NBTHelper;
import com.mcf.davidee.nbtedit.NBTStringHelper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.*;

public class NBTTree {
	
	private NbtCompound baseTag;
	
	private Node<NamedNBT> root;
	
	public NBTTree (NbtCompound tag){
		baseTag = tag;
		construct();
	}
	public Node<NamedNBT> getRoot(){
		return root;
	}
	
	public boolean canDelete(Node<NamedNBT> node){
		return node != root;
	}
	
	public boolean delete(Node<NamedNBT> node){
		if (node == null || node == root)
			return false;
		return deleteNode(node,root);
	}
	
	private boolean deleteNode(Node<NamedNBT> toDelete, Node<NamedNBT> cur){
		for (Iterator<Node<NamedNBT>> it = cur.getChildren().iterator(); it.hasNext();){
			Node<NamedNBT> child = it.next();
			if (child == toDelete){
				it.remove();
				return true;
			}
			boolean flag = deleteNode(toDelete,child);
			if (flag)
				return true;
		}
		return false;
	}
	
	
	private void construct() {
		root = new Node<NamedNBT>(new NamedNBT("ROOT", (NbtCompound)baseTag.copy()));
		addChildrenToTree(root);
		sort(root);
	}
	
	public void sort(Node<NamedNBT> node) {
		Collections.sort(node.getChildren(), NBTEdit.SORTER);
		for (Node<NamedNBT> c : node.getChildren())
			sort(c);
	}
	
	public void addChildrenToTree(Node<NamedNBT> parent){
		NbtElement tag = parent.getObject().getNBT();
		if (tag instanceof NbtCompound){
			Map<String,NbtElement> map =  NBTHelper.getMap((NbtCompound)tag);
			for (Map.Entry<String,NbtElement> entry : map.entrySet()){
				NbtElement base = entry.getValue();
				Node<NamedNBT> child = new Node<NamedNBT>(parent, new NamedNBT(entry.getKey(), base));
				parent.addChild(child);
				addChildrenToTree(child);
			}
			
		}
		else if (tag instanceof NbtList){
			NbtList list = (NbtList)tag;
			for (int i =0; i < list.size(); ++ i){
				NbtElement base = NBTHelper.getTagAt(list, i);
				Node<NamedNBT> child = new Node<NamedNBT>(parent, new NamedNBT(base));
				parent.addChild(child);
				addChildrenToTree(child);
			}
		}
	}
	
	public NbtCompound toNBTTagCompound(){
		NbtCompound tag = new NbtCompound();
		addChildrenToTag(root, tag);
		return tag;
	}
	
	public void addChildrenToTag (Node<NamedNBT> parent, NbtCompound tag){
		for (Node<NamedNBT> child : parent.getChildren()){
			NbtElement base = child.getObject().getNBT();
			String name = child.getObject().getName();
			if (base instanceof NbtCompound){
				NbtCompound newTag = new NbtCompound();
				addChildrenToTag(child, newTag);
				tag.put(name, newTag);
			}
			else if (base instanceof NbtList){
				NbtList list = new NbtList();
				addChildrenToList(child, list);
				tag.put(name, list);
			}
			else
				tag.put(name, base.copy());
		}
	}
	
	public void addChildrenToList(Node<NamedNBT> parent, NbtList list){
		for (Node<NamedNBT> child: parent.getChildren()){
			NbtElement base = child.getObject().getNBT();
			if (base instanceof NbtCompound){
				NbtCompound newTag = new NbtCompound();
				addChildrenToTag(child, newTag);
				list.add(newTag);
			}
			else if (base instanceof NbtList){
				NbtList newList = new NbtList();
				addChildrenToList(child, newList);
				list.add(newList);
			}
			else
				list.add(base.copy());
		}
	}
	
	public void print(){
		print(root,0);
	}
	
	private void print(Node<NamedNBT> n, int i){
		System.out.println(repeat("\t",i) + NBTStringHelper.getNBTName(n.getObject()));
		for (Node<NamedNBT> child : n.getChildren())
			print(child,i+1);
	}
	
	public List<String> toStrings(){
		List<String> s = new ArrayList<String>();
		toStrings(s,root,0);
		return s;
	}
	
	private void toStrings(List<String> s, Node<NamedNBT> n, int i){
		s.add(repeat("   ",i) + NBTStringHelper.getNBTName(n.getObject()));
		for (Node<NamedNBT> child : n.getChildren())
			toStrings(s,child,i+1);
	}
	
	public static String repeat(String c, int i){
		StringBuilder b = new StringBuilder(i+1);
		for (int j =0; j < i; ++ j)
			b.append(c);
		return b.toString();
	}
}
