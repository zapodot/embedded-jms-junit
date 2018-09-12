package org.zapodot.junit5.jms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zapodot.junit5.jms.annotations.BrokerConfig;
import org.zapodot.junit5.jms.annotations.InjectEmbeddedJMS;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(EmbeddedJMSBroker.class)
@BrokerConfig(name = EmbeddedJMSBrokerWithConfigTest.STUFF_BROKER)
class EmbeddedJMSBrokerWithConfigTest {

    protected static final String STUFF_BROKER = "StuffBroker";

    protected static final String PRETTY_BROKER = "PrettyBroker";

    @InjectEmbeddedJMS
    private URI uri;

    @Test
    void configuredUsingBrokerConfig() {
        assertNotNull(uri);
        assertEquals("vm://" + STUFF_BROKER, uri.toString());
    }

    @BrokerConfig(name = PRETTY_BROKER)
    @Test
    void brokerConfigOverriddenOnMethod() {
        assertEquals("vm://" + PRETTY_BROKER, uri.toString());
    }
}