package org.zapodot.junit5.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zapodot.junit5.jms.annotations.InjectEmbeddedJMS;

import javax.jms.ConnectionFactory;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(EmbeddedJMSBroker.class)
class InjectEmbeddedJMSDefaultSetupTest {

    @InjectEmbeddedJMS
    private ConnectionFactory connectionFactory;

    @InjectEmbeddedJMS
    private ActiveMQConnectionFactory activeMQConnectionFactory;

    @InjectEmbeddedJMS
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