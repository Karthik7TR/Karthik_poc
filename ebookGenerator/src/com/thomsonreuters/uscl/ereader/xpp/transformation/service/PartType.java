package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import org.jetbrains.annotations.NotNull;

/**
 * Types of different parts of original XML
 */
public enum PartType {
    MAIN("main"),
    FOOTNOTE("footnotes");

    private final String name;

    PartType(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @NotNull
    public static PartType valueOfByName(@NotNull final String typeName) {
        for (final PartType type : PartType.values()) {
            if (type.getName().equals(typeName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with name " + typeName);
    }
}
