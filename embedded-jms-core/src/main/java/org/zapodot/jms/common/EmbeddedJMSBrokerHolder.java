package org.zapodot.jms.common;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.memory.MemoryPersistenceAdapter;
import org.apache.activemq.transport.TransportServer;
import org.apache.activemq.transport.vm.VMTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.ConnectionFactory;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Internal API. May be removed, moved or changed without prior deprecation
 */
public class EmbeddedJMSBrokerHolder implements AutoCloseable, ConnectionFactoryAccessor, ActiveMQConnectionFactoryAccessor, BrokerURIAccessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedJMSBrokerHolder.class);

    private final BrokerService brokerService;

    private final File tempDir;

    EmbeddedJMSBrokerHolder(final BrokerService brokerService, final File tempDir) {
        Preconditions.checkNotNull(brokerService);
        this.brokerService = brokerService;

        Preconditions.checkNotNull(tempDir);
        this.tempDir = tempDir;
    }

    public BrokerService getBrokerService() {
        return brokerService;
    }

    @Override
    public ConnectionFactory getConnectionFactory() {
        return getActiveMQConnectionFactory();
    }

    @Override
    public ActiveMQConnectionFactory getActiveMQConnectionFactory() {
        return new ActiveMQConnectionFactory(brokerService.getVmConnectorURI());
    }

    @Override
    public URI getBrokerUri() {
        return brokerService.getVmConnectorURI();
    }

    public static EmbeddedJMSBrokerHolder create(final String name, boolean marshal, boolean persistent) {
        final File tempDir = Files.createTempDir();
        LOGGER.debug("Created temporary directory: \"{}\"", tempDir.getAbsolutePath());
        return new EmbeddedJMSBrokerHolder(createAndConfigureBrokerService(new BrokerSettings(name,
                                                                                              marshal,
                                                                                              persistent,
                                                                                              tempDir)), tempDir);
    }

    private static BrokerService createAndConfigureBrokerService(final BrokerSettings brokerSettings) {
        return configureBrokerService(new BrokerService(), brokerSettings);
    }

    static BrokerService configureBrokerService(final BrokerService brokerService,
                                                final BrokerSettings brokerSettings) {
        Preconditions.checkNotNull(brokerService);
        Preconditions.checkNotNull(brokerSettings);
        brokerService.setBrokerName(brokerSettings.getName());
        brokerService.setStartAsync(false);
        brokerService.setPersistent(brokerSettings.isPersistent());
        brokerService.setUseJmx(false);
        brokerService.setDataDirectoryFile(brokerSettings.getTempDir());
        brokerService.setUseShutdownHook(false);
        try {
            if (brokerSettings.isPersistent()) {
                brokerService.setPersistenceAdapter(new MemoryPersistenceAdapter());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not enable the MemoryPersistenceAdapter");
        }
        try {
            brokerService.addConnector(createVmTransportServer(createVmTransportUri(brokerSettings.getName(),
                                                                                    brokerSettings.isMarshal())));
        } catch (Exception e) {
            throw new IllegalStateException("Could not create VM Transport URI", e);
        }
        return brokerService;
    }

    private static TransportServer createVmTransportServer(final URI vmUri) {
        try {
            return new VMTransportFactory().doBind(vmUri);
        } catch (IOException e) {
            throw new IllegalStateException("Could not setup VM transport", e);
        }
    }

    private static URI createVmTransportUri(final String name, final boolean marshal) {
        try {
            return new URI(String.format("vm://%s?marshal=%s", name, marshal));
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Could not create an URI for the VM Transport", e);
        }
    }

    public void start() {
        try {
            brokerService.start(true);
        } catch (Exception e) {
            throw new IllegalStateException("Could not start the embedded JMS broker", e);
        }
    }

    @Override
    public void close() throws Exception {
        try {
            brokerService.stop();
        } finally {
            if (tempDir.isDirectory()) {
                MoreFiles.deleteRecursively(tempDir.toPath(), RecursiveDeleteOption.ALLOW_INSECURE);
            }
        }
    }
}
