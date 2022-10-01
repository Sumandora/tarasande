/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 6/24/22, 1:27 PM
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

package de.florianmichael.viaprotocolhack.util;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.ViaProtocolHack;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * ######################################################################################################################
 * # Notes:                                                                                                             #
 * # As of 1.6.4 all protocol IDs are given in the negative range, this has something to do with the sorting algorithms #
 * # of ViaVersion, which pushes protocols with higher protocol IDs further up the protocol pipeline                    #
 * # (which would break the protocol hack).                                                                             #
 * ######################################################################################################################
 */
public class VersionList {

    private static final List<ProtocolVersion> protocols = new LinkedList<>();

    public static final ProtocolVersion R1_19_2 = new ProtocolVersion(760, "1.19.1-1.19.2");
    public static final ProtocolVersion R1_19 = new ProtocolVersion(759, "1.19");

    public static final ProtocolVersion R1_18_2 = new ProtocolVersion(758, "1.18.2");
    public static final ProtocolVersion R1_18_1 = new ProtocolVersion(757, "1.18-1.18.1");

    public static final ProtocolVersion R1_17_1 = new ProtocolVersion(756, "1.17.1");
    public static final ProtocolVersion R1_17 = new ProtocolVersion(755, "1.17");

    public static final ProtocolVersion R1_16_5 = new ProtocolVersion(754, "1.16.4-1.16.5");
    public static final ProtocolVersion R1_16_3 = new ProtocolVersion(753, "1.16.3");
    public static final ProtocolVersion R1_16_2 = new ProtocolVersion(751, "1.16.2");
    public static final ProtocolVersion R1_16_1 = new ProtocolVersion(736, "1.16.1");
    public static final ProtocolVersion R1_16 = new ProtocolVersion(735, "1.16");

    public static final ProtocolVersion R1_15_2 = new ProtocolVersion(578, "1.15.2");
    public static final ProtocolVersion R1_15_1 = new ProtocolVersion(575, "1.15.1");
    public static final ProtocolVersion R1_15 = new ProtocolVersion(573, "1.15");

    public static final ProtocolVersion R1_14_4 = new ProtocolVersion(498, "1.14.4");
    public static final ProtocolVersion R1_14_3 = new ProtocolVersion(490, "1.14.3");
    public static final ProtocolVersion R1_14_2 = new ProtocolVersion(485, "1.14.2");
    public static final ProtocolVersion R1_14_1 = new ProtocolVersion(480, "1.14.1");
    public static final ProtocolVersion R1_14 = new ProtocolVersion(477, "1.14");

    public static final ProtocolVersion R1_13_2 = new ProtocolVersion(404, "1.13.2");
    public static final ProtocolVersion R1_13_1 = new ProtocolVersion(401, "1.13.1");
    public static final ProtocolVersion R1_13 = new ProtocolVersion(393, "1.13");

    public static final ProtocolVersion R1_12_2 = new ProtocolVersion(340, "1.12.2");
    public static final ProtocolVersion R1_12_1 = new ProtocolVersion(338, "1.12.1");
    public static final ProtocolVersion R1_12 = new ProtocolVersion(335, "1.12");

    public static final ProtocolVersion R1_11_1 = new ProtocolVersion(316, "1.11.1-1.11.2");
    public static final ProtocolVersion R1_11 = new ProtocolVersion(315, "1.11");

    public static final ProtocolVersion R1_10 = new ProtocolVersion(210, "1.10.x");

    public static final ProtocolVersion R1_9_4 = new ProtocolVersion(110, "1.9.3-1.9.4");
    public static final ProtocolVersion R1_9_2 = new ProtocolVersion(109, "1.9.2");
    public static final ProtocolVersion R1_9_1 = new ProtocolVersion(108, "1.9.1");
    public static final ProtocolVersion R1_9 = new ProtocolVersion(107, "1.9");

    public static final ProtocolVersion R1_8 = new ProtocolVersion(47, "1.8.x");

    public static void registerProtocols() throws IllegalAccessException {
        for (Field declaredField : VersionList.class.getDeclaredFields()) {
            if (declaredField.get(null) instanceof ProtocolVersion)
                protocols.add((ProtocolVersion) declaredField.get(null));
        }
    }

    private static boolean isSingleplayer() {
        return ViaProtocolHack.instance().provider().isSinglePlayer();
    }

    public static boolean isEqualTo(final ProtocolVersion protocolVersion) {
        if (isSingleplayer())
            return false;

        return ViaProtocolHack.instance().current() == protocolVersion.getVersion();
    }

    public static boolean isOlderOrEqualTo(final ProtocolVersion protocolVersion) {
        if (isSingleplayer())
            return false;

        return ViaProtocolHack.instance().current() <= protocolVersion.getVersion();
    }

    public static boolean isOlderTo(final ProtocolVersion protocolVersion) {
        if (isSingleplayer() || protocolVersion == null)
            return false;

        return ViaProtocolHack.instance().current() < protocolVersion.getVersion();
    }

    public static boolean isNewerTo(final ProtocolVersion protocolVersion) {
        if (isSingleplayer() || protocolVersion == null)
            return false;

        return ViaProtocolHack.instance().current() > protocolVersion.getVersion();
    }

    public static boolean isNewerOrEqualTo(final ProtocolVersion protocolVersion) {
        if (isSingleplayer() || protocolVersion == null)
            return false;

        return ViaProtocolHack.instance().current() >= protocolVersion.getVersion();
    }

    public static boolean isSupported(int server, int client) {
        return server == client || Via.getManager().getProtocolManager().getProtocolPath(client, server) != null;
    }

    public static List<ProtocolVersion> getProtocols() {
        return protocols;
    }
}
