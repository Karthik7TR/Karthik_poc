package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

/**
 * Types of different parts of original XML
 */
public enum PartType
{
    MAIN("main"),
    FOOTNOTE("footnotes");

    private final String name;

    PartType(final String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
