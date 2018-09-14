package org.zapodot.junit5.jms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zapodot.junit5.jms.annotations.BrokerConfig;
import org.zapodot.junit5.jms.annotations.EmbeddedJms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(EmbeddedJmsBroker.class)
@BrokerConfig(persistence = "true")
class EmbeddedJmsBrokerPersistenceTest {

    private static final String PERSISTED_MESSAGE = "PersistedMessage";

    @EmbeddedJms
    private ConnectionFactory connectionFactory;

    @Test
    void name() throws JMSException {
        assertNotNull(connectionFactory);

        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            final Queue queue = session.createQueue("test.queue");
            final MessageProducer producer = session.createProducer(queue);
            final QueueBrowser queueBrowser = session.createBrowser(queue);
            try {
                final TextMessage textMessage = session.createTextMessage(PERSISTED_MESSAGE);

                producer.send(textMessage);
                assertEquals(PERSISTED_MESSAGE, ((TextMessage) queueBrowser.getEnumeration().nextElement()).getText());

            } finally {
                queueBrowser.close();
                producer.close();
            }
        } finally {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}