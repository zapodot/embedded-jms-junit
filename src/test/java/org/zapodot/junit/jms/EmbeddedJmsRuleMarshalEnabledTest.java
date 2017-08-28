package org.zapodot.junit.jms;

import org.junit.Rule;
import org.junit.Test;

import javax.jms.ConnectionFactory;

import static org.junit.Assert.assertEquals;

public class EmbeddedJmsRuleMarshalEnabledTest implements SendReceivable {
    private static final String TEST_QUEUE = EmbeddedJmsRuleMarshalEnabledTest.class.getSimpleName();
    private static final String TEST_MESSAGE = "Test with wire format marshal";
    @Rule
    public EmbeddedJmsRule embeddedJmsRule = EmbeddedJmsRule.builder().withMarshalEnabled().withName("named").build();

    @Test
    public void sendAndReceiveUsingWireFormat() throws Exception {
        final ConnectionFactory connectionFactory = embeddedJmsRule.connectionFactory();
        assertEquals(TEST_MESSAGE, sendReceive(connectionFactory, TEST_QUEUE, TEST_MESSAGE));
    }
}