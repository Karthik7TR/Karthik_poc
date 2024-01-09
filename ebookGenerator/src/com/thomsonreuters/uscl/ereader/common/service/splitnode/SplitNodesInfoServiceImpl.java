package com.thomsonreuters.uscl.ereader.common.service.splitnode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class SplitNodesInfoServiceImpl implements SplitNodesInfoService {
    @Override
    public List<String> getTitleIds(final File splitNodeInfoFile, final String fullyQualifiedTitleId) {
        final List<String> splitTitles = new ArrayList<>();
        splitTitles.add(fullyQualifiedTitleId);

        final List<String> lines = getLines(splitNodeInfoFile);
        for (final String line : lines) {
            final String[] splitted = line.split("\\|");
            splitTitles.add(splitted[1]);
        }
        return splitTitles;
    }

    @Override
    public Set<SplitNodeInfo> getSubmittedSplitNodes(
        final File splitNodeInfoFile,
        final BookDefinition bookDefinition,
        final Version submittedVersion) {
        final Set<SplitNodeInfo> set = new HashSet<>();
        final List<String> lines = getLines(splitNodeInfoFile);
        for (final String line : lines) {
            final String[] splitted = line.split("\\|");
            final SplitNodeInfo splitNodeInfo = new SplitNodeInfo();
            splitNodeInfo.setBookDefinition(bookDefinition);
            splitNodeInfo.setBookVersionSubmitted(submittedVersion.getVersionWithoutPrefix());
            String guid = splitted[0];
            if (guid.length() > 33) {
                guid = StringUtils.substring(guid, 0, 33);
            }
            splitNodeInfo.setSplitNodeGuid(guid);
            splitNodeInfo.setSpitBookTitle(splitted[1]);
            set.add(splitNodeInfo);
        }
        return set;
    }

    private List<String> getLines(final File splitNodeInfoFile) {
        try {
            return FileUtils.readLines(splitNodeInfoFile);
        } catch (final IOException iox) {
            throw new RuntimeException("Unable to find File : " + splitNodeInfoFile.getAbsolutePath(), iox);
        }
    }
}
