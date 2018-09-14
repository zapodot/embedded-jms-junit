package org.zapodot.junit5.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zapodot.junit5.jms.annotations.EmbeddedJms;

import javax.jms.ConnectionFactory;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(EmbeddedJmsBroker.class)
class EmbeddedJmsDefaultSetupTest {

    @EmbeddedJms
    private ConnectionFactory connectionFactory;

    @EmbeddedJms
    private ActiveMQConnectionFactory activeMQConnectionFactory;

    @EmbeddedJms
    private URI brokerUri;


    @Test
    void fieldsAreInjected() {
        assertNotNull(connectionFactory);
        assertNotNull(activeMQConnectionFactory);
        assertNotNull(brokerUri);
    }

    @Test
    void brokerUriGenerated() {
        assertEquals("vm://" + getClass().getSimpleName(), brokerUri.toString());
    }
}