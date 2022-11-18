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

package de.florianmichael.vialegacy.protocol;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import java.util.ArrayList;
import java.util.List;

public class LegacyProtocolVersion extends ProtocolVersion {

    public static final List<ProtocolVersion> PROTOCOL_VERSIONS = new ArrayList<>();
    public static final List<ProtocolVersion> PRE_NETTY_VERSIONS = new ArrayList<>();

    public static final ProtocolVersion r1_7_6_10 = new ProtocolVersion(5, "1.7.6-1.7.10") {
        { PROTOCOL_VERSIONS.add(this); }
    };
    public static final ProtocolVersion r1_7_0_5 = new ProtocolVersion(4, "1.7-1.7.5") {
        { PROTOCOL_VERSIONS.add(this); }
    };

    public static final LegacyProtocolVersion r1_6_4 = new LegacyProtocolVersion(78, "1.6.4");
    public static final LegacyProtocolVersion r1_6_3_pre = new LegacyProtocolVersion(77, "1.6.3-pre");
    public static final LegacyProtocolVersion r1_6_2 = new LegacyProtocolVersion(74, "1.6.2");
    public static final LegacyProtocolVersion r1_6_1 = new LegacyProtocolVersion(73, "1.6.1");

    public static final LegacyProtocolVersion r1_5_2 = new LegacyProtocolVersion(61, "1.5.2");
    public static final LegacyProtocolVersion r1_5_1 = new LegacyProtocolVersion(60, "1.5.1");

    public static final LegacyProtocolVersion r1_4_6_7 = new LegacyProtocolVersion(51, "1.4.6-1.4.7");
    public static final LegacyProtocolVersion r1_4_4_5 = new LegacyProtocolVersion(49, "1.4.4-1.4.5");
    public static final LegacyProtocolVersion r1_4_3_pre = new LegacyProtocolVersion(48, "1.4.3-pre");
    public static final LegacyProtocolVersion r1_4_0_2 = new LegacyProtocolVersion(47, "1.4-1.4.2");

    public static final LegacyProtocolVersion r1_3_1_2 = new LegacyProtocolVersion(39, "1.3.1-1.3.2");

    public static final LegacyProtocolVersion r1_2_4_5 = new LegacyProtocolVersion(29, "1.2.4-1.2.5");
    public static final LegacyProtocolVersion r1_2_1_3 = new LegacyProtocolVersion(28, "1.2.1-1.2.3");

    public static final LegacyProtocolVersion r1_1 = new LegacyProtocolVersion(23, "1.1");

    public static final LegacyProtocolVersion r1_0_0_1 = new LegacyProtocolVersion(22, "1.0.0-1.0.1");

    public LegacyProtocolVersion(int version, String name) {
        super(-version, name); // Negative ID's since ViaVersion uses there value to order the pipeline

        PROTOCOL_VERSIONS.add(this);
        PRE_NETTY_VERSIONS.add(this);
    }
}
