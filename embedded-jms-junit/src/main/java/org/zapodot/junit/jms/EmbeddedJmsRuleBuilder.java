package org.zapodot.junit.jms;

import org.zapodot.junit.jms.impl.EmbeddedJmsRuleImpl;

/**
 * Builder that allows the setup of the Embedded JMS broker to be tweaked to individual needs
 */
public class EmbeddedJmsRuleBuilder {
    private String predefinedName;
    private boolean marshal = false;
    private boolean persistent = false;

    /**
     * Explicitly sets the name of the broker. Convenient if more then one broker is configured per test
     *
     * @param name the name to use for the broker
     * @return the same {@link EmbeddedJmsRuleBuilder} with the name property set
     */
    public EmbeddedJmsRuleBuilder withName(final String name) {
        this.predefinedName = name;
        return this;
    }

    /**
     * Enables marshaling, meaning that all commands are sent and received using a {@link org.apache.activemq.wireformat.WireFormat}
     *
     * @return the same {@link EmbeddedJmsRuleBuilder} with the marshal property set to true
     */
    public EmbeddedJmsRuleBuilder withMarshalEnabled() {
        this.marshal = true;
        return this;
    }

    public EmbeddedJmsRuleImpl build() {
        return new EmbeddedJmsRuleImpl(predefinedName, marshal, persistent);
    }
}