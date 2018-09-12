package org.zapodot.junit5.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zapodot.junit5.jms.annotations.InjectEmbeddedJMS;

import javax.jms.ConnectionFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("An embedded JMS ActiveMQ broker")
@ExtendWith(EmbeddedJMSBroker.class)
class EmbeddedJMSBrokerNestedTest {

    @InjectEmbeddedJMS
    private ConnectionFactory connectionFactory;

    @DisplayName("when javax.jms.ConnectionFactory is injected")
    @Test
    void injectionWorks() {
        assertNotNull(connectionFactory);
    }

    @Nested
    @DisplayName("in a nested test class")
    class WhenNested {

        @InjectEmbeddedJMS
        private ActiveMQConnectionFactory activeMQConnectionFactory;

        @DisplayName("the injection fields from the outer class is available")
        @Test
        void injectionWorksForNestedClasses() {
            assertNotNull(connectionFactory);
        }

        @DisplayName("injection works also for the internal class")
        @Test
        void injectionIntoFieldInNestedClass() {
            assertNotNull(activeMQConnectionFactory);

        }
    }
}