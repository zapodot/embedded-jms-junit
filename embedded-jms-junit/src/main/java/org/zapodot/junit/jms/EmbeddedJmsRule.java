package org.zapodot.junit.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.rules.TestRule;

import javax.jms.ConnectionFactory;
import java.net.URI;

/**
 * The general contract for the EmbeddedJmsRule.
 *
 * Example of use:
 * <code>
 *      public class MyTests {
 *
 *          &#x00040;Rule
 *          public EmbeddedJmsRule embeddedJmsRule = EmbeddedJmsRule.builder().build();
 *
 *          &#x00040;Test
 *          public void testJmsIntegration() {
 *              final ConnectionFactory connectionFactory = embeddedJmsRule.connectionFactory();
 *              // Do stuff
 *              // ...
 *          }
 *
 *      }
 * </code>
 */
public interface EmbeddedJmsRule extends TestRule {

    static EmbeddedJmsRuleBuilder builder() {
        return new EmbeddedJmsRuleBuilder();
    }

    /**
     * Creates a ConnectionFactory that connects to the embedded broker
     *
     * @return a fresh instance of {@link ConnectionFactory}
     */
    ConnectionFactory connectionFactory();

    /**
     * Creates a {@link ActiveMQConnectionFactory} for those who needs access to the AMQ specific interface
     *
     * @return a fresh instance of {@link ActiveMQConnectionFactory}
     */
    ActiveMQConnectionFactory activeMqConnectionFactory();

    /**
     * An URI that may be used to connect to the embedded broker
     *
     * @return a {@link URI}
     */
    URI brokerUri();
}
