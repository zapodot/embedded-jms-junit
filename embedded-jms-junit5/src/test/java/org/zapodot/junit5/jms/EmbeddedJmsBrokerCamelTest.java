package org.zapodot.junit5.jms;

import com.google.common.collect.ImmutableMap;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.LoggingLevel;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zapodot.junit5.jms.annotations.BrokerConfig;
import org.zapodot.junit5.jms.annotations.EmbeddedJms;

import javax.jms.ConnectionFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@BrokerConfig(persistence = "false")
@ExtendWith(EmbeddedJmsBroker.class)
class EmbeddedJmsBrokerCamelTest {

    private static final String MOCK_ENDPOINT_URI = "mock:output";

    private static final String JMS_DESTINATION_URI = "activemq:queue:inputQueue";

    @EmbeddedJms
    private ConnectionFactory connectionFactory;

    private JndiRegistry registry;

    private DefaultCamelContext camelContext;

    @BeforeEach
    public void setup() throws Exception {
        final ActiveMQComponent activeMQComponent = new ActiveMQComponent();
        activeMQComponent.setConnectionFactory(connectionFactory);
        activeMQComponent.setUseSingleConnection(true);

        this.registry = new JndiRegistry(ImmutableMap.of("activemq", activeMQComponent));

        this.camelContext = new DefaultCamelContext(registry);
        camelContext.setName(EmbeddedJmsBrokerCamelTest.class.getSimpleName() + "Context");

        try {
            this.camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() {
                    from(JMS_DESTINATION_URI)
                            .id(EmbeddedJmsBrokerCamelTest.class.getSimpleName())
                            .convertBodyTo(String.class)
                            .log(LoggingLevel.INFO, "Received message ${id} with body \"${body}\"")
                            .to(MOCK_ENDPOINT_URI);
                }
            });
            this.camelContext.start();
        } catch (Exception e) {
            if (this.camelContext.isStarted()) {
                this.camelContext.stop();
            }
        }


    }

    @Test
    void jmsUsingCamel() throws Exception {
        final String messageBody = "Hello Camel!";
        assertNotNull(camelContext);

        final MockEndpoint mockEndpoint = camelContext.getEndpoint(MOCK_ENDPOINT_URI, MockEndpoint.class);
        mockEndpoint.expectedBodiesReceived(messageBody);

        final ProducerTemplate template = camelContext.createProducerTemplate();
        template.start();
        template.sendBody(JMS_DESTINATION_URI, messageBody);

        MockEndpoint.assertIsSatisfied(camelContext);
    }

    @AfterEach
    public void tearDown() throws Exception {
        this.camelContext.stop();
        this.registry.close();
    }

}