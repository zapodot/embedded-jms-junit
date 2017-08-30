package org.zapodot.junit.jms.impl;

import org.junit.Test;

/**
 * @author sondre
 */
public class EmbeddedJmsRuleImplTest {

    @Test(expected = IllegalStateException.class)
    public void connectionFactory() throws Exception {
        final EmbeddedJmsRuleImpl rule = new EmbeddedJmsRuleImpl("predefined", true, false);
        rule.connectionFactory();
    }

    @Test(expected = IllegalStateException.class)
    public void activeMqConnectionFactory() throws Exception {
        final EmbeddedJmsRuleImpl rule = new EmbeddedJmsRuleImpl("predefined", true, false);
        rule.activeMqConnectionFactory();
    }

    @Test(expected = IllegalStateException.class)
    public void brokerUri() throws Exception {
        final EmbeddedJmsRuleImpl rule = new EmbeddedJmsRuleImpl("predefined", true, false);
        rule.brokerUri();
    }

}