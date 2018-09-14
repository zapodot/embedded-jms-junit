package org.zapodot.jms.common;

import com.google.common.io.Files;
import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.transport.TransportServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

@DisplayName("EmbeddedJMSBrokerHolder")
@ExtendWith(TemporaryDirectory.class)
@ExtendWith(MockitoExtension.class)
class EmbeddedJMSBrokerHolderConfigurerTest {

    @DisplayName("when configured")
    @Nested
    class Configure {

        @DisplayName("with persistence disabled")
        @Test
        void configureBrokerService(@Mock final BrokerService brokerService,
                                    @TemporaryDirectory.TempDir final File tempDir) throws Exception {
            final BrokerSettings brokerSettings = new BrokerSettings("name", false, false, tempDir);
            final BrokerService configuredService = EmbeddedJMSBrokerHolder.configureBrokerService(brokerService,
                                                                                                   brokerSettings);
            assertNotNull(configuredService);

            verify(brokerService, never()).setPersistenceAdapter(isA(PersistenceAdapter.class));
            verify(brokerService).setBrokerName(eq(brokerSettings.getName()));
            verify(brokerService).setUseShutdownHook(eq(false));
            verify(brokerService).setUseJmx(eq(false));
            verify(brokerService).setPersistent(eq(false));
            verify(brokerService).setStartAsync(eq(false));
            verify(brokerService).setDataDirectoryFile(eq(tempDir));
            // Need to clean up after testing by shutting down TransportServer
            final ArgumentCaptor<TransportServer> transportServerArgumentCaptor = ArgumentCaptor
                    .forClass(TransportServer.class);
            verify(brokerService).addConnector(transportServerArgumentCaptor.capture());
            transportServerArgumentCaptor.getValue().stop();
            verifyNoMoreInteractions(brokerService);
        }

        @DisplayName("with persistence enabled")
        @Test
        void configureBrokerServicePersistenceEnabled(@Mock final BrokerService brokerService,
                                                      @TemporaryDirectory.TempDir final File tempDir) throws Exception {
            final BrokerSettings brokerSettings = new BrokerSettings("name", false, true, tempDir);
            assertNotNull(EmbeddedJMSBrokerHolder.configureBrokerService(brokerService,
                                                                         brokerSettings));

            // Need to clean up after testing by shutting down PersistenceAdapter
            final ArgumentCaptor<PersistenceAdapter> persistenceAdapterArgumentCaptor = ArgumentCaptor
                    .forClass(PersistenceAdapter.class);
            verify(brokerService, times(1)).setPersistenceAdapter(persistenceAdapterArgumentCaptor.capture());
            persistenceAdapterArgumentCaptor.getValue().stop();

            // Need to clean up after testing by shutting down TransportServer
            final ArgumentCaptor<TransportServer> transportServerArgumentCaptor = ArgumentCaptor
                    .forClass(TransportServer.class);
            verify(brokerService, times(1)).addConnector(transportServerArgumentCaptor.capture());
            transportServerArgumentCaptor.getValue().stop();
            verify(brokerService).setBrokerName(eq(brokerSettings.getName()));
            verify(brokerService).setUseShutdownHook(eq(false));
            verify(brokerService).setUseJmx(eq(false));
            verify(brokerService).setPersistent(eq(brokerSettings.isPersistent()));
            verify(brokerService).setStartAsync(eq(false));
            verify(brokerService).setDataDirectoryFile(eq(tempDir));
            verifyNoMoreInteractions(brokerService);
        }

        @DisplayName("with persistence enabled but an IOException occurs")
        @Test
        void configureBrokerServicePersistentFails(@Mock final BrokerService brokerService,
                                                   @TemporaryDirectory.TempDir final File tempDir) throws Exception {
            doThrow(new IOException("I/O failure")).when(brokerService)
                                                   .setPersistenceAdapter(isA(PersistenceAdapter.class));
            final BrokerSettings brokerSettings = new BrokerSettings("name", false, true, tempDir);
            assertThrows(IllegalStateException.class,
                         () -> EmbeddedJMSBrokerHolder.configureBrokerService(brokerService,
                                                                              brokerSettings));
            // Need to clean up after testing by shutting down PersistenceAdapter
            final ArgumentCaptor<PersistenceAdapter> persistenceAdapterArgumentCaptor = ArgumentCaptor
                    .forClass(PersistenceAdapter.class);
            verify(brokerService, times(1)).setPersistenceAdapter(persistenceAdapterArgumentCaptor.capture());
            persistenceAdapterArgumentCaptor.getValue().stop();
            verify(brokerService).setBrokerName(eq(brokerSettings.getName()));
            verify(brokerService).setUseShutdownHook(eq(false));
            verify(brokerService).setUseJmx(eq(false));
            verify(brokerService).setPersistent(eq(brokerSettings.isPersistent()));
            verify(brokerService).setStartAsync(eq(false));
            verify(brokerService).setDataDirectoryFile(eq(tempDir));
            verifyNoMoreInteractions(brokerService);

        }
    }

    @DisplayName("fails to start")
    @Test
    void startFails(@Mock final BrokerService brokerService,
                    @TemporaryDirectory.TempDir final File tempDir) throws Exception {
        doThrow(new IOException("IO failure")).when(brokerService).start(eq(true));
        final EmbeddedJMSBrokerHolder embeddedJMSBrokerHolder = new EmbeddedJMSBrokerHolder(brokerService, tempDir);
        assertThrows(IllegalStateException.class, () -> embeddedJMSBrokerHolder.start());

        verify(brokerService).start(eq(true));
        // Need to clean up after testing by shutting down TransportServer
        verifyNoMoreInteractions(brokerService);
    }

    @DisplayName("starts and stops successfully")
    @Test
    void startStopSucceeds(@Mock final BrokerService brokerService,
                           @TemporaryDirectory.TempDir final File tempDir) throws Exception {
        try (final EmbeddedJMSBrokerHolder embeddedJMSBrokerHolder = new EmbeddedJMSBrokerHolder(brokerService,
                                                                                                 tempDir)) {
            assertNotNull(embeddedJMSBrokerHolder);
            embeddedJMSBrokerHolder.start();
        }
        assertFalse(tempDir.exists());
        verify(brokerService).start(eq(true));
        verify(brokerService).stop();
        verifyNoMoreInteractions(brokerService);
    }

    @DisplayName("starts and stops successfully even though the temp dir has been removed")
    @Test
    void startStopTempDirRemoved(@Mock final BrokerService brokerService) throws Exception {
        final File tempDir = Files.createTempDir();
        MoreFiles.deleteRecursively(tempDir.toPath(), RecursiveDeleteOption.ALLOW_INSECURE);
        try (final EmbeddedJMSBrokerHolder embeddedJMSBrokerHolder = new EmbeddedJMSBrokerHolder(brokerService,
                                                                                                 tempDir)) {
            assertNotNull(embeddedJMSBrokerHolder);
            embeddedJMSBrokerHolder.start();
        }
        verify(brokerService).start(eq(true));
        verify(brokerService).stop();
        verifyNoMoreInteractions(brokerService);
    }
}