package org.zapodot.junit5.jms.internal;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.platform.commons.util.AnnotationUtils;
import org.zapodot.junit5.jms.annotations.InjectEmbeddedJMS;

import javax.jms.ConnectionFactory;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;

/**
 * InjectableFieldsAccessor - part of the internal API. May be removed, moved or changed without prior deprecation
 */
class InjectableFieldsAccessor {

    private InjectableFieldsAccessor() {
    }


    static List<Field> findInjectableFields(final Class<?> type) {
        return AnnotationUtils.findAnnotatedFields(type,
                                                   InjectEmbeddedJMS.class,
                                                   field -> ConnectionFactory.class
                                                           .isAssignableFrom(field.getType()) ||
                                                           ActiveMQConnectionFactory.class
                                                                   .isAssignableFrom(field.getType()) ||
                                                           URI.class
                                                                   .isAssignableFrom(field.getType()));
    }
}
