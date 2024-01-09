package com.thomsonreuters.uscl.ereader.util;

public class ValueConverter {

    private static final String Y = "Y";
    private static final String N = "N";

    public static String getStringForBooleanValue(boolean value) {
        return value ? Y : N;
    }

    public static boolean isEqualsYes(String value) {
        return Y.equalsIgnoreCase(value);
    }
}
