package su.mandora.tarasande.injection.mixin.core.connection.playerlistentry;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import su.mandora.tarasande.injection.accessor.playerlistentry.IOtherClientPlayerEntity;
import su.mandora.tarasande.injection.accessor.playerlistentry.IPlayerListEntry;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.stream.Collectors;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @Shadow @Final public Map<UUID, PlayerListEntry> playerListEntries;

    @Shadow @Nullable public abstract PlayerListEntry getPlayerListEntry(UUID uuid);

    @Inject(method = "onEntitySpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onSpawnPacket(Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;)V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void capturePlayerListEntry(EntitySpawnS2CPacket packet, CallbackInfo ci, Entity entity) {
        if(packet.getEntityType() == EntityType.PLAYER && entity instanceof OtherClientPlayerEntity otherClientPlayerEntity) {
            PlayerListEntry playerListEntry = getPlayerListEntry(packet.getUuid());
            if(playerListEntry != null) {
                ((IPlayerListEntry) playerListEntry).tarasande_addOwners(otherClientPlayerEntity);
                ((IOtherClientPlayerEntity) otherClientPlayerEntity).tarasande_setPlayerListEntry(new WeakReference<>(playerListEntry));
            }
        }
    }

    @Inject(method = "onPlayerList", at = @At(value = "INVOKE", target = "Ljava/util/Map;putIfAbsent(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void capturePlayerListEntry(PlayerListS2CPacket packet, CallbackInfo ci, Iterator<?> var2, PlayerListS2CPacket.Entry entry, PlayerListEntry playerListEntry) {
        Set<PlayerListEntry> duplicates = playerListEntries.values().stream().filter(o -> Objects.equals(o.getProfile().getName(), playerListEntry.getProfile().getName())).collect(Collectors.toSet());
        if(!duplicates.isEmpty()) {
            for(PlayerListEntry duplicate : duplicates) {
                ((IPlayerListEntry) duplicate).tarasande_addDuplicate(playerListEntry);
                ((IPlayerListEntry) playerListEntry).tarasande_addDuplicate(duplicate);
            }
        }
    }

    @Inject(method = "handlePlayerListAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket$Entry;listed()Z"))
    public void setListed(PlayerListS2CPacket.Action action, PlayerListS2CPacket.Entry receivedEntry, PlayerListEntry currentEntry, CallbackInfo ci) {
        ((IPlayerListEntry) currentEntry).tarasande_setListed(receivedEntry.listed());
    }

    @Inject(method = "onPlayerRemove", at = @At(value = "INVOKE", target = "Ljava/util/Set;remove(Ljava/lang/Object;)Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void setRemoved(PlayerRemoveS2CPacket packet, CallbackInfo ci, Iterator<?> var2, UUID uUID, PlayerListEntry playerListEntry) {
        // Even if we remove all references, it won't be garbage collected immediately. Store the fact that this one is invalid inside of it, then leave it to the garbage collector.
        ((IPlayerListEntry) playerListEntry).tarasande_setRemoved(true);
    }

}
