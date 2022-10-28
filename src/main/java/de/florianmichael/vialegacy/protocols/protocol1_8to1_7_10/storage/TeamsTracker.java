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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class TeamsTracker extends StoredObject {

	private final HashMap<TeamsEntry, ArrayList<String>> teams = new HashMap<>();

	public TeamsTracker(UserConnection user) {
		super(user);
	}

	public TeamsEntry getTeamsEntry(String player) {
		for (Map.Entry<TeamsEntry, ArrayList<String>> entry : teams.entrySet())
			if (entry.getValue().contains(player))
				return entry.getKey();
		return null;
	}

	public ArrayList<String> getPlayers(TeamsEntry entry) {
		return teams.get(entry);
	}

	public TeamsEntry getByUniqueName(String uniqueName) {
		for (Map.Entry<TeamsEntry, ArrayList<String>> entry : teams.entrySet())
			if (entry.getKey().uniqueName.equals(uniqueName))
				return entry.getKey();
		return null;
	}

	public void putTeamsEntry(TeamsEntry entry, ArrayList<String> players) {
		teams.put(entry, players);
	}

	public void removeTeamsEntryIf(Predicate<Map.Entry<TeamsEntry, ArrayList<String>>> predicate) {
		teams.entrySet().removeIf(predicate);
	}

	public static class TeamsEntry {
		public String uniqueName;
		public String prefix;
		public String name;
		public String suffix;

		public TeamsEntry(String uniqueName, String prefix, String name, String suffix) {
			this.uniqueName = uniqueName;
			this.prefix = prefix;
			this.name = name;
			this.suffix = suffix;
		}

		public String concat(String player) {
			return prefix + player + suffix;
		}
	}
}
