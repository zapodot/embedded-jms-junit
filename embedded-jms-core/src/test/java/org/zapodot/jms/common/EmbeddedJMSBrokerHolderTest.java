package org.zapodot.jms.common;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("squid:S00112")
class EmbeddedJMSBrokerHolderTest {

    @DisplayName("Create EmbeddedJMSBrokerHolder instance")
    @Test
    void create() throws Exception {
        try (final EmbeddedJMSBrokerHolder embeddedJmsBrokerHolder = EmbeddedJMSBrokerHolder
                .create("name", false, false)) {
            assertNotNull(embeddedJmsBrokerHolder.getBrokerService());
            assertFalse(embeddedJmsBrokerHolder.getBrokerService().isStarted());
        }
    }

    @DisplayName("Creating a broker using an illegal URI as name should fail")
    @Test
    void createFails() {
        assertThrows(IllegalStateException.class, () -> EmbeddedJMSBrokerHolder
                .create("\\\\\\", false, false));
    }

    @DisplayName("Creates two brokers with the same name should cause the second one to fail")
    @Test
    void createDouble() {

        final String name = "name";
        assertThrows(IllegalStateException.class, () -> {
            try (EmbeddedJMSBrokerHolder broker1 = EmbeddedJMSBrokerHolder.create(name, false, false)) {
                broker1.start();
                try (EmbeddedJMSBrokerHolder broker2 = EmbeddedJMSBrokerHolder.create(name, false, false)) {
                    broker2.start();
                }
            }

        });

    }

    @DisplayName("Create EmbeddedJMSBrokerHolder instance, start it and then stop it again")
    @Test
    @SuppressWarnings("squid:S4087")
    void startStop() throws Exception {
        try (final EmbeddedJMSBrokerHolder embeddedJmsBrokerHolder = EmbeddedJMSBrokerHolder
                .create("name", false, false)) {
            final BrokerService brokerService = embeddedJmsBrokerHolder.getBrokerService();
            assertNotNull(brokerService);
            embeddedJmsBrokerHolder.start();
            assertTrue(brokerService.isStarted());
            embeddedJmsBrokerHolder.close();
            assertTrue(embeddedJmsBrokerHolder.getBrokerService().isStopped());
        }
    }
}