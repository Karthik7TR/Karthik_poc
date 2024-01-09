package com.thomsonreuters.uscl.ereader.mgr.web;

import java.util.Comparator;

public class CaseSensetiveStringComporator implements Comparator<String> {
    @Override
    public int compare(final String firstStr, final String secondStr) {
        return firstStr.compareTo(secondStr);
    }
}
