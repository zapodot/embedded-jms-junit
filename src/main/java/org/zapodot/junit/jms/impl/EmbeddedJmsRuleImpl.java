package org.zapodot.junit.jms.impl;

import com.google.common.io.Files;
import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.transport.TransportServer;
import org.apache.activemq.transport.vm.VMTransportFactory;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.zapodot.junit.jms.EmbeddedJmsRule;

import javax.jms.ConnectionFactory;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Implementation. Part of the internal API
 */
public class EmbeddedJmsRuleImpl implements EmbeddedJmsRule {

    private final String predefinedName;
    private final boolean marshal;
    private final boolean persistent;
    private BrokerService brokerService;
    private File tempDir;

    public EmbeddedJmsRuleImpl(final String predefinedName, final boolean marshal, final boolean persistent) {
        this.predefinedName = predefinedName;
        this.marshal = marshal;
        this.persistent = persistent;
    }

    @Override
    public ConnectionFactory connectionFactory() {
        return activeMqConnectionFactory();
    }

    @Override
    public ActiveMQConnectionFactory activeMqConnectionFactory() {
        if (brokerService == null) {
            throw new IllegalStateException("Can not create ConnectionFactory before the broker has started");
        } else {
            return new ActiveMQConnectionFactory(brokerService.getVmConnectorURI());
        }
    }

    @Override
    public URI brokerUri() {
        if (brokerService == null) {
            throw new IllegalStateException("Can not create broker URI before the broker has started");
        } else {
            return brokerService.getVmConnectorURI();
        }
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                startService(getBrokerName(description));
                try {
                    base.evaluate();
                } finally {
                    stopService();
                }
            }
        };
    }

    private String getBrokerName(final Description description) {
        if (predefinedName == null) {
            return extractNameFromDescription(description);
        } else {
            return predefinedName;
        }
    }

    private String extractNameFromDescription(final Description description) {
        return description.getTestClass() == null ? description.getClassName() : description.getTestClass()
                                                                                            .getSimpleName();
    }

    private void startService(final String name) {
        try {
            brokerService = createBrokerService(name);
            brokerService.start(true);
        } catch (Exception e) {
            throw new IllegalStateException("Could not start broker", e);
        }
    }

    private void stopService() {
        try {
            brokerService.stop();
            MoreFiles.deleteRecursively(tempDir.toPath(), RecursiveDeleteOption.ALLOW_INSECURE);
            brokerService = null;
        } catch (Exception e) {
            throw new IllegalStateException("Could not stop broker", e);
        }
    }

    private URI createVmTransportUri(final String name) {
        try {
            return new URI(String.format("vm://%s?marshal=%s", name, marshal));
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Could not create an URI for the VM Transport", e);
        }
    }

    private BrokerService createBrokerService(final String name) {
        final BrokerService broker = new BrokerService();
        broker.setPersistent(persistent);
        broker.setBrokerName(name);
        broker.setStartAsync(false);
        tempDir = Files.createTempDir();
        broker.setDataDirectoryFile(tempDir);
        try {
            broker.addConnector(createVmTransportServer(createVmTransportUri(name)));
        } catch (Exception e) {
            throw new IllegalStateException("Could not create VM Transport URI", e);
        }
        broker.setUseJmx(false);

        return broker;
    }

    private TransportServer createVmTransportServer(final URI vmUri) {
        try {
            return new VMTransportFactory().doBind(vmUri);
        } catch (IOException e) {
            throw new IllegalStateException("Could not setup VM transport", e);
        }
    }

}
