package com.thomsonreuters.uscl.ereader.gather.util;

public class ErrorCodeConstants {
    /*** TOC errors    ***/
    public static final String ERROR_1001 = "Failed to retrieve toc child element ";
    public static final String ERROR_1002 = "Failed to get TOC useing passed guid ";
    public static final String ERROR_1003 = "Failed to connect to Novus system ";

    /*** IO/file errors   ***/
    public static final String ERROR_2001 = "Failed to create DOM object ";
    public static final String ERROR_2002 = "Failed to find specified file path ";
    public static final String ERROR_2003 = "Failed while printing DOM to specified path  ";

    /*** Document errors ***/
    public static final String ERROR_3001 = "Failed to retrieve Document ";
}
