package org.zapodot.junit5.jms.internal;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.platform.commons.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zapodot.jms.common.EmbeddedJMSBrokerHolder;

import javax.jms.ConnectionFactory;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Optional;

/**
 * FieldInjector - part of the internal API. May be removed, moved or changed without prior deprecation
 */
public class FieldInjector {

    private static final Logger LOGGER = LoggerFactory.getLogger(FieldInjector.class);

    private FieldInjector() {
        // Constructor added to avoid instantiation
    }

    public static void injectToInstance(final Object instance, final EmbeddedJMSBrokerHolder embeddedJMSBrokerHolder) {
        if (instance.getClass().isMemberClass()) {
            tryToFindOuterInstance(instance)
                    .ifPresent(i -> injectToInstance(i, embeddedJMSBrokerHolder));
        }
        InjectableFieldsAccessor.findInjectableFields(instance.getClass())
                                .stream()
                                .forEach(field -> injectConnectionFactoryOrURI(instance,
                                                                               field,
                                                                               embeddedJMSBrokerHolder));
    }

    private static Optional<Object> tryToFindOuterInstance(final Object innerInstance) {
        return ReflectionUtils.getOutermostInstance(innerInstance, innerInstance.getClass().getDeclaringClass());
    }

    private static void injectConnectionFactoryOrURI(final Object instance,
                                                     final Field field,
                                                     final EmbeddedJMSBrokerHolder embeddedJMSBrokerHolder) {
        boolean accessibleOriginal = field.isAccessible();
        field.setAccessible(true);
        try {
            if (ActiveMQConnectionFactory.class.isAssignableFrom(field.getType())) {
                LOGGER.debug("Will inject org.apache.activemq.ActiveMQConnectionFactory to field \"{}\"",
                             field.getName());
                field.set(instance, embeddedJMSBrokerHolder.getActiveMQConnectionFactory());
            } else if (ConnectionFactory.class.isAssignableFrom(field.getType())) {
                LOGGER.debug("Will inject javax.jms.ConnectionFactory to field \"{}\"", field.getName());
                field.set(instance, embeddedJMSBrokerHolder.getConnectionFactory());
            } else if (URI.class.isAssignableFrom(field.getType())) {
                LOGGER.debug("Will inject java.net.URI to field \"{}\"", field.getName());
                field.set(instance, embeddedJMSBrokerHolder.getBrokerUri());
            }

        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new IllegalStateException(
                    "Could not inject embedded javax.jms.ConnectionFactory, ActiveMQConnectionFactory or URI to field",
                    e);
        } finally {
            field.setAccessible(accessibleOriginal);
        }
    }
}
