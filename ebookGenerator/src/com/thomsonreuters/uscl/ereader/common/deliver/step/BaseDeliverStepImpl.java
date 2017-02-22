package com.thomsonreuters.uscl.ereader.common.deliver.step;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.deliver.service.DeliveryCleanupService;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import org.springframework.batch.core.ExitStatus;

public abstract class BaseDeliverStepImpl extends BookStepImpl implements DeliverStep
{
    @Resource(name = "proviewHandler")
    private ProviewHandler proviewHandler;
    @Resource(name = "docMetadataService")
    private DocMetadataService docMetadataService;
    @Resource(name = "deliveryCleanupService")
    private DeliveryCleanupService cleanupService;

    private List<String> publishedSplitTiltes = new ArrayList<>();

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.common.step.BaseStep#executeStep()
     */
    @Override
    public ExitStatus executeStep() throws Exception
    {
        try
        {
            publishBook();
            return ExitStatus.COMPLETED;
        }
        catch (final ProviewException e)
        {
            cleanupService.cleanup(this);
            throw e;
        }
    }

    private void publishBook() throws ProviewException
    {
        final BookDefinition bookDefinition = getBookDefinition();
        if (bookDefinition.isSplitBook())
        {
            publishSplitBook();
        }
        else
        {
            proviewHandler
                .publishTitle(bookDefinition.getFullyQualifiedTitleId(), getBookVersion(), getAssembledBookFile());
        }
    }

    private void publishSplitBook() throws ProviewException
    {
        final List<String> splitTitles = docMetadataService.findDistinctSplitTitlesByJobId(getJobInstanceId());
        for (final String splitTitleId : splitTitles)
        {
            final File assembledSplitTitleFile = getAssembledSplitTitleFile(splitTitleId);
            proviewHandler.publishTitle(splitTitleId, getBookVersion(), assembledSplitTitleFile);
            publishedSplitTiltes.add(splitTitleId);
        }
    }

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.common.deliver.step.DeliverStep#getPublishedSplitTitles()
     */
    @Override
    public List<String> getPublishedSplitTitles()
    {
        return publishedSplitTiltes;
    }
}
