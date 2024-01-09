package com.thomsonreuters.uscl.ereader.xpp.common;

import javax.validation.constraints.NotNull;

/**
 * Types of streams of DIVXML
 */
public enum StreamType {
    MAIN("main"),
    FOOTNOTES("footnote");

    private @NotNull String name;

    StreamType(@NotNull final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
