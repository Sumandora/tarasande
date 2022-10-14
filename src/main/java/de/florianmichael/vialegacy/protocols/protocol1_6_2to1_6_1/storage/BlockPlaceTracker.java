package de.florianmichael.vialegacy.protocols.protocol1_6_2to1_6_1.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class BlockPlaceTracker extends StoredObject {
	
	private long time;
	private int lastX;
	private int lastY;
	private int lastZ;
	
	public BlockPlaceTracker(UserConnection user) {
		super(user);
	}
	
	public int getLastX() {
		return lastX;
	}
	
	public void setLastX(int lastX) {
		this.lastX = lastX;
	}
	
	public int getLastY() {
		return lastY;
	}
	
	public void setLastY(int lastY) {
		this.lastY = lastY;
	}
	
	public int getLastZ() {
		return lastZ;
	}
	
	public void setLastZ(int lastZ) {
		this.lastZ = lastZ;
	}
	
	public long getTime() {
		return time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
}
