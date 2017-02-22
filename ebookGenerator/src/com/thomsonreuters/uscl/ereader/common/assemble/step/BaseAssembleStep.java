package com.thomsonreuters.uscl.ereader.common.assemble.step;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.assemble.exception.EBookAssemblyException;
import com.thomsonreuters.uscl.ereader.assemble.service.EBookAssemblyService;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import org.springframework.batch.core.ExitStatus;

public abstract class BaseAssembleStep extends BookStepImpl
{
    @Resource(name = "eBookAssemblyService")
    private EBookAssemblyService assemblyService;
    @Resource(name = "docMetadataService")
    private DocMetadataService docMetadataService;

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.common.step.BaseStep#executeStep()
     */
    @Override
    public ExitStatus executeStep() throws Exception
    {
        final BookDefinition bookDefinition = getBookDefinition();
        if (bookDefinition.isSplitBook())
        {
            assembleSplitBook();
        }
        else
        {
            assemblyService.assembleEBook(getAssembleTitleDirectory(), getAssembledBookFile());
        }
        return ExitStatus.COMPLETED;
    }

    private void assembleSplitBook() throws EBookAssemblyException
    {
        final List<String> splitTitles = docMetadataService.findDistinctSplitTitlesByJobId(getJobInstanceId());
        for (final String splitTitleId : splitTitles)
        {
            final File assembledSplitTitleFile = getAssembledSplitTitleFile(splitTitleId);
            final File assembleDirectory = getAssembleSplitTitleDirectory(splitTitleId);
            assemblyService.assembleEBook(assembleDirectory, assembledSplitTitleFile);
        }
    }
}
