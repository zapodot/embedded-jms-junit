package org.zapodot.junit5.jms;

import com.google.common.base.Strings;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.util.AnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zapodot.jms.common.EmbeddedJMSBrokerHolder;
import org.zapodot.junit5.jms.annotations.BrokerConfig;
import org.zapodot.junit5.jms.annotations.EmbeddedJms;
import org.zapodot.junit5.jms.internal.BrokerConfiguration;
import org.zapodot.junit5.jms.internal.BrokerConfigurationBuilder;
import org.zapodot.junit5.jms.internal.FieldInjector;

import javax.jms.ConnectionFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.Optional;

public class EmbeddedJmsBroker implements BeforeEachCallback, AfterEachCallback, TestInstancePostProcessor, ParameterResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedJmsBroker.class);

    private static final ExtensionContext.Namespace EMBEDDED_JMS_EXT = ExtensionContext.Namespace
            .create("org.zapodot.junit5.jms");

    private static final String TEST_INSTANCE = "TestInstance";

    private static final String STORE_EMBEDDED_JMS_BROKER = "EmbeddedJmsBroker";

    private static final String STORE_BROKER_CONFIGURATION = "BrokerConfiguration";

    private static EmbeddedJMSBrokerHolder getOrCreateEmbeddedJMSBrokerHolder(final ExtensionContext context) {
        EmbeddedJMSBrokerHolder embeddedJmsBrokerHolder = context.getStore(EMBEDDED_JMS_EXT)
                                                                 .get(STORE_EMBEDDED_JMS_BROKER,
                                                                      EmbeddedJMSBrokerHolder.class);
        if (embeddedJmsBrokerHolder == null) {
            embeddedJmsBrokerHolder = createEmbeddedJmsBrokerHolderOrDefault(context,
                                                                             context.getStore(EMBEDDED_JMS_EXT).get(
                                                                                     STORE_BROKER_CONFIGURATION,
                                                                                     BrokerConfiguration.class));
            context.getStore(EMBEDDED_JMS_EXT).put(STORE_EMBEDDED_JMS_BROKER, embeddedJmsBrokerHolder);
        }
        return embeddedJmsBrokerHolder;
    }

    private static EmbeddedJMSBrokerHolder createEmbeddedJmsBrokerHolderOrDefault(final ExtensionContext extensionContext,
                                                                                  final BrokerConfiguration brokerConfiguration) {
        return createEmbeddedJmsBrokerHolder(
                extensionContext, brokerConfiguration)
                .orElseGet(() -> EmbeddedJMSBrokerHolder
                        .create(extractNameFromExtensionContext(extensionContext), false, false));
    }

    private static Optional<BrokerConfiguration> createBrokerConfigurationFromContext(final ExtensionContext extensionContext) {
        return findAnnotation(extensionContext.getElement(), BrokerConfig.class)
                .map(BrokerConfigurationBuilder::fromBrokerConfigAnnotation)
                .map(BrokerConfigurationBuilder::build);

    }

    private static Optional<EmbeddedJMSBrokerHolder> createEmbeddedJmsBrokerHolder(final ExtensionContext extensionContext,
                                                                                   final BrokerConfiguration brokerConfiguration) {
        LOGGER.debug("Constructing broker using configuration \"{}\"", brokerConfiguration);
        return Optional.ofNullable(brokerConfiguration)
                       .map(b -> createEmbeddedJMSBrokerHolderFromBrokerConfig(b,
                                                                               extractNameFromExtensionContext(
                                                                                       extensionContext)));

    }

    private static String extractNameFromExtensionContext(final ExtensionContext extensionContext) {
        return extensionContext.getTestClass().map(Class::getSimpleName).orElse(extensionContext.getUniqueId());
    }

    private static EmbeddedJMSBrokerHolder createEmbeddedJMSBrokerHolderFromBrokerConfig(final BrokerConfiguration brokerConfig,
                                                                                         String nameFromTest) {
        final boolean marshall = brokerConfig.getMarshal();
        final boolean persistence = brokerConfig.getPersistenceEnabled();
        final String name = Optional.ofNullable(brokerConfig.getName())
                                    .filter(n -> !Strings.isNullOrEmpty(n))
                                    .orElse(nameFromTest);
        LOGGER.info("Creating an embedded JMS broker using name \"{}\"", name);
        return EmbeddedJMSBrokerHolder.create(name, marshall, persistence);
    }

    private static <A extends Annotation> Optional<A> findAnnotation(Optional<? extends AnnotatedElement> element,
                                                                     Class<A> annotationType) {

        if (!element.isPresent()) {
            return Optional.empty();
        }
        return element.flatMap(e -> findAnnotationForElement(annotationType, e));
    }

    private static <A extends Annotation> Optional<A> findAnnotationForElement(final Class<A> annotationType,
                                                                               final AnnotatedElement e) {
        return AnnotationUtils.findAnnotation(e, annotationType);
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext,
                                     final ExtensionContext extensionContext) {
        return parameterContext.isAnnotated(EmbeddedJms.class);
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext,
                                   final ExtensionContext context) {
        final Parameter parameter = parameterContext.getParameter();
        final EmbeddedJMSBrokerHolder embeddedJMSBrokerHolder = context.getStore(EMBEDDED_JMS_EXT)
                                                                       .get(STORE_EMBEDDED_JMS_BROKER,
                                                                            EmbeddedJMSBrokerHolder.class);
        if (ActiveMQConnectionFactory.class.isAssignableFrom(parameter.getType())) {
            return embeddedJMSBrokerHolder.getActiveMQConnectionFactory();
        } else if (ConnectionFactory.class.isAssignableFrom(parameter.getType())) {
            return embeddedJMSBrokerHolder.getConnectionFactory();
        } else if (URI.class.isAssignableFrom(parameter.getType())) {
            return embeddedJMSBrokerHolder.getBrokerUri();
        } else {
            return null;
        }

    }

    @Override
    public void afterEach(final ExtensionContext context) {

        LOGGER.debug("afterEach \"{}\"", context.getTestClass());
        Optional.ofNullable(context.getStore(EMBEDDED_JMS_EXT)
                                   .get(STORE_EMBEDDED_JMS_BROKER, EmbeddedJMSBrokerHolder.class))
                .ifPresent(embeddedJMSBrokerHolder -> {
                    try {
                        embeddedJMSBrokerHolder.close();
                    } catch (Exception e) {
                        throw new IllegalStateException("Could not close down Embedded broker", e);
                    }
                });
    }

    @Override
    public void beforeEach(final ExtensionContext context) {
        LOGGER.debug("beforeEach \"{}\"", context.getTestClass());
        final BrokerConfiguration brokerConfigurationFromInstance = context.getStore(EMBEDDED_JMS_EXT).get(
                STORE_BROKER_CONFIGURATION,
                BrokerConfiguration.class);

        Optional.ofNullable(BrokerConfigurationBuilder.fromInstance(BrokerConfiguration.DEFAULT)
                                                      .mergeWithBrokerConfiguration(
                                                              brokerConfigurationFromInstance))
                .map(brokerConfigurationBuilder -> {
                    createBrokerConfigurationFromContext(context)
                            .ifPresent(brokerConfigurationBuilder::mergeWithBrokerConfiguration);
                    return brokerConfigurationBuilder;
                })
                .map(BrokerConfigurationBuilder::build)
                .ifPresent(c -> context.getStore(EMBEDDED_JMS_EXT).put(STORE_BROKER_CONFIGURATION, c));


        final EmbeddedJMSBrokerHolder embeddedJmsBrokerHolder = getOrCreateEmbeddedJMSBrokerHolder(context);
        embeddedJmsBrokerHolder.start();

        Object testInstance = context.getStore(EMBEDDED_JMS_EXT).get(TEST_INSTANCE);
        if (testInstance == null) {
            testInstance = context.getTestInstance().orElse(null);
        }
        Optional.ofNullable(testInstance)
                .ifPresent(ti -> FieldInjector.injectToInstance(ti, embeddedJmsBrokerHolder));
    }

    @Override
    public void postProcessTestInstance(final Object testInstance, final ExtensionContext context) {
        LOGGER.debug("postProcessTestInstance instance: \"{}\"", testInstance);
        context.getStore(EMBEDDED_JMS_EXT).put(TEST_INSTANCE, testInstance);

        createBrokerConfigurationFromContext(context)
                .ifPresent(c -> context.getStore(EMBEDDED_JMS_EXT).put(STORE_BROKER_CONFIGURATION, c));
    }
}
