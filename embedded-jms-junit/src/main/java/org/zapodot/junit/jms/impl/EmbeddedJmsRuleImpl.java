package org.zapodot.junit.jms.impl;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.zapodot.jms.common.EmbeddedJMSBrokerHolder;
import org.zapodot.junit.jms.EmbeddedJmsRule;

import javax.jms.ConnectionFactory;
import java.net.URI;

/**
 * Implementation. Part of the internal API
 */
public class EmbeddedJmsRuleImpl implements EmbeddedJmsRule {

    private final String predefinedName;
    private final boolean marshal;
    private final boolean persistent;

    private EmbeddedJMSBrokerHolder jmsBrokerHolder;

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
        if (jmsBrokerHolder == null) {
            throw new IllegalStateException("Can not create ConnectionFactory before the broker has started");
        } else {
            return jmsBrokerHolder.getActiveMQConnectionFactory();
        }
    }

    @Override
    public URI brokerUri() {
        if (jmsBrokerHolder == null) {
            throw new IllegalStateException("Can not create broker URI before the broker has started");
        } else {
            return jmsBrokerHolder.getBrokerUri();
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
            jmsBrokerHolder = EmbeddedJMSBrokerHolder.create(name, marshal, persistent);
            jmsBrokerHolder.start();
        } catch (Exception e) {
            throw new IllegalStateException("Could not start broker", e);
        }
    }

    private void stopService() {
        try {
            jmsBrokerHolder.close();
        } catch (Exception e) {
            throw new IllegalStateException("Could not stop broker", e);
        }
    }

}
