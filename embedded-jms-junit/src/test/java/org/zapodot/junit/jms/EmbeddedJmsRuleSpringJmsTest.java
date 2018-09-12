package org.zapodot.junit.jms;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.concurrent.*;

import static org.junit.Assert.assertThat;


public class EmbeddedJmsRuleSpringJmsTest {

    private static final String TEST_MESSAGE = "Test message";
    @Rule
    public EmbeddedJmsRule embeddedJmsRule = EmbeddedJmsRule.builder().build();

    @Test
    public void jmsOperation() throws Exception {

        final JmsOperations jmsOperations = createSpringJmsOperation();
        final ExecutorService executorService = Executors.newFixedThreadPool(1);
        try {
            final Future<String> messageFuture = executorService.submit(receiveMessage(jmsOperations));
            jmsOperations.convertAndSend(TEST_MESSAGE);
            final String message = messageFuture.get(30L, TimeUnit.SECONDS);
            assertThat(message, CoreMatchers.equalTo(TEST_MESSAGE));
        } finally {
            executorService.shutdownNow();
        }
    }

    private JmsOperations createSpringJmsOperation() {
        final JmsTemplate jmsTemplate = new JmsTemplate(embeddedJmsRule.connectionFactory());
        jmsTemplate.setDefaultDestinationName(getClass().getSimpleName());
        jmsTemplate.setReceiveTimeout(TimeUnit.SECONDS.toMillis(10L));
        jmsTemplate.afterPropertiesSet();
        return jmsTemplate;
    }

    private Callable<String> receiveMessage(final JmsOperations jmsOperations) {
        return () -> {

            final TextMessage textMessage = (TextMessage) jmsOperations.receive();
            try {
                return textMessage.getText();
            } catch (JMSException e) {
                throw new IllegalStateException("Could not receive message", e);
            }
        };
    }
}