package org.zapodot.junit5.jms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.JmsTemplate;
import org.zapodot.junit5.jms.annotations.EmbeddedJms;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("squid:S00112")
@ExtendWith(EmbeddedJmsBroker.class)
class EmbeddedJmsBrokerRequestReplySpringTest {

    private static final String TEST_MESSAGE = "Test message";

    private static final String DESTINATION = "queue:destination";

    @EmbeddedJms
    private ConnectionFactory connectionFactory;

    @DisplayName("Request/reply using Spring JMS Template")
    @Test
    void requestReply() throws Exception {
        final JmsOperations jmsOperations = createJmsTemplate();
        final ExecutorService executorService = Executors.newFixedThreadPool(1);
        try {
            final Future<String> messageFuture = executorService.submit(receiveMessage(jmsOperations));
            jmsOperations.convertAndSend(DESTINATION, TEST_MESSAGE);
            final String message = messageFuture.get(30L, TimeUnit.SECONDS);
            assertEquals(TEST_MESSAGE, message);
        } finally {
            executorService.shutdownNow();
        }
    }

    private JmsOperations createJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.afterPropertiesSet();
        return jmsTemplate;
    }

    private Callable<String> receiveMessage(final JmsOperations jmsOperations) {
        return () -> {

            final TextMessage textMessage = (TextMessage) jmsOperations.receive(DESTINATION);
            try {
                return textMessage.getText();
            } catch (JMSException e) {
                throw new IllegalStateException("Could not receive message", e);
            }
        };
    }
}