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
/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.model.SkinProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TabListTracker extends StoredObject {

	private final ArrayList<TabListEntry> tabListEntries = new ArrayList<>();

	public TabListTracker(UserConnection user) {
		super(user);
	}

	public TabListEntry getTabListEntry(String name) {
		for (TabListEntry entry : tabListEntries) {
			if (name.equals(entry.name)) {
				return entry;
			}
		}
		return null;
	}

	public void remove(TabListEntry entry) {
		tabListEntries.remove(entry);
	}

	public void add(TabListEntry entry) {
		tabListEntries.add(entry);
	}

	public static class TabListEntry {
		public String name;
		public String displayName;
		public UUID uuid;
		public int ping;
		public List<SkinProperty> properties = new ArrayList<>();

		public TabListEntry(String name, UUID uuid) {
			this.name = name;
			this.uuid = uuid;
		}
	}
}
