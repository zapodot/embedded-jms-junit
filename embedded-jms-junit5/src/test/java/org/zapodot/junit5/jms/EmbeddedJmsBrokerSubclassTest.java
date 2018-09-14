package org.zapodot.junit5.jms;

import org.junit.jupiter.api.Test;
import org.zapodot.junit5.jms.annotations.EmbeddedJms;

import javax.jms.ConnectionFactory;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EmbeddedJmsBrokerSubclassTest extends AbstractJmsTest {

    @EmbeddedJms
    private ConnectionFactory subConnectionFactory;

    @EmbeddedJms
    private URI brokerUri;

    @Test
    void testIfInjectionWorksIfDefinedInSuperClass() {
        assertNotNull(super.connectionFactory);
        assertNotNull(subConnectionFactory);
        assertEquals("vm://" + AbstractJmsTest.BROKER_SUPER_BROKER, brokerUri.toString());
    }
}
