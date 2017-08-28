package org.zapodot.junit.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.rules.TestRule;

import javax.jms.ConnectionFactory;
import java.net.URI;

public interface EmbeddedJmsRule extends TestRule {

    /**
     * Creates a ConnectionFactory that connects to the embedded broker
     * @return a fresh instance of {@link ConnectionFactory}
     */
    ConnectionFactory connectionFactory();

    /**
     * Creates a {@link ActiveMQConnectionFactory} for those who needs access to the AMQ specific interface
     * @return a fresh instance of {@link ActiveMQConnectionFactory}
     */
    ActiveMQConnectionFactory activeMqConnectionFactory();

    /**
     * An URI that may be used to connect to the embedded broker
     * @return a {@link URI}
     */
    URI brokerUri();

    static EmbeddedJmsRuleBuilder builder() {
        return new EmbeddedJmsRuleBuilder();
    }
}
