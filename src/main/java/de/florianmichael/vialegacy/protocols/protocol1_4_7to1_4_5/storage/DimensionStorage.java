package de.florianmichael.vialegacy.protocols.protocol1_4_7to1_4_5.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class DimensionStorage extends StoredObject {
	
	private int dimension;
	
	public DimensionStorage(UserConnection user) {
		super(user);
	}
	
	public int getDimension() {
		return dimension;
	}
	
	public void setDimension(int dimension) {
		this.dimension = dimension;
	}
}
