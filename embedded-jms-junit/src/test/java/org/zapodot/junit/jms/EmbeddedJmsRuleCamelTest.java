package org.zapodot.junit.jms;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.LoggingLevel;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Rule;
import org.junit.Test;

public class EmbeddedJmsRuleCamelTest extends CamelTestSupport {

    private static final String MOCK_ENDPOINT_URI = "mock:output";
    private static final String JMS_DESTINATION_URI = "activemq:queue:inputQueue";
    @Rule
    public EmbeddedJmsRule embeddedJmsRule = EmbeddedJmsRule.builder().build();

    private ActiveMQComponent activeMQComponent() {
        final ActiveMQComponent activeMQComponent = new ActiveMQComponent();
        activeMQComponent.setConnectionFactory(embeddedJmsRule.connectionFactory());
        return activeMQComponent;
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        final JndiRegistry registry = super.createRegistry();
        registry.bind("activemq", activeMQComponent());
        return registry;
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(JMS_DESTINATION_URI)
                        .id("testRoute")
                        .convertBodyTo(String.class)
                        .log(LoggingLevel.INFO, "Received message ${id} with body \"${body}\"")
                        .to(MOCK_ENDPOINT_URI);

            }
        };
    }

    @Override
    public boolean isUseRouteBuilder() {
        return true;
    }

    @Override
    protected int getShutdownTimeout() {
        return 5;
    }

    @Test
    public void sendAndReceiveUsingCamel() throws Exception {

        final String messageBody = "Hello Camel!";
        getMockEndpoint(MOCK_ENDPOINT_URI).expectedBodiesReceived(messageBody);

        template.sendBody(JMS_DESTINATION_URI, messageBody);

        assertMockEndpointsSatisfied();

    }
}