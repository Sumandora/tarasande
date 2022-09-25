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

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

public class ViaForge implements ClientModInitializer {

    public final static int SHARED_VERSION = SharedConstants.getProtocolVersion();
    public static int CURRENT_VERSION = SHARED_VERSION;

    private static final ViaForge instance = new ViaForge();
    private final Logger jLogger = new JLoggerToLog4j(LogManager.getLogger("ViaForge"));
    private final CompletableFuture<Void> initFuture = new CompletableFuture<>();
    private final ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaForge-%d").build();
    private final ExecutorService asyncExecutor = Executors.newFixedThreadPool(8, factory);
    private final EventLoop eventLoop = new DefaultEventLoopGroup(1, factory).next();
    private File file;

    public static ViaForge getInstance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        eventLoop.submit(initFuture::join);

        try {
            VersionList.registerProtocols();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        this.file = new File("ViaForge");
        if (this.file.mkdir())
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

    public Logger getjLogger() {
        return jLogger;
    }

    public CompletableFuture<Void> getInitFuture() {
        return initFuture;
    }

    public ExecutorService getAsyncExecutor() {
        return asyncExecutor;
    }

    public EventLoop getEventLoop() {
        return eventLoop;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
