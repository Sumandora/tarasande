package de.enzaxd.viaforge;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import de.enzaxd.viaforge.equals.VersionList;
import de.enzaxd.viaforge.platform.Injector;
import de.enzaxd.viaforge.platform.Platform;
import de.enzaxd.viaforge.platform.ProviderLoader;
import de.enzaxd.viaforge.util.JLoggerToLog4j;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoop;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.SharedConstants;
import org.apache.logging.log4j.LogManager;
import su.mandora.tarasande.value.ValueNumber;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

public class ViaForge {

    private final Logger jLogger = new JLoggerToLog4j(LogManager.getLogger("ViaForge"));
    private final ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaForge-%d").build();
    private final ExecutorService asyncExecutor = Executors.newFixedThreadPool(8, factory);
    private final EventLoop eventLoop = new DefaultEventLoopGroup(1, factory).next();
    private ValueNumber protocol;

    public void build() {
        this.protocol = new ValueNumber(this, "Protocol", Integer.MIN_VALUE, SharedConstants.getProtocolVersion(), Integer.MAX_VALUE, 1, true);
        final CompletableFuture<Void> initFuture = new CompletableFuture<>();

        eventLoop.submit(initFuture::join);

        try {
            VersionList.registerProtocols();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        final File file = new File("ViaForge");

        if (file.mkdir())
            this.getjLogger().info("Creating ViaForge Folder");

        Via.init(
                ViaManagerImpl.builder()
                        .injector(new Injector())
                        .loader(new ProviderLoader())
                        .platform(new Platform(file))
                        .build()
        );

        MappingDataLoader.enableMappingsCache();
        ((ViaManagerImpl) Via.getManager()).init();

        initFuture.complete(null);
    }

    public int getVersion() {
        return (int) protocol.getValue();
    }

    public void setVersion(final int target) {
        this.protocol.setValue(target);
    }

    public Logger getjLogger() {
        return jLogger;
    }

    public ExecutorService getAsyncExecutor() {
        return asyncExecutor;
    }

    public EventLoop getEventLoop() {
        return eventLoop;
    }
}
