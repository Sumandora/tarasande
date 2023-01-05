package de.florianmichael.viabeta.base;

import com.google.common.collect.Range;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viabeta.protocol.alpha.protocola1_0_16_2toa1_0_15.Protocola1_0_16_2toa1_0_15;
import de.florianmichael.viabeta.protocol.alpha.protocola1_0_17_1_0_17_4toa1_0_16_2.Protocola1_0_17_1_0_17_4toa1_0_16_2;
import de.florianmichael.viabeta.protocol.alpha.protocola1_1_0_1_1_2_1toa1_0_17_1_0_17_4.Protocola1_1_0_1_1_2_1toa1_0_17_1_0_17_4;
import de.florianmichael.viabeta.protocol.alpha.protocola1_2_0_1_2_1_1toa1_1_0_1_1_2_1.Protocola1_2_0_1_2_1_1toa1_1_0_1_1_2_1;
import de.florianmichael.viabeta.protocol.alpha.protocola1_2_2toa1_2_0_1_2_1_1.Protocola1_2_2toa1_2_0_1_2_1_1;
import de.florianmichael.viabeta.protocol.alpha.protocola1_2_3_1_2_3_4toa1_2_2.Protocola1_2_3_1_2_3_4toa1_2_2;
import de.florianmichael.viabeta.protocol.alpha.protocola1_2_3_5_1_2_6toa1_2_3_1_2_3_4.Protocola1_2_3_5_1_2_6toa1_2_3_1_2_3_4;
import de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.Protocolb1_0_1_1_1toa1_2_3_5_1_2_6;
import de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1.Protocol1_0_0_1tob1_8_0_1;
import de.florianmichael.viabeta.protocol.beta.protocolb1_1_2tob1_0_1_1.Protocolb1_1_2tob1_0_1_1;
import de.florianmichael.viabeta.protocol.beta.protocolb1_2_0_2tob1_1_2.Protocolb1_2_0_2tob1_1_2;
import de.florianmichael.viabeta.protocol.beta.protocolb1_3_0_1tob1_2_0_2.Protocolb1_3_0_1tob1_2_0_2;
import de.florianmichael.viabeta.protocol.beta.protocolb1_4_0_1tob1_3_0_1.Protocolb1_4_0_1tob1_3_0_1;
import de.florianmichael.viabeta.protocol.beta.protocolb1_5_0_2tob1_4_0_1.Protocolb1_5_0_2tob1_4_0_1;
import de.florianmichael.viabeta.protocol.beta.protocolb1_6_0_6tob1_5_0_2.Protocolb1_6_0_6tob1_5_0_2;
import de.florianmichael.viabeta.protocol.beta.protocolb1_7_0_3tob1_6_0_6.Protocolb1_7_0_3tob1_6_0_6;
import de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.Protocolb1_8_0_1tob1_7_0_3;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.Protocola1_0_15toc0_30;
import de.florianmichael.viabeta.protocol.classic.protocolc0_0_16a_02to0_0_15a_1.Protocolc0_0_16a_02to0_0_15a_1;
import de.florianmichael.viabeta.protocol.classic.protocolc0_0_18a_02toc0_0_16a_02.Protocolc0_0_18a_02toc0_0_16a_02;
import de.florianmichael.viabeta.protocol.classic.protocolc0_0_19a_06toc0_0_18a_02.Protocolc0_0_19a_06toc0_0_18a_02;
import de.florianmichael.viabeta.protocol.classic.protocolc0_0_20a_27toc0_0_19a_06.Protocolc0_27toc0_0_19a_06;
import de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_0_20a_27.Protocolc0_30toc0_27;
import de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_28_30cpe.Protocolc0_30toc0_30cpe;
import de.florianmichael.viabeta.protocol.protocol1_1to1_0_0_1.Protocol1_1to1_0_0_1;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.Protocol1_2_1_3to1_1;
import de.florianmichael.viabeta.protocol.protocol1_2_4_5to1_2_1_3.Protocol1_2_4_5to1_2_1_3;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.Protocol1_3_1_2to1_2_4_5;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.Protocol1_4_2to1_3_1_2;
import de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2.Protocol1_4_4_5to1_4_2;
import de.florianmichael.viabeta.protocol.protocol1_4_6_7to1_4_4_5.Protocol1_4_6_7to1_4_4_5;
import de.florianmichael.viabeta.protocol.protocol1_5_0_1to1_4_6_7.Protocol1_5_0_1to1_4_6_7;
import de.florianmichael.viabeta.protocol.protocol1_5_2to1_5_0_1.Protocol1_5_2to1_5_0_1;
import de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.Protocol1_6_1to1_5_2;
import de.florianmichael.viabeta.protocol.protocol1_6_2to1_6_1.Protocol1_6_2to1_6_1;
import de.florianmichael.viabeta.protocol.protocol1_6_4to1_6_2.Protocol1_6_4to1_6_2;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.Protocol1_7_2_5to1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.Protocol1_8to1_7_6_10;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.ViaBetaConfigImpl;
import de.florianmichael.viabeta.api.EmptyBaseProtocol;
import de.florianmichael.viabeta.protocol.protocol1_7_6_10to1_7_2_5.Protocol1_7_6_10to1_7_2_5;
import de.florianmichael.viabeta.api.LegacyVersionEnum;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface ViaBetaPlatform {

    default void init() {
        final ViaBetaConfigImpl config = new ViaBetaConfigImpl(new File(getDataFolder(), "viabeta.yml"));
        config.reloadConfig();

        ViaBeta.init(this, config);
        Via.getManager().getSubPlatforms().add("ViaBeta-FlorianMichael");
        getLogger().log(Level.SEVERE, "ViaVersion you fool, loading me was a big mistake!");

        // Pre-Netty
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_8to1_7_6_10(), ProtocolVersion.v1_8, ProtocolVersion.v1_7_6);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_7_6_10to1_7_2_5(), ProtocolVersion.v1_7_6, ProtocolVersion.v1_7_1);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_7_2_5to1_6_4(), ProtocolVersion.v1_7_1, LegacyVersionEnum.r1_6_4.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_6_4to1_6_2(), LegacyVersionEnum.r1_6_4.getProtocol(), LegacyVersionEnum.r1_6_2.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_6_2to1_6_1(), LegacyVersionEnum.r1_6_2.getProtocol(), LegacyVersionEnum.r1_6_1.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_6_1to1_5_2(), LegacyVersionEnum.r1_6_1.getProtocol(), LegacyVersionEnum.r1_5_2.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_5_2to1_5_0_1(), LegacyVersionEnum.r1_5_2.getProtocol(), LegacyVersionEnum.r1_5tor1_5_1.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_5_0_1to1_4_6_7(), LegacyVersionEnum.r1_5tor1_5_1.getProtocol(), LegacyVersionEnum.r1_4_6tor1_4_7.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_4_6_7to1_4_4_5(), LegacyVersionEnum.r1_4_6tor1_4_7.getProtocol(), LegacyVersionEnum.r1_4_4tor1_4_5.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_4_4_5to1_4_2(), LegacyVersionEnum.r1_4_4tor1_4_5.getProtocol(), LegacyVersionEnum.r1_4_2.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_4_2to1_3_1_2(), LegacyVersionEnum.r1_4_2.getProtocol(), LegacyVersionEnum.r1_3_1tor1_3_2.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_3_1_2to1_2_4_5(), LegacyVersionEnum.r1_3_1tor1_3_2.getProtocol(), LegacyVersionEnum.r1_2_4tor1_2_5.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_2_4_5to1_2_1_3(), LegacyVersionEnum.r1_2_4tor1_2_5.getProtocol(), LegacyVersionEnum.r1_2_1tor1_2_3.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_2_1_3to1_1(), LegacyVersionEnum.r1_2_1tor1_2_3.getProtocol(), LegacyVersionEnum.r1_1.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_1to1_0_0_1(), LegacyVersionEnum.r1_1.getProtocol(), LegacyVersionEnum.r1_0_0tor1_0_1.getProtocol());

        // Beta
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_0_0_1tob1_8_0_1(), LegacyVersionEnum.r1_0_0tor1_0_1.getProtocol(), LegacyVersionEnum.b1_8tob1_8_1.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocolb1_8_0_1tob1_7_0_3(), LegacyVersionEnum.b1_8tob1_8_1.getProtocol(), LegacyVersionEnum.b1_7tob1_7_3.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocolb1_7_0_3tob1_6_0_6(), LegacyVersionEnum.b1_7tob1_7_3.getProtocol(), LegacyVersionEnum.b1_6tob1_6_6.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocolb1_6_0_6tob1_5_0_2(), LegacyVersionEnum.b1_6tob1_6_6.getProtocol(), LegacyVersionEnum.b1_5tob1_5_2.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocolb1_5_0_2tob1_4_0_1(), LegacyVersionEnum.b1_5tob1_5_2.getProtocol(), LegacyVersionEnum.b1_4tob1_4_1.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocolb1_4_0_1tob1_3_0_1(), LegacyVersionEnum.b1_4tob1_4_1.getProtocol(), LegacyVersionEnum.b1_3tob1_3_1.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocolb1_3_0_1tob1_2_0_2(), LegacyVersionEnum.b1_3tob1_3_1.getProtocol(), LegacyVersionEnum.b1_2_0tob1_2_2.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocolb1_2_0_2tob1_1_2(), LegacyVersionEnum.b1_2_0tob1_2_2.getProtocol(), LegacyVersionEnum.b1_1_2.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocolb1_1_2tob1_0_1_1(), LegacyVersionEnum.b1_1_2.getProtocol(), LegacyVersionEnum.b1_0tob1_1_1.getProtocol());

        // Alpha
        Via.getManager().getProtocolManager().registerProtocol(new Protocolb1_0_1_1_1toa1_2_3_5_1_2_6(), LegacyVersionEnum.b1_0tob1_1_1.getProtocol(), LegacyVersionEnum.a1_2_3_5toa1_2_6.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocola1_2_3_5_1_2_6toa1_2_3_1_2_3_4(), LegacyVersionEnum.a1_2_3_5toa1_2_6.getProtocol(), LegacyVersionEnum.a1_2_3toa1_2_3_4.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocola1_2_3_1_2_3_4toa1_2_2(), LegacyVersionEnum.a1_2_3toa1_2_3_4.getProtocol(), LegacyVersionEnum.a1_2_2.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocola1_2_2toa1_2_0_1_2_1_1(), LegacyVersionEnum.a1_2_2.getProtocol(), LegacyVersionEnum.a1_2_0toa1_2_1_1.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocola1_2_0_1_2_1_1toa1_1_0_1_1_2_1(), LegacyVersionEnum.a1_2_0toa1_2_1_1.getProtocol(), LegacyVersionEnum.a1_1_0toa1_1_2_1.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocola1_1_0_1_1_2_1toa1_0_17_1_0_17_4(), LegacyVersionEnum.a1_1_0toa1_1_2_1.getProtocol(), LegacyVersionEnum.a1_0_17toa1_0_17_4.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocola1_0_17_1_0_17_4toa1_0_16_2(), LegacyVersionEnum.a1_0_17toa1_0_17_4.getProtocol(), LegacyVersionEnum.a1_0_16toa1_0_16_2.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocola1_0_16_2toa1_0_15(), LegacyVersionEnum.a1_0_16toa1_0_16_2.getProtocol(), LegacyVersionEnum.a1_0_15.getProtocol());

        // Classic
        Via.getManager().getProtocolManager().registerProtocol(new Protocola1_0_15toc0_30(), LegacyVersionEnum.a1_0_15.getProtocol(), LegacyVersionEnum.c0_28toc0_30.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocolc0_30toc0_30cpe(), LegacyVersionEnum.c0_28toc0_30.getProtocol(), LegacyVersionEnum.c0_30cpe.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocolc0_30toc0_27(), LegacyVersionEnum.c0_28toc0_30.getProtocol(), LegacyVersionEnum.c0_0_20ac0_27.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocolc0_27toc0_0_19a_06(), LegacyVersionEnum.c0_0_20ac0_27.getProtocol(), LegacyVersionEnum.c0_0_19a_06.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocolc0_0_19a_06toc0_0_18a_02(), LegacyVersionEnum.c0_0_19a_06.getProtocol(), LegacyVersionEnum.c0_0_18a_02.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocolc0_0_18a_02toc0_0_16a_02(), LegacyVersionEnum.c0_0_18a_02.getProtocol(), LegacyVersionEnum.c0_0_16a_02.getProtocol());
        Via.getManager().getProtocolManager().registerProtocol(new Protocolc0_0_16a_02to0_0_15a_1(), LegacyVersionEnum.c0_0_16a_02.getProtocol(), LegacyVersionEnum.c0_0_15a_1.getProtocol());

        // Base Protocols for bypassing r1.6.4 login
        for (LegacyVersionEnum version : LegacyVersionEnum.values()) {
            Via.getManager().getProtocolManager().registerBaseProtocol(new EmptyBaseProtocol(), Range.singleton(version.getVersion()));
        }
    }

    Logger getLogger();
    File getDataFolder();
}
