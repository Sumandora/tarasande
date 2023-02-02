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

import java.util.ArrayList;
import java.util.List;

public class Node<T> {

	private List<Node<T>> children;
	
	private Node<T> parent;
	private T obj;
	
	private boolean drawChildren;
	
	public Node(){
		this((T)null);
	}
	
	public Node(T obj){
		children = new ArrayList<Node<T>>();
		this.obj = obj;
	}
	
	public boolean shouldDrawChildren(){
		return drawChildren;
	}
	
	public void setDrawChildren(boolean draw){
		drawChildren = draw;
	}
	
	public Node(Node<T> parent){
		this(parent,null);
	}
	
	public Node(Node<T> parent, T obj){
		this.parent = parent;
		children = new ArrayList<Node<T>>();
		this.obj = obj;
	}
	
	public void addChild(Node<T> n){
		children.add(n);
	}
	
	public boolean removeChild(Node<T> n){
		return children.remove(n);
	}
	
	public List<Node<T>> getChildren(){
		return children;
	}
	
	public Node<T> getParent(){
		return parent;
	}
	
	public T getObject(){
		return obj;
	}
	
	public String toString(){
		return "" + obj;
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}
	
	public boolean hasParent(){
		return parent != null;
	}

	
}
