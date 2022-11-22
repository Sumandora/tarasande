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
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.model.TeamsEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class TeamsTracker extends StoredObject {

	private final HashMap<TeamsEntry, List<String>> teams = new HashMap<>();

	public TeamsTracker(UserConnection user) {
		super(user);
	}

	public TeamsEntry getTeamsEntry(String player) {
		for (Map.Entry<TeamsEntry, List<String>> entry : teams.entrySet()) {
			if (entry.getValue().contains(player)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public List<String> getPlayers(TeamsEntry entry) {
		if (teams.containsKey(entry)) {
			return teams.get(entry);
		}
		return new ArrayList<>();
	}

	public TeamsEntry getByUniqueName(String uniqueName) {
		for (Map.Entry<TeamsEntry, List<String>> entry : teams.entrySet()) {
			if (entry.getKey().uniqueName.equals(uniqueName)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public void putTeamsEntry(TeamsEntry entry, List<String> players) {
		teams.put(entry, players);
	}

	public void removeTeamsEntryIf(Predicate<Map.Entry<TeamsEntry, List<String>>> predicate) {
		teams.entrySet().removeIf(predicate);
	}
}
