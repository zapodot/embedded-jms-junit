package org.zapodot.jms.common;

import java.io.File;

public class BrokerSettings {
    private final String name;

    private final boolean marshal;

    private final boolean persistent;

    private final File tempDir;

    public BrokerSettings(final String name, final boolean marshal, final boolean persistent, final File tempDir) {
        this.name = name;
        this.marshal = marshal;
        this.persistent = persistent;
        this.tempDir = tempDir;
    }

    public String getName() {
        return name;
    }

    public boolean isMarshal() {
        return marshal;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public File getTempDir() {
        return tempDir;
    }
}
