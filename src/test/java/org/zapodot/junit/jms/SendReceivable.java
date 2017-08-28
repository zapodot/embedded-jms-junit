package org.zapodot.junit.jms;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import java.util.concurrent.*;

import static org.zapodot.junit.jms.CallablesForSendingAndReceiving.messagesFromTestQueue;
import static org.zapodot.junit.jms.CallablesForSendingAndReceiving.sendMessageToQueue;

public interface SendReceivable {
    default String sendReceive(final ConnectionFactory connectionFactory, final String queueName, final String textMessage) throws JMSException {
        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
        final Future<String> messageFuture = executorService.submit(messagesFromTestQueue(connectionFactory, queueName));
            executorService.submit(sendMessageToQueue(connectionFactory, queueName, textMessage));
            return messageFuture.get(30L, TimeUnit.SECONDS);
        } catch (InterruptedException|ExecutionException|TimeoutException e) {
            throw new IllegalStateException("An error occurred during send/receive from JMS", e);
        } finally {
            executorService.shutdownNow();
        }
    }
}
