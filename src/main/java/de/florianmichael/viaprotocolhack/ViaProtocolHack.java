package de.florianmichael.viaprotocolhack;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import de.florianmichael.viaprotocolhack.platform.CustomViaProviders;
import de.florianmichael.viaprotocolhack.platform.ViaVersionPlatform;
import de.florianmichael.viaprotocolhack.platform.viaversion.CustomViaInjector;
import de.florianmichael.viaprotocolhack.util.JLoggerToLog4J;
import de.florianmichael.viaprotocolhack.util.VersionList;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class ViaProtocolHack {
    private final static ViaProtocolHack instance = new ViaProtocolHack();

    private final ExecutorService executorService = Executors.newFixedThreadPool(8, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaProtocolHack-%d").build());
    private final EventLoop eventLoop = new DefaultEventLoop(executorService);
    private final Logger logger = new JLoggerToLog4J(LogManager.getLogger("ViaProtocolHack"));

    private INativeProvider provider;
    private File directory;

    public void init(final INativeProvider provider) throws Exception {
        this.provider = provider;
        this.directory = new File(this.provider.run(), "ViaProtocolHack");

        VersionList.registerProtocols();

        CompletableFuture.runAsync(() -> {
            final ViaVersionPlatform platform = new ViaVersionPlatform(this.logger());

            Via.init(ViaManagerImpl.builder().injector(new CustomViaInjector()).loader(new CustomViaProviders()).platform(platform).build());
            platform.init();

            ((ViaManagerImpl) Via.getManager()).init();
        });
    }

    public INativeProvider provider() {
        return provider;
    }

    public File directory() {
        return directory;
    }

    public ExecutorService executorService() {
        return executorService;
    }

    public EventLoop eventLoop() {
        return eventLoop;
    }

    public Logger logger() {
        return logger;
    }

    public static ViaProtocolHack instance() {
        return instance;
    }
}
