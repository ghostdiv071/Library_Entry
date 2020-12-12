package com.minakov;

public enum ConnectionUtils {
    URL("jdbc:hsqldb:file:/database/Library; hsqldb.lock_file=false"),
    USER("SA"),
    PASSWORD("");

    public final String value;

    ConnectionUtils(String value) {
        this.value = value;
    }
}
