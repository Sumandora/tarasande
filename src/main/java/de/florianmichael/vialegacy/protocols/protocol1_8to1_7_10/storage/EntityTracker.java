/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 08.04.22, 20:42
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

package de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.ClientboundPackets1_7_10;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.Protocol1_8to1_7_10;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.metadata.MetadataRewriter1_8to1_7_10;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityTracker extends StoredObject {

	private final Map<Integer, Entity1_10Types.EntityType> clientEntityTypes = new ConcurrentHashMap<>();
	private final Map<Integer, List<Metadata>> metadataBuffer = new ConcurrentHashMap<>();
	private final Protocol1_8to1_7_10 protocol1_8to1_7_10;

	public EntityTracker(final UserConnection user, final Protocol1_8to1_7_10 protocol1_8to1_7_10) {
		super(user);
		this.protocol1_8to1_7_10 = protocol1_8to1_7_10;
	}

	public void removeEntity(int entityId) {
		clientEntityTypes.remove(entityId);
	}

	public Map<Integer, Entity1_10Types.EntityType> getClientEntityTypes() {
		return this.clientEntityTypes;
	}

	public void addMetadataToBuffer(int entityID, List<Metadata> metadataList) {
		if (this.metadataBuffer.containsKey(entityID)) {
			this.metadataBuffer.get(entityID).addAll(metadataList);
		} else if (!metadataList.isEmpty()) {
			this.metadataBuffer.put(entityID, metadataList);
		}
	}

	public void sendMetadataBuffer(int entityId) {
		if (!this.metadataBuffer.containsKey(entityId)) return;
		final PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_7_10.ENTITY_METADATA, this.getUser());
		wrapper.write(Type.VAR_INT, entityId);
		wrapper.write(Types1_8.METADATA_LIST, this.metadataBuffer.get(entityId));
		if (!this.metadataBuffer.get(entityId).isEmpty()) {
			try {
				wrapper.send(Protocol1_8to1_7_10.class);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		this.metadataBuffer.remove(entityId);
	}
}
