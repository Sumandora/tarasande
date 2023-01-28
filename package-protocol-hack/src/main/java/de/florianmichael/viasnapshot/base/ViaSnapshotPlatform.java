package de.florianmichael.viasnapshot.base;

import com.google.common.collect.Range;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocols.base.BaseProtocol1_16;
import de.florianmichael.viasnapshot.ViaSnapshot;
import de.florianmichael.viasnapshot.api.SnapshotProtocols;
import de.florianmichael.viasnapshot.protocol.protocol1_14to3D_Shareware.Protocol1_14to3D_Shareware;
import de.florianmichael.viasnapshot.protocol.protocol1_16_2toCombatTest8c.Protocol1_16_2toCombatTest8c;
import de.florianmichael.viasnapshot.protocol.protocol1_16to20w14infinite.Protocol1_16to20w14infinite;

import java.util.logging.Level;
import java.util.logging.Logger;

public interface ViaSnapshotPlatform {

    default void init() {
        ViaSnapshot.init(this);
        Via.getManager().getSubPlatforms().add("ViaSnapshot-FlorianMichael");
        getLogger().log(Level.SEVERE, "no good? no, this man is definitely up to evil.");

        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_14to3D_Shareware(), ProtocolVersion.v1_14, SnapshotProtocols.s3d_shareware);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_16to20w14infinite(), ProtocolVersion.v1_16, SnapshotProtocols.s20w14infinite);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_16_2toCombatTest8c(), ProtocolVersion.v1_16_2, SnapshotProtocols.sCombatTest8C);

        Via.getManager().getProtocolManager().registerBaseProtocol(new BaseProtocol1_16(), Range.singleton(SnapshotProtocols.s20w14infinite.getVersion()));
        Via.getManager().getProtocolManager().registerBaseProtocol(new BaseProtocol1_16(), Range.singleton(SnapshotProtocols.sCombatTest8C.getVersion()));
    }

    Logger getLogger();
}
