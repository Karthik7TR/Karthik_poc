package com.thomsonreuters.uscl.ereader;

public enum StringBool {
    TRUE("Y"),
    FALSE("N");

    private final String stringRepresentation;

    StringBool(final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    public static String toString(final boolean value) {
        return (value ? TRUE : FALSE).stringRepresentation;
    }

    public static boolean toBool(final String value) {
        return TRUE.stringRepresentation.equalsIgnoreCase(value);
    }
}
