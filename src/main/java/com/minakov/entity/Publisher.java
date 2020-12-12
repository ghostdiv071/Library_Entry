package com.minakov.entity;

public enum Publisher {
    Moscow("Москва"),
    Peter("Питер"),
    Reilly("O’Reilly");

    private final String value;

    Publisher(String value) {
        this.value = value;
    }

    public String getPublisher() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
