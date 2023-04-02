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
