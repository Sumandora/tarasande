/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 08.04.22, 17:07
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

import java.util.HashMap;
import java.util.Map;

public class WindowIDTracker extends StoredObject {

    private final Map<Short, Short> types = new HashMap<>();

    public WindowIDTracker(UserConnection user) {
        super(user);
    }

    public void put(final short windowId, final short index) {
        this.types.put(windowId, index);
    }

    public short get(final short windowId) {
        return types.getOrDefault(windowId, (short)-1);
    }

    public String getInventoryString(int b) {
        return switch (b) {
            case 0 -> "minecraft:chest";
            case 1 -> "minecraft:crafting_table";
            case 2 -> "minecraft:furnace";
            case 3 -> "minecraft:dispenser";
            case 4 -> "minecraft:enchanting_table";
            case 5 -> "minecraft:brewing_stand";
            case 6 -> "minecraft:villager";
            case 7 -> "minecraft:beacon";
            case 8 -> "minecraft:anvil";
            case 9 -> "minecraft:hopper";
            case 10 -> "minecraft:dropper";
            case 11 -> "EntityHorse";
            default -> throw new IllegalArgumentException("Unknown type " + b);
        };
    }
}
