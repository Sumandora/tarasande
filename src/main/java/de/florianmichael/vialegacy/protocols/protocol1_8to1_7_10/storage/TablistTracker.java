/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 08.04.22, 17:47
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TablistTracker extends StoredObject {

	private ArrayList<TabListEntry> tablist = new ArrayList<>();

	public TablistTracker(UserConnection user) {
		super(user);
	}

	public TabListEntry getTabListEntry(String name) {
		for (TabListEntry entry : tablist) if (name.equals(entry.name)) return entry;
		return null;
	}

	public TabListEntry getTabListEntry(UUID uuid) {
		for (TabListEntry entry : tablist) if (uuid.equals(entry.uuid)) return entry;
		return null;
	}

	public void remove(TabListEntry entry) {
		tablist.remove(entry);
	}

	public void add(TabListEntry entry) {
		tablist.add(entry);
	}

	public static boolean shouldUpdateDisplayName(String oldName, String newName) {
		return oldName == null && newName != null || oldName != null && newName == null || oldName != null && !oldName.equals(newName);
	}

	public static class TabListEntry {
		public String name;
		public String displayName;
		public UUID uuid;
		public int ping;
		public List<Property> properties = new ArrayList<>();

		public TabListEntry(String name, UUID uuid) {
			this.name = name;
			this.uuid = uuid;
		}
	}

	public static class Property {
		public String name;
		public String value;
		public String signature;

		public Property(String name, String value, String signature) {
			this.name = name;
			this.value = value;
			this.signature = signature;
		}
	}
}
