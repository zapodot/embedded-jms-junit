package org.zapodot.junit5.jms;

import org.junit.jupiter.api.extension.ExtendWith;
import org.zapodot.junit5.jms.annotations.BrokerConfig;
import org.zapodot.junit5.jms.annotations.EmbeddedJms;

import javax.jms.ConnectionFactory;

@ExtendWith(EmbeddedJmsBroker.class)
@BrokerConfig(name = AbstractJmsTest.BROKER_SUPER_BROKER)
public abstract class AbstractJmsTest {

    protected static final String BROKER_SUPER_BROKER = "SuperDuperBroker";

    @EmbeddedJms
    protected ConnectionFactory connectionFactory;
}
