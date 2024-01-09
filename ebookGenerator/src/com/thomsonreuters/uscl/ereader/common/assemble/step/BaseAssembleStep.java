package com.thomsonreuters.uscl.ereader.common.assemble.step;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.assemble.exception.EBookAssemblyException;
import com.thomsonreuters.uscl.ereader.assemble.service.EBookAssemblyService;
import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.common.step.SplitBookTitlesAwareStep;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.springframework.batch.core.ExitStatus;

public abstract class BaseAssembleStep extends BookStepImpl implements SplitBookTitlesAwareStep {
    @Resource(name = "eBookAssemblyService")
    private EBookAssemblyService assemblyService;
    @Resource(name = "assembleFileSystem")
    private AssembleFileSystem fileSystem;

    @Override
    public ExitStatus executeStep() throws Exception {
        final BookDefinition bookDefinition = getBookDefinition();
        if (bookDefinition.isSplitBook()) {
            assembleSplitBook();
        } else {
            final File titleDirectory = fileSystem.getTitleDirectory(this);
            final File assembledBookFile = fileSystem.getAssembledBookFile(this);
            assemblyService.assembleEBook(titleDirectory, assembledBookFile);
        }
        return ExitStatus.COMPLETED;
    }

    private void assembleSplitBook() throws EBookAssemblyException {
        final List<String> splitTitles = getSplitTitles();
        for (final String splitTitleId : splitTitles) {
            final File assembledSplitTitleFile = fileSystem.getAssembledSplitTitleFile(this, splitTitleId);
            final File assembleDirectory = fileSystem.getSplitTitleDirectory(this, splitTitleId);
            assemblyService.assembleEBook(assembleDirectory, assembledSplitTitleFile);
        }
    }
}
