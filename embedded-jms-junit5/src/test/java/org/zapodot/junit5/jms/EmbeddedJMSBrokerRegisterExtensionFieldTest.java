package org.zapodot.junit5.jms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.zapodot.junit5.jms.annotations.BrokerConfig;
import org.zapodot.junit5.jms.annotations.InjectEmbeddedJMS;

import javax.jms.ConnectionFactory;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Injection using @RegisterExtension on field")
@BrokerConfig(name = EmbeddedJMSBrokerRegisterExtensionFieldTest.BROKER_NAME)
class EmbeddedJMSBrokerRegisterExtensionFieldTest {

    static final String BROKER_NAME = "StaticRegisterExtensionBroker";

    @RegisterExtension
    final EmbeddedJMSBroker embeddedJMSBroker = new EmbeddedJMSBroker();

    @InjectEmbeddedJMS
    private ConnectionFactory connectionFactory;

    @InjectEmbeddedJMS
    private URI brokerUri;

    @DisplayName("injection on fields works")
    @Test
    void checkInjectionViaRegisterExtension() {
        assertNotNull(connectionFactory);
    }

    @DisplayName("class level configuration not used")
    @Test
    void classLevelConfigurationNotUsed() {
        assertNotNull(brokerUri);
        assertNotEquals("vm://" + BROKER_NAME, brokerUri.toString());
    }
}