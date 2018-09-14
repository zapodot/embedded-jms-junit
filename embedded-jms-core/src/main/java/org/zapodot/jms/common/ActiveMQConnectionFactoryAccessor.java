package org.zapodot.jms.common;

import org.apache.activemq.ActiveMQConnectionFactory;

public interface ActiveMQConnectionFactoryAccessor {
    ActiveMQConnectionFactory getActiveMQConnectionFactory();
}
