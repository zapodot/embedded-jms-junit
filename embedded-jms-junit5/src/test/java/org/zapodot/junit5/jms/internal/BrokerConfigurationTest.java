package org.zapodot.junit5.jms.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BrokerConfigurationTest {

    @Test
    void equalsNull() {
        assertNotEquals(BrokerConfigurationBuilder.instance().build(), null);
    }

    @Test
    void equalsOtherType() {
        assertNotEquals(BrokerConfigurationBuilder.instance().build(), "");
    }

    @Test
    void equalsSame() {
        final BrokerConfiguration brokerConfiguration = BrokerConfigurationBuilder.instance().build();
        assertEquals(brokerConfiguration, brokerConfiguration);
    }

    @Test
    void equalsSimilarInstances() {
        assertEquals(BrokerConfigurationBuilder.instance().build(),
                     BrokerConfigurationBuilder.instance().build());
    }

    @Test
    void testHashCode() {
        assertEquals(BrokerConfigurationBuilder.instance().build().hashCode(),
                     BrokerConfigurationBuilder.instance().build().hashCode());
    }

    @Test
    void testToString() {
        assertEquals(BrokerConfigurationBuilder.instance().build().toString(),
                     BrokerConfigurationBuilder.instance().build().toString());
    }
}