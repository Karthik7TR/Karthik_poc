package com.thomsonreuters.uscl.ereader.smoketest.service;

import java.io.File;
import java.io.FilenameFilter;

public class AppFilenameFilter implements FilenameFilter
{
    @Override
    public boolean accept(final File dir, final String name)
    {
        return !name.endsWith("X");
    }
}
