package com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain;

import java.util.ArrayDeque;

public class XpathStack extends ArrayDeque<String>
{
    private static final long serialVersionUID = 1L;

    public String toXPathString()
    {
        final String tempString;
        tempString = super.toString();
        return tempString.replaceAll("\\[|,\\s", "/").replaceAll("\\]", "");
    }
}

