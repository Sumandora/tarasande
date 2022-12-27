package de.florianmichael.clampclient.injection.mixin.protocolhack.screen.hud;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.stream.Stream;

@Mixin(PlayerListHud.class)
public class MixinPlayerListHud {

    @Unique
    private final Ordering<PlayerListEntry> protocolhack_1_19_1sorter = Ordering.from((first, second) -> {
        final Team firstTeam = first.getScoreboardTeam();
        final Team secondTeam = second.getScoreboardTeam();

        return ComparisonChain.start().
                compareTrueFirst(first.getGameMode() != GameMode.SPECTATOR, second.getGameMode() != GameMode.SPECTATOR).
                compare(firstTeam != null ? firstTeam.getName() : "", secondTeam != null ? secondTeam.getName() : "").
                compare(first.getProfile().getName(), second.getProfile().getName(), String::compareToIgnoreCase).
                result();
    });

    @Shadow @Final private MinecraftClient client;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;toList()Ljava/util/List;"))
    public List<PlayerListEntry> test(Stream<PlayerListEntry> instance) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_19_1)) {
            return protocolhack_1_19_1sorter.sortedCopy(client.getNetworkHandler().getPlayerList()).stream().toList().subList(0, 80);
        }
        return instance.toList();
    }
}
