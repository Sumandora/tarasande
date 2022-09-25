/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 30.03.22, 19:43
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

package de.enzaxd.viaforge.equals;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.enzaxd.viaforge.ViaForge;

public class ProtocolEquals {

    public static boolean isEqualTo(final ProtocolVersion protocolVersion) {
        return ViaForge.CURRENT_VERSION == protocolVersion.getVersion();
    }

    public static boolean isOlderOrEqualTo(final ProtocolVersion protocolVersion) {
        return ViaForge.CURRENT_VERSION <= protocolVersion.getVersion();
    }

    public static boolean isOlderTo(final ProtocolVersion protocolVersion) {
        return ViaForge.CURRENT_VERSION < protocolVersion.getVersion();
    }

    public static boolean isNewerTo(final ProtocolVersion protocolVersion) {
        return ViaForge.CURRENT_VERSION > protocolVersion.getVersion();
    }

    public static boolean isNewerOrEqualTo(final ProtocolVersion protocolVersion) {
        return ViaForge.CURRENT_VERSION >= protocolVersion.getVersion();
    }
}
