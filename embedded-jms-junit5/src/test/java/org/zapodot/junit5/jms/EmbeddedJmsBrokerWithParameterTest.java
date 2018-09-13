package org.zapodot.junit5.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zapodot.junit5.jms.annotations.EmbeddedJms;

import javax.jms.ConnectionFactory;
import java.net.URI;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(EmbeddedJmsBroker.class)
@DisplayName("Inject parameter to test")
class EmbeddedJmsBrokerWithParameterTest {

    @DisplayName("supports parameter type javax.jms.ConnectionFactory")
    @Test
    void connectionFactoryParameter(@EmbeddedJms ConnectionFactory connectionFactory) {
        assertNotNull(connectionFactory);
    }

    @DisplayName("supports parameter type org.apache.activemq.ActiveMQConnectionFactory")
    @Test
    void activeMQConnectionFactoryParameter(@EmbeddedJms ActiveMQConnectionFactory activeMQConnectionFactory) {
        assertNotNull(activeMQConnectionFactory);
    }

    @DisplayName("supports parameter type java.net.URI")
    @Test
    void brokerUriParameter(@EmbeddedJms URI brokerURI) {
        assertNotNull(brokerURI);
    }

    @DisplayName("not supported parameter type java.sql.Connection")
    @Test
    void otherType(@EmbeddedJms Connection connection) {
        // If the parameter is not supported it will neither be injected nor fail
        assertNull(connection);
    }
}