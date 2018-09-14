package org.zapodot.junit5.jms.internal;

import com.google.common.base.Strings;
import org.zapodot.junit5.jms.annotations.BrokerConfig;

import java.util.Optional;

/**
 * BrokerConfigurationBuilder - part of the internal API. May be removed, moved or changed without prior deprecation
 */
public final class BrokerConfigurationBuilder {
    private String name;

    private Boolean marshal;

    private Boolean persistenceEnabled;

    private BrokerConfigurationBuilder() {
    }

    public static BrokerConfigurationBuilder instance() {
        return new BrokerConfigurationBuilder();
    }

    public static BrokerConfigurationBuilder fromInstance(final BrokerConfiguration brokerConfiguration) {
        return Optional.ofNullable(brokerConfiguration)
                       .map(b -> instance()
                               .withName(b.getName())
                               .withPersistenceEnabled(b.getPersistenceEnabled())
                               .withMarshal(b.getMarshal()))
                       .orElse(BrokerConfigurationBuilder.instance());
    }

    public static BrokerConfigurationBuilder fromBrokerConfigAnnotation(BrokerConfig brokerConfig) {
        return instance()
                .withName(Strings.emptyToNull(brokerConfig.name()))
                .withMarshal(convertFromString(brokerConfig.marshall()))
                .withPersistenceEnabled(convertFromString(brokerConfig.persistence()));
    }

    public BrokerConfigurationBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public BrokerConfigurationBuilder withMarshal(Boolean marshal) {
        this.marshal = marshal;
        return this;
    }

    public BrokerConfigurationBuilder withPersistenceEnabled(Boolean persistenceEnabled) {
        this.persistenceEnabled = persistenceEnabled;
        return this;
    }

    public BrokerConfigurationBuilder mergeWithBrokerConfiguration(final BrokerConfiguration brokerConfiguration) {
        Optional.ofNullable(brokerConfiguration)
                .ifPresent(b -> {
                    withName(b.getName());
                    withMarshal(b.getMarshal());
                    withPersistenceEnabled(b.getPersistenceEnabled());
                });
        return this;
    }

    private static Boolean convertFromString(final String value) {
        return Optional.ofNullable(Strings.emptyToNull(value)).map(Boolean::valueOf).orElse(null);
    }

    public BrokerConfiguration build() {
        return new BrokerConfiguration(name, marshal, persistenceEnabled);
    }

}
