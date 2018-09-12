package org.zapodot.junit5.jms.internal;

import java.util.Objects;

public class BrokerConfiguration {

    public static final BrokerConfiguration DEFAULT = BrokerConfigurationBuilder.instance().withMarshal(false)
                                                                                .withPersistenceEnabled(false).build();

    private final String name;

    private final Boolean marshal;

    private final Boolean persistenceEnabled;

    public BrokerConfiguration(final String name, final Boolean marshal, final Boolean persistenceEnabled) {
        this.name = name;
        this.marshal = marshal;
        this.persistenceEnabled = persistenceEnabled;
    }

    public String getName() {
        return name;
    }

    public Boolean getMarshal() {
        return marshal;
    }

    public Boolean getPersistenceEnabled() {
        return persistenceEnabled;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BrokerConfiguration that = (BrokerConfiguration) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(marshal, that.marshal) &&
                Objects.equals(persistenceEnabled, that.persistenceEnabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, marshal, persistenceEnabled);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BrokerConfiguration{");
        sb.append("name='").append(name).append('\'');
        sb.append(", marshal=").append(marshal);
        sb.append(", persistenceEnabled=").append(persistenceEnabled);
        sb.append('}');
        return sb.toString();
    }
}
