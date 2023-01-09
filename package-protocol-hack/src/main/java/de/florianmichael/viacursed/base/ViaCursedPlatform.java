package de.florianmichael.viacursed.base;

import com.google.common.collect.Range;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocols.base.BaseProtocol1_16;
import de.florianmichael.viacursed.ViaCursed;
import de.florianmichael.viacursed.api.CursedProtocols;
import de.florianmichael.viacursed.protocol.snapshot.protocol1_14to3D_Shareware.Protocol1_14to3D_Shareware;
import de.florianmichael.viacursed.protocol.snapshot.protocol1_16_2toCombatTest8c.Protocol1_16_2toCombatTest8c;
import de.florianmichael.viacursed.protocol.snapshot.protocol1_16to20w14infinite.Protocol1_16to20w14infinite;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.Protocol1_19_3toBedrock1_19_51;

import java.util.logging.Level;
import java.util.logging.Logger;

public interface ViaCursedPlatform {

    default void init() {
        ViaCursed.init(this);
        Via.getManager().getSubPlatforms().add("ViaCursed-FlorianMichael");
        getLogger().log(Level.SEVERE, "no good? no, this man is definitely up to evil.");

        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_14to3D_Shareware(), ProtocolVersion.v1_14, CursedProtocols.s3d_shareware);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_16to20w14infinite(), ProtocolVersion.v1_16, CursedProtocols.s20w14infinite);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_16_2toCombatTest8c(), ProtocolVersion.v1_16_2, CursedProtocols.sCombatTest8C);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_19_3toBedrock1_19_51(), ProtocolVersion.v1_19_3, CursedProtocols.ogBedrock1_19_51);

        Via.getManager().getProtocolManager().registerBaseProtocol(new BaseProtocol1_16(), Range.singleton(CursedProtocols.s20w14infinite.getVersion()));
        Via.getManager().getProtocolManager().registerBaseProtocol(new BaseProtocol1_16(), Range.singleton(CursedProtocols.sCombatTest8C.getVersion()));
    }

    Logger getLogger();
}
