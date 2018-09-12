package org.zapodot.junit5.jms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zapodot.junit5.jms.annotations.BrokerConfig;
import org.zapodot.junit5.jms.annotations.InjectEmbeddedJMS;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Extension with custom configuration")
@ExtendWith(EmbeddedJMSBroker.class)
@BrokerConfig(name = EmbeddedJMSBrokerWithConfigTest.STUFF_BROKER)
class EmbeddedJMSBrokerWithConfigTest {

    static final String STUFF_BROKER = "StuffBroker";

    private static final String PRETTY_BROKER = "PrettyBroker";

    @InjectEmbeddedJMS
    private URI uri;

    @DisplayName("class level")
    @Test
    void configuredUsingBrokerConfig() {
        assertNotNull(uri);
        assertEquals("vm://" + STUFF_BROKER, uri.toString());
    }

    @DisplayName("method level configuration which overrides class level")
    @BrokerConfig(name = PRETTY_BROKER)
    @Test
    void brokerConfigOverriddenOnMethod() {
        assertEquals("vm://" + PRETTY_BROKER, uri.toString());
    }
}