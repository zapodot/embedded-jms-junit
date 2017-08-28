package org.zapodot.junit.jms;

import javax.jms.*;
import java.lang.IllegalStateException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public interface CallablesForSendingAndReceiving {

    static Callable<String> messagesFromTestQueue(final ConnectionFactory connectionFactory, final String queueName) {
        return () -> {
            final Connection connection = connectionFactory.createConnection();
            connection.start();
            try {
                final Session consumerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                try {
                    final Queue queue = consumerSession.createQueue(queueName);
                    final MessageConsumer messageConsumer = consumerSession.createConsumer(queue);
                    final Message message = messageConsumer.receive(TimeUnit.SECONDS.toMillis(30L));
                    if (message instanceof TextMessage) {
                        return ((TextMessage) message).getText();
                    } else {
                        throw new IllegalStateException("Illegal message type received");
                    }
                } finally {
                    consumerSession.close();
                }
            } finally {
                connection.close();
            }
        };
    }

    static Callable<Boolean> sendMessageToQueue(final ConnectionFactory connectionFactory,
                                                final String testQueueName,
                                                final String testMessage) throws JMSException {
        return () -> {
            final Connection connection = connectionFactory.createConnection();
            connection.start();

            final Session senderSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            final Queue senderQueue = senderSession.createQueue(testQueueName);
            final MessageProducer messageProducer = senderSession.createProducer(senderQueue);
            final TextMessage textMessage = senderSession.createTextMessage(testMessage);
            messageProducer.send(textMessage);
            messageProducer.close();
            senderSession.close();
            connection.close();
            return Boolean.TRUE;
        };
    }
}
