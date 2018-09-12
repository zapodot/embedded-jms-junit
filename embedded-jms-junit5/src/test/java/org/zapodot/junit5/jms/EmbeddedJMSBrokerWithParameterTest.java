package org.zapodot.junit5.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zapodot.junit5.jms.annotations.InjectEmbeddedJMS;

import javax.jms.ConnectionFactory;
import java.net.URI;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(EmbeddedJMSBroker.class)
@DisplayName("Inject parameter to test")
class EmbeddedJMSBrokerWithParameterTest {

    @DisplayName("supports parameter type javax.jms.ConnectionFactory")
    @Test
    void connectionFactoryParameter(@InjectEmbeddedJMS ConnectionFactory connectionFactory) {
        assertNotNull(connectionFactory);
    }

    @DisplayName("supports parameter type org.apache.activemq.ActiveMQConnectionFactory")
    @Test
    void activeMQConnectionFactoryParameter(@InjectEmbeddedJMS ActiveMQConnectionFactory activeMQConnectionFactory) {
        assertNotNull(activeMQConnectionFactory);
    }

    @DisplayName("supports parameter type java.net.URI")
    @Test
    void brokerUriParameter(@InjectEmbeddedJMS URI brokerURI) {
        assertNotNull(brokerURI);
    }

    @DisplayName("not supported parameter type java.sql.Connection")
    @Test
    void otherType(@InjectEmbeddedJMS Connection connection) {
        // If the parameter is not supported it will neither be injected nor fail
        assertNull(connection);
    }
}