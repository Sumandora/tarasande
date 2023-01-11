package de.florianmichael.viabeta.api;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.storage.ClassicProgressStorage;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.storage.SeedStorage;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.storage.EntityTracker_1_2_4_5;
import de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.storage.EntityTracker_1_5_2;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.storage.EntityTracker_1_7_6_10;

public class BetaProtocolAccess {

    public static String getTrackedEntities1_7_6_10(final UserConnection connection) {
        final EntityTracker_1_7_6_10 entityTracker = connection.get(EntityTracker_1_7_6_10.class);
        if (entityTracker != null) {
            if (entityTracker.getTrackedEntities().isEmpty()) return null;

            return String.valueOf(entityTracker.getTrackedEntities().size());
        }
        return null;
    }

    public static String getVirtualHolograms1_7_6_10(final UserConnection connection) {
        final EntityTracker_1_7_6_10 entityTracker = connection.get(EntityTracker_1_7_6_10.class);
        if (entityTracker != null) {
            if (entityTracker.getVirtualHolograms().isEmpty()) return null;

            return String.valueOf(entityTracker.getVirtualHolograms().size());
        }
        return null;
    }

    public static String getTrackedEntities1_5_2(final UserConnection connection) {
        final EntityTracker_1_5_2 entityTracker = connection.get(EntityTracker_1_5_2.class);
        if (entityTracker != null) {
            if (entityTracker.getTrackedEntities().isEmpty()) return null;

            return String.valueOf(entityTracker.getTrackedEntities().size());
        }
        return null;
    }

    public static String getTrackedEntities1_2_4_5(final UserConnection connection) {
        final EntityTracker_1_2_4_5 entityTracker = connection.get(EntityTracker_1_2_4_5.class);
        if (entityTracker != null) {
            if (entityTracker.getTrackedEntities().isEmpty()) return null;

            return String.valueOf(entityTracker.getTrackedEntities().size());
        }
        return null;
    }

    public static String getWorldSeed1_1(final UserConnection connection) {
        final SeedStorage seedStorage = connection.get(SeedStorage.class);
        if (seedStorage != null) {
            if (seedStorage.seed == 0L) return null;

            return String.valueOf(seedStorage.seed);
        }
        return null;
    }

    public static String getWorldLoading_C_0_30(final UserConnection connection) {
        final ClassicProgressStorage classicProgressStorage = connection.get(ClassicProgressStorage.class);
        if (classicProgressStorage == null) return null;

        return ViaBeta.PREFIX_1_7 + classicProgressStorage.status + " (" + classicProgressStorage.progress + "%)";
    }
}
