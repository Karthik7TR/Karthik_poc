package com.thomsonreuters.uscl.ereader.common.group.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class SplitNodesInfoServiceImpl implements SplitNodesInfoService
{
    @Override
    public List<String> readSplitNodeInforFile(final File splitNodeInfoFile, final String fullyQualifiedTitleId)
    {
        final List<String> splitTitles = new ArrayList<>();
        splitTitles.add(fullyQualifiedTitleId);

        try
        {
            final List<String> lines = FileUtils.readLines(splitNodeInfoFile);
            for (final String line : lines)
            {
                final String[] splitted = line.split("\\|");
                splitTitles.add(splitted[1]);
            }
        }
        catch (final IOException iox)
        {
            throw new RuntimeException("Unable to find File : " + splitNodeInfoFile.getAbsolutePath(), iox);
        }
        return splitTitles;
    }
}
