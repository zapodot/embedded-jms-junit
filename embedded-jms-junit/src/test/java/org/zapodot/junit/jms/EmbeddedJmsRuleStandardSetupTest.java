package org.zapodot.junit.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Rule;
import org.junit.Test;

import javax.jms.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class EmbeddedJmsRuleStandardSetupTest implements SendReceivable {

    private static final String TEST_QUEUE = "test.queue";
    private static final String TEST_MESSAGE = "Test message";
    @Rule
    public EmbeddedJmsRule embeddedJmsRule = EmbeddedJmsRule.builder().build();

    @Test
    public void connectionFactory() throws Exception {

        final ConnectionFactory connectionFactory = embeddedJmsRule.connectionFactory();
        assertThat(connectionFactory, notNullValue(ConnectionFactory.class));
    }

    @Test
    public void connectAndSendAndReceive() throws Exception {
        final ConnectionFactory connectionFactory = embeddedJmsRule.connectionFactory();
        assertThat(sendReceive(connectionFactory, TEST_QUEUE, TEST_MESSAGE), equalTo(TEST_MESSAGE));

    }

    @Test
    public void usingUri() throws Exception {
        final ActiveMQConnectionFactory mqConnectionFactory = new ActiveMQConnectionFactory(embeddedJmsRule.brokerUri());
        final Connection connection = mqConnectionFactory.createConnection();
        connection.start();
        try {
            final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            try {
                final Queue queue = session.createQueue(TEST_QUEUE);

                final MessageProducer producer = session.createProducer(queue);
                final QueueBrowser queueBrowser = session.createBrowser(queue);
                try {

                    final String text = "Fire and forget";
                    final TextMessage textMessage = session.createTextMessage(text);
                    producer.send(textMessage);
                    assertThat(((TextMessage) queueBrowser.getEnumeration().nextElement()).getText(), equalTo(text));
                } finally {
                    queueBrowser.close();
                    producer.close();
                }

            } finally {
                session.close();
            }
        } finally {
            connection.close();
        }
    }
}