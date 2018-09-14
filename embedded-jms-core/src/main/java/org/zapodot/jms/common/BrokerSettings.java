package org.zapodot.jms.common;

import java.io.File;

/**
 * Internal broker configuration holder.
 * <p>
 * This is part of the internal API and may be changed or removed without prior notice.
 */
class BrokerSettings {
    private final String name;

    private final boolean marshal;

    private final boolean persistent;

    private final File tempDir;

    BrokerSettings(final String name, final boolean marshal, final boolean persistent, final File tempDir) {
        this.name = name;
        this.marshal = marshal;
        this.persistent = persistent;
        this.tempDir = tempDir;
    }

    String getName() {
        return name;
    }

    boolean isMarshal() {
        return marshal;
    }

    boolean isPersistent() {
        return persistent;
    }

    File getTempDir() {
        return tempDir;
    }

}
