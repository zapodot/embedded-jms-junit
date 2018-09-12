package org.zapodot.jms.common;

import javax.jms.ConnectionFactory;

public interface ConnectionFactoryAccessor {
    ConnectionFactory getConnectionFactory();
}
