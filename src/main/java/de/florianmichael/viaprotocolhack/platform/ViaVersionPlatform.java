package de.florianmichael.viaprotocolhack.platform;

import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.configuration.ConfigurationProvider;
import com.viaversion.viaversion.api.configuration.ViaVersionConfig;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.PlatformTask;
import com.viaversion.viaversion.api.platform.UnsupportedSoftware;
import com.viaversion.viaversion.api.platform.ViaPlatform;
import com.viaversion.viaversion.libs.gson.JsonObject;
import de.florianmichael.viaprotocolhack.ViaProtocolHack;
import de.florianmichael.viaprotocolhack.platform.viaversion.CustomViaAPIWrapper;
import de.florianmichael.viaprotocolhack.platform.viaversion.CustomViaConfig;
import de.florianmichael.viaprotocolhack.util.FutureTaskId;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.File;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ViaVersionPlatform implements ViaPlatform<UUID> {

    private final Logger logger;
    private final ViaAPI<UUID> api = new CustomViaAPIWrapper();

    private CustomViaConfig config;

    public ViaVersionPlatform(final Logger logger) {
        this.logger = logger;
        config = new CustomViaConfig(ViaProtocolHack.instance().directory().toPath().resolve("viaversion.yml").toFile());
    }

    @Override
    public FutureTaskId runAsync(Runnable runnable) {
        return new FutureTaskId(CompletableFuture.runAsync(runnable, ViaProtocolHack.instance().executorService())
                .exceptionally(throwable -> {
                    if (!(throwable instanceof CancellationException))
                        throwable.printStackTrace();

                    return null;
                }));
    }

    @Override
    public PlatformTask<?> runSync(Runnable runnable) {
        return new FutureTaskId(ViaProtocolHack.instance().eventLoop().submit(runnable).addListener(errorLogger()));
    }

    @Override
    public FutureTaskId runSync(Runnable runnable, long ticks) {
        // ViaVersion seems to not need to run delayed tasks on main thread
        return new FutureTaskId(ViaProtocolHack.instance().eventLoop()
                .schedule(() -> runSync(runnable), ticks * 50, TimeUnit.MILLISECONDS)
                .addListener(errorLogger())
        );
    }

    @Override
    public FutureTaskId runRepeatingSync(Runnable runnable, long ticks) {
        // ViaVersion seems to not need to run repeating tasks on main thread
        return new FutureTaskId(ViaProtocolHack.instance().eventLoop()
                .scheduleAtFixedRate(() -> runSync(runnable), 0, ticks * 50, TimeUnit.MILLISECONDS)
                .addListener(errorLogger())
        );
    }

    @Override
    public ViaCommandSender[] getOnlinePlayers() {
        return new ViaCommandSender[0];
    }

    @Override
    public void sendMessage(UUID uuid, String s) {
    }

    @Override
    public boolean kickPlayer(UUID uuid, String s) {
        return false;
    }

    @Override
    public boolean disconnect(UserConnection connection, String message) {
        return ViaPlatform.super.disconnect(connection, message);
    }

    protected <T extends Future<?>> GenericFutureListener<T> errorLogger() {
        return future -> {
            if (!future.isCancelled() && future.cause() != null) {
                future.cause().printStackTrace();
            }
        };
    }

    @Override
    public boolean isProxy() {
        return true;
    }

    @Override
    public void onReload() {
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public ViaVersionConfig getConf() {
        return config;
    }

    @Override
    public ViaAPI<UUID> getApi() {
        return api;
    }

    @Override
    public File getDataFolder() {
        return ViaProtocolHack.instance().directory();
    }

    @Override
    public String getPluginVersion() {
        return "4.4.3-SNAPSHOT";
    }

    @Override
    public String getPlatformName() {
        return "ViaProtocolHack by FlorianMichael";
    }

    @Override
    public String getPlatformVersion() {
        return "1.3.3.7";
    }

    @Override
    public boolean isPluginEnabled() {
        return true;
    }

    @Override
    public ConfigurationProvider getConfigurationProvider() {
        return config;
    }

    @Override
    public boolean isOldClientsAllowed() {
        return true;
    }

    @Override
    public Collection<UnsupportedSoftware> getUnsupportedSoftwareClasses() {
        return ViaPlatform.super.getUnsupportedSoftwareClasses();
    }

    @Override
    public boolean hasPlugin(String s) {
        return false;
    }

    @Override
    public JsonObject getDump() {
        return ViaProtocolHack.instance().provider().createDump();
    }
}
