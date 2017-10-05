package com.thomsonreuters.uscl.ereader.common.archive.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.archive.step.BaseArchiveStep;
import com.thomsonreuters.uscl.ereader.common.filesystem.ArchiveFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.FileSystemException;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import org.apache.commons.io.FileUtils;

public class ArchiveServiceImpl implements ArchiveService {
    @Resource(name = "docMetadataService")
    private DocMetadataService docMetadataService;
    @Resource(name = "archiveFileSystem")
    private ArchiveFileSystem archivefileSystem;
    @Resource(name = "assembleFileSystem")
    private AssembleFileSystem assembleFileSystem;

    @Override
    public void archiveBook(final BaseArchiveStep step) {
        final File archiveDirectory = createArchiveDirectory(step);

        final BookDefinition bookDefinition = step.getBookDefinition();
        if (bookDefinition.isSplitBook()) {
            archiveSplitBook(step, archiveDirectory);
        } else {
            archiveBook(assembleFileSystem.getAssembledBookFile(step), archiveDirectory);
        }
    }

    private void archiveSplitBook(final BaseArchiveStep step, final File archiveDirectory) {
        final List<String> splitTitles = docMetadataService.findDistinctSplitTitlesByJobId(step.getJobInstanceId());
        for (final String splitTitleId : splitTitles) {
            final File assembledSplitTitleFile = assembleFileSystem.getAssembledSplitTitleFile(step, splitTitleId);
            archiveBook(assembledSplitTitleFile, archiveDirectory);
        }
    }

    private void archiveBook(final File assembledBookFile, final File archiveDirectory) {
        try {
            FileUtils.copyFileToDirectory(assembledBookFile, archiveDirectory);
        } catch (final IOException e) {
            final String message = String.format(
                "Cannot copy file %s to directory %s",
                assembledBookFile.getAbsolutePath(),
                archiveDirectory.getAbsolutePath());
            throw new FileSystemException(message, e);
        }
    }

    private File createArchiveDirectory(final BaseArchiveStep step) {
        final File archiveDirectory = archivefileSystem.getArchiveVersionDirectory(step);
        if (!archiveDirectory.exists() && !archiveDirectory.mkdirs()) {
            throw new FileSystemException(
                String.format("Archive directory %s was not created", archiveDirectory.getAbsolutePath()));
        }
        return archiveDirectory;
    }
}
