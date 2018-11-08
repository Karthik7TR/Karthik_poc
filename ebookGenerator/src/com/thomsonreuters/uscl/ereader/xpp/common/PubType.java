package com.thomsonreuters.uscl.ereader.xpp.common;

public enum PubType {
    COMMON("common"),
    RUTTER("rutter"),
    PRIMARY("primary");

    private final String name;

    PubType(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
