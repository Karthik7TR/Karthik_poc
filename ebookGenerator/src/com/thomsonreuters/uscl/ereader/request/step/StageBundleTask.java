package com.thomsonreuters.uscl.ereader.request.step;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.jms.exception.MessageQueueException;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.request.EBookRequest;
import com.thomsonreuters.uscl.ereader.request.dao.BundleArchiveDao;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Required;

public class StageBundleTask extends AbstractSbTasklet
{
    public static final String ERROR_DUPLICATE_REQUEST = "Request already received";

    private static final Logger log = LogManager.getLogger(StageBundleTask.class);
    private BundleArchiveDao bundleArchiveDao;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext) throws Exception
    {
        // TODO Auto-generated method stub
        log.debug("Staging Bundle...");

        final EBookRequest request =
            (EBookRequest) getJobExecutionContext(chunkContext).get(JobParameterKey.KEY_EBOOK_REQUEST);
        // unpack bundle and set additional request fields
        archiveRequest(request);
        return ExitStatus.COMPLETED;
    }

    private void archiveRequest(final EBookRequest request) throws MessageQueueException
    {
        final EBookRequest dup = bundleArchiveDao.findByRequestId(request.getMessageId());
        if (dup != null)
        {
            if (dup.equals(request))
            {
                throw new MessageQueueException(ERROR_DUPLICATE_REQUEST);
            }
            else
            {
                // two non-identical requests have been received with the same ID
                // TODO identify steps for processing this scenario
                throw new MessageQueueException("non-identical duplicate request received");
            }
        }
        else
        {
            bundleArchiveDao.saveRequest(request);
        }
    }

    @Required
    public void setBundleArchiveDao(final BundleArchiveDao bundleArchiveDao)
    {
        this.bundleArchiveDao = bundleArchiveDao;
    }

    public BundleArchiveDao getBundleArchiveDao()
    {
        return bundleArchiveDao;
    }
}
