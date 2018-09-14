package org.zapodot.junit5.jms.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configuration for the Embedded JMS Broker
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface BrokerConfig {
    /**
     * The name of the broker. If not specified it will default to the name of the test method
     *
     * @return the provided
     */
    String name() default "";

    /**
     * Whether marshalling of messages should be enabled or not (default: false)
     *
     * @return a boolean string
     */
    String marshall() default "";

    /**
     * Whether message persistence should be enabled or not. Default is false
     *
     * @return a boolean string
     */
    String persistence() default "";
}
