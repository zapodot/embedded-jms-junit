package org.zapodot.jms.common;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
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

    private EmbeddedJMSBrokerHolder(final BrokerService brokerService, final File tempDir) {
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
        return new EmbeddedJMSBrokerHolder(createBrokerService(name, marshal, persistent, tempDir), tempDir);
    }

    private static BrokerService createBrokerService(final String name,
                                                     boolean marshal,
                                                     boolean persistent,
                                                     File tempDir) {
        final BrokerService brokerService = new BrokerService();
        brokerService.setBrokerName(name);
        brokerService.setStartAsync(false);
        brokerService.setPersistent(persistent);
        brokerService.setDataDirectoryFile(tempDir);
        brokerService.setUseShutdownHook(false);
        try {
            brokerService.addConnector(createVmTransportServer(createVmTransportUri(name, marshal)));
        } catch (Exception e) {
            throw new IllegalStateException("Could not create VM Transport URI", e);
        }
        brokerService.setUseJmx(false);
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
        brokerService.stop();
        if (tempDir.isDirectory()) {
            MoreFiles.deleteRecursively(tempDir.toPath(), RecursiveDeleteOption.ALLOW_INSECURE);
        }
    }
}
