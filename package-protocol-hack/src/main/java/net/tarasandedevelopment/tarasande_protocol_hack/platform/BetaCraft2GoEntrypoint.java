package net.tarasandedevelopment.tarasande_protocol_hack.platform;

import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.util.VersionListEnum;
import net.tarasandedevelopment.tarasande_protocol_hack.TarasandeProtocolHack;
import net.tarasandedevelopment.tarasande_protocol_hack.util.values.ProtocolHackValues;

import java.util.Arrays;

public class BetaCraft2GoEntrypoint implements Runnable {

    @Override
    public void run() {
        if (!ProtocolHackValues.INSTANCE.getAutoDetectInBetaCraft2Go().getValue()) return;

        ProtocolHackValues.INSTANCE.getBetaCraftAuth().setValue(Boolean.parseBoolean(System.getProperty("FMBC-OnlineMode")));

        final String serverVersion = System.getProperty("FMBC-ServerVersion").replace("c0.30-c", VersionListEnum.c0_28toc0_30.getName());
        Arrays.stream(VersionListEnum.values()).filter(v -> v.getName().contains(serverVersion)).findFirst().ifPresent(versionListEnum -> {
            TarasandeProtocolHack.Companion.update(versionListEnum, true);
            ViaLoadingBase.instance().logger().info("BetaCraft2Go forced TarasandeProtocolHack to switch to " + versionListEnum.getName());
        });
    }
}
