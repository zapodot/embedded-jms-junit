package org.zapodot.junit5.jms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.zapodot.junit5.jms.annotations.EmbeddedJms;

import javax.jms.ConnectionFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Injection using @RegisterExtension on static field")
class EmbeddedJmsBrokerRegisterExtensionStaticFieldTest {

    @RegisterExtension
    static EmbeddedJmsBroker embeddedJMSBroker = new EmbeddedJmsBroker();

    @EmbeddedJms
    private ConnectionFactory connectionFactory;

    @DisplayName("injection on fields works")
    @Test
    void checkInjectionViaRegisterExtension() {
        assertNotNull(connectionFactory);
    }
}