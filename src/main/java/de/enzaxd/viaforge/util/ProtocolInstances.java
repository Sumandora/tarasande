/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 22.06.22, 19:31
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

package de.enzaxd.viaforge.util;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ServerboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.ClientboundPackets1_12;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.ServerboundPackets1_12;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ServerboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_17_1to1_17.ClientboundPackets1_17_1;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ClientboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ServerboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.ClientboundPackets1_18;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.ClientboundPackets1_19;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.ServerboundPackets1_19;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_8.ServerboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import de.enzaxd.viaforge.equals.VersionList;

import java.util.Objects;

public class ProtocolInstances {

    public static Protocol<ClientboundPackets1_8, ClientboundPackets1_9, ServerboundPackets1_8, ServerboundPackets1_9> getProtocol1_9To1_8() {
        return (Protocol<ClientboundPackets1_8, ClientboundPackets1_9, ServerboundPackets1_8, ServerboundPackets1_9>) getProtocol(VersionList.R1_9, VersionList.R1_8);
    }

    public static Protocol<ClientboundPackets1_9, ClientboundPackets1_9, ServerboundPackets1_9, ServerboundPackets1_9> getProtocol1_9_1To1_9() {
        return (Protocol<ClientboundPackets1_9, ClientboundPackets1_9, ServerboundPackets1_9, ServerboundPackets1_9>) getProtocol(VersionList.R1_9_1, VersionList.R1_9);
    }

    public static Protocol<ClientboundPackets1_9, ClientboundPackets1_9_3, ServerboundPackets1_9, ServerboundPackets1_9_3> getProtocol1_9_3To1_9_1_2() {
        return (Protocol<ClientboundPackets1_9, ClientboundPackets1_9_3, ServerboundPackets1_9, ServerboundPackets1_9_3>) getProtocol(VersionList.R1_9_4, VersionList.R1_9_2);
    }

    public static Protocol<ClientboundPackets1_9_3, ClientboundPackets1_9_3, ServerboundPackets1_9_3, ServerboundPackets1_9_3> getProtocol1_10To1_9_3_4() {
        return (Protocol<ClientboundPackets1_9_3, ClientboundPackets1_9_3, ServerboundPackets1_9_3, ServerboundPackets1_9_3>) getProtocol(VersionList.R1_10, VersionList.R1_9_4);
    }

    public static Protocol<ClientboundPackets1_9_3, ClientboundPackets1_9_3, ServerboundPackets1_9_3, ServerboundPackets1_9_3> getProtocol1_11To1_10() {
        return (Protocol<ClientboundPackets1_9_3, ClientboundPackets1_9_3, ServerboundPackets1_9_3, ServerboundPackets1_9_3>) getProtocol(VersionList.R1_11, VersionList.R1_10);
    }

    public static Protocol<ClientboundPackets1_9_3, ClientboundPackets1_9_3, ServerboundPackets1_9_3, ServerboundPackets1_9_3> getProtocol1_11_1To1_11() {
        return (Protocol<ClientboundPackets1_9_3, ClientboundPackets1_9_3, ServerboundPackets1_9_3, ServerboundPackets1_9_3>) getProtocol(VersionList.R1_11_1, VersionList.R1_11);
    }

    public static Protocol<ClientboundPackets1_9_3, ClientboundPackets1_12, ServerboundPackets1_9_3, ServerboundPackets1_12> getProtocol1_12To1_11_1() {
        return (Protocol<ClientboundPackets1_9_3, ClientboundPackets1_12, ServerboundPackets1_9_3, ServerboundPackets1_12>) getProtocol(VersionList.R1_12, VersionList.R1_11_1);
    }

    public static Protocol<ClientboundPackets1_12, ClientboundPackets1_12_1, ServerboundPackets1_12, ServerboundPackets1_12_1> getProtocol1_12_1To1_12() {
        return (Protocol<ClientboundPackets1_12, ClientboundPackets1_12_1, ServerboundPackets1_12, ServerboundPackets1_12_1>) getProtocol(VersionList.R1_12_1, VersionList.R1_12);
    }

    public static Protocol<ClientboundPackets1_12_1, ClientboundPackets1_12_1, ServerboundPackets1_12_1, ServerboundPackets1_12_1> getProtocol1_12_2To1_12_1() {
        return (Protocol<ClientboundPackets1_12_1, ClientboundPackets1_12_1, ServerboundPackets1_12_1, ServerboundPackets1_12_1>) getProtocol(VersionList.R1_12_2, VersionList.R1_12_1);
    }

    public static Protocol<ClientboundPackets1_12_1, ClientboundPackets1_13, ServerboundPackets1_12_1, ServerboundPackets1_13> getProtocol1_13To1_12_2() {
        return (Protocol<ClientboundPackets1_12_1, ClientboundPackets1_13, ServerboundPackets1_12_1, ServerboundPackets1_13>) getProtocol(VersionList.R1_13, VersionList.R1_12_2);
    }

    public static Protocol<ClientboundPackets1_13, ClientboundPackets1_13, ServerboundPackets1_13, ServerboundPackets1_13> getProtocol1_13_1To1_13() {
        return (Protocol<ClientboundPackets1_13, ClientboundPackets1_13, ServerboundPackets1_13, ServerboundPackets1_13>) getProtocol(VersionList.R1_13_1, VersionList.R1_13);
    }

    public static Protocol<ClientboundPackets1_13, ClientboundPackets1_13, ServerboundPackets1_13, ServerboundPackets1_13> getProtocol1_13_2To1_13_1() {
        return (Protocol<ClientboundPackets1_13, ClientboundPackets1_13, ServerboundPackets1_13, ServerboundPackets1_13>) getProtocol(VersionList.R1_13_2, VersionList.R1_13_1);
    }

    public static Protocol<ClientboundPackets1_13, ClientboundPackets1_14, ServerboundPackets1_13, ServerboundPackets1_14> getProtocol1_14To1_13_2() {
        return (Protocol<ClientboundPackets1_13, ClientboundPackets1_14, ServerboundPackets1_13, ServerboundPackets1_14>) getProtocol(VersionList.R1_14, VersionList.R1_13_2);
    }

    public static Protocol<ClientboundPackets1_14, ClientboundPackets1_14, ServerboundPackets1_14, ServerboundPackets1_14> getProtocol1_14_1To1_14() {
        return (Protocol<ClientboundPackets1_14, ClientboundPackets1_14, ServerboundPackets1_14, ServerboundPackets1_14>) getProtocol(VersionList.R1_14_1, VersionList.R1_14);
    }

    public static Protocol<ClientboundPackets1_14, ClientboundPackets1_14, ServerboundPackets1_14, ServerboundPackets1_14> getProtocol1_14_2To1_14_1() {
        return (Protocol<ClientboundPackets1_14, ClientboundPackets1_14, ServerboundPackets1_14, ServerboundPackets1_14>) getProtocol(VersionList.R1_14_2, VersionList.R1_14_1);
    }

    public static Protocol<ClientboundPackets1_14, ClientboundPackets1_14, ServerboundPackets1_14, ServerboundPackets1_14> getProtocol1_14_3To1_14_2() {
        return (Protocol<ClientboundPackets1_14, ClientboundPackets1_14, ServerboundPackets1_14, ServerboundPackets1_14>) getProtocol(VersionList.R1_14_3, VersionList.R1_14_2);
    }

    public static Protocol<ClientboundPackets1_14, ClientboundPackets1_14, ServerboundPackets1_14, ServerboundPackets1_14> getProtocol1_14_4To1_14_3() {
        return (Protocol<ClientboundPackets1_14, ClientboundPackets1_14, ServerboundPackets1_14, ServerboundPackets1_14>) getProtocol(VersionList.R1_14_4, VersionList.R1_14_3);
    }

    public static Protocol<ClientboundPackets1_14, ClientboundPackets1_15, ServerboundPackets1_14, ServerboundPackets1_14> getProtocol1_15To1_14_4() {
        return (Protocol<ClientboundPackets1_14, ClientboundPackets1_15, ServerboundPackets1_14, ServerboundPackets1_14>) getProtocol(VersionList.R1_15, VersionList.R1_14_4);
    }

    public static Protocol<ClientboundPackets1_15, ClientboundPackets1_15, ServerboundPackets1_14, ServerboundPackets1_14> getProtocol1_15_1To1_15() {
        return (Protocol<ClientboundPackets1_15, ClientboundPackets1_15, ServerboundPackets1_14, ServerboundPackets1_14>) getProtocol(VersionList.R1_15_1, VersionList.R1_15);
    }

    public static Protocol<ClientboundPackets1_15, ClientboundPackets1_15, ServerboundPackets1_14, ServerboundPackets1_14> getProtocol1_15_2To1_15_1() {
        return (Protocol<ClientboundPackets1_15, ClientboundPackets1_15, ServerboundPackets1_14, ServerboundPackets1_14>) getProtocol(VersionList.R1_15_2, VersionList.R1_15_1);
    }

    public static Protocol<ClientboundPackets1_15, ClientboundPackets1_16, ServerboundPackets1_14, ServerboundPackets1_16> getProtocol1_16To1_15_2() {
        return (Protocol<ClientboundPackets1_15, ClientboundPackets1_16, ServerboundPackets1_14, ServerboundPackets1_16>) getProtocol(VersionList.R1_16, VersionList.R1_15_2);
    }

    public static Protocol<ClientboundPackets1_16, ClientboundPackets1_16, ServerboundPackets1_16, ServerboundPackets1_16> getProtocol1_16_1To1_16() {
        return (Protocol<ClientboundPackets1_16, ClientboundPackets1_16, ServerboundPackets1_16, ServerboundPackets1_16>) getProtocol(VersionList.R1_16_1, VersionList.R1_16);
    }

    public static Protocol<ClientboundPackets1_16, ClientboundPackets1_16_2, ServerboundPackets1_16, ServerboundPackets1_16_2> getProtocol1_16_2To1_16_1() {
        return (Protocol<ClientboundPackets1_16, ClientboundPackets1_16_2, ServerboundPackets1_16, ServerboundPackets1_16_2>) getProtocol(VersionList.R1_16_2, VersionList.R1_16_1);
    }

    public static Protocol<ClientboundPackets1_16_2, ClientboundPackets1_16_2, ServerboundPackets1_16_2, ServerboundPackets1_16_2> getProtocol1_16_3To1_16_2() {
        return (Protocol<ClientboundPackets1_16_2, ClientboundPackets1_16_2, ServerboundPackets1_16_2, ServerboundPackets1_16_2>) getProtocol(VersionList.R1_16_3, VersionList.R1_16_2);
    }

    public static Protocol<ClientboundPackets1_16_2, ClientboundPackets1_16_2, ServerboundPackets1_16_2, ServerboundPackets1_16_2> getProtocol1_16_4To1_16_3() {
        return (Protocol<ClientboundPackets1_16_2, ClientboundPackets1_16_2, ServerboundPackets1_16_2, ServerboundPackets1_16_2>) getProtocol(VersionList.R1_16_5, VersionList.R1_16_3);
    }

    public static Protocol<ClientboundPackets1_16_2, ClientboundPackets1_17, ServerboundPackets1_16_2, ServerboundPackets1_17> getProtocol1_17To1_16_4() {
        return (Protocol<ClientboundPackets1_16_2, ClientboundPackets1_17, ServerboundPackets1_16_2, ServerboundPackets1_17>) getProtocol(VersionList.R1_17, VersionList.R1_16_5);
    }

    public static Protocol<ClientboundPackets1_17, ClientboundPackets1_17_1, ServerboundPackets1_17, ServerboundPackets1_17> getProtocol1_17_1To1_17() {
        return (Protocol<ClientboundPackets1_17, ClientboundPackets1_17_1, ServerboundPackets1_17, ServerboundPackets1_17>) getProtocol(VersionList.R1_17_1, VersionList.R1_17);
    }

    public static Protocol<ClientboundPackets1_17_1, ClientboundPackets1_18, ServerboundPackets1_17, ServerboundPackets1_17> getProtocol1_18To1_17_1() {
        return (Protocol<ClientboundPackets1_17_1, ClientboundPackets1_18, ServerboundPackets1_17, ServerboundPackets1_17>) getProtocol(VersionList.R1_18_1, VersionList.R1_17_1);
    }

    public static Protocol<ClientboundPackets1_18, ClientboundPackets1_18, ServerboundPackets1_17, ServerboundPackets1_17> getProtocol1_18_2To1_18() {
        return (Protocol<ClientboundPackets1_18, ClientboundPackets1_18, ServerboundPackets1_17, ServerboundPackets1_17>) getProtocol(VersionList.R1_18_2, VersionList.R1_18_1);
    }

    public static Protocol<ClientboundPackets1_18, ClientboundPackets1_19, ServerboundPackets1_17, ServerboundPackets1_19> getProtocol1_19To1_18_2() {
        return (Protocol<ClientboundPackets1_18, ClientboundPackets1_19, ServerboundPackets1_17, ServerboundPackets1_19>) getProtocol(VersionList.R1_19, VersionList.R1_18_2);
    }

    public static Protocol<?, ?, ?, ?> getProtocol(final ProtocolVersion from, final ProtocolVersion to) {
        return Objects.requireNonNull(Via.getManager().getProtocolManager().getProtocolPath(from.getVersion(), to.getVersion())).get(0).protocol();
    }
}
