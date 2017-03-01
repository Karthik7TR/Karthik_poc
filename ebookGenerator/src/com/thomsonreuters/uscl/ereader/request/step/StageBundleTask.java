package com.thomsonreuters.uscl.ereader.request.step;

import java.io.File;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.request.EBookRequest;
import com.thomsonreuters.uscl.ereader.request.EBookRequestException;
import com.thomsonreuters.uscl.ereader.request.dao.BundleArchiveDao;
import com.thomsonreuters.uscl.ereader.request.service.GZIPService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Required;

public class StageBundleTask extends AbstractSbTasklet
{
    public static final String ERROR_DUPLICATE_REQUEST = "Request already received.\n\t id: %s";
    public static final String EBOOK_BUILDER_BASE_DIR = "/apps/eBookBuilder/";
    public static final String STAGED_BUNDLE_FILE_PATTERN = "/apps/eBookBuilder/%s/xpp/jobs/%s";

    private static final Logger log = LogManager.getLogger(StageBundleTask.class);
    private GZIPService gzipService;
    private BundleArchiveDao bundleArchiveDao;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext) throws Exception
    {
        log.debug("Staging Bundle...");

        final EBookRequest request =
            (EBookRequest) getJobExecutionContext(chunkContext).get(JobParameterKey.KEY_EBOOK_REQUEST);
        // unpack bundle
        final File tarball = request.getEBookSrcFile();
        final String env = getJobParameters(chunkContext).getString(JobParameterKey.ENVIRONMENT_NAME);
        final String destName = StringUtils.substringBefore(tarball.getName(), ".tar.gz");
        final File destDir = new File(String.format(STAGED_BUNDLE_FILE_PATTERN, env, destName));
        gzipService.untarzip(tarball, destDir);
        // set additional request fields

        archiveRequest(request);
        log.debug("request staged.");
        return ExitStatus.COMPLETED;
    }

    private void archiveRequest(final EBookRequest request) throws EBookRequestException
    {
        final EBookRequest dup = bundleArchiveDao.findByRequestId(request.getMessageId());
        if (dup != null)
        {
            if (dup.equals(request))
            {
                final String msg = String.format(ERROR_DUPLICATE_REQUEST, request.getMessageId());
                throw new EBookRequestException(msg);
            }
            else
            {
                // two non-identical requests have been received with the same ID
                // TODO identify steps for processing this scenario
                throw new EBookRequestException("non-identical duplicate request received");
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

    @Required
    public void setGZIPService(final GZIPService gzipService)
    {
        this.gzipService = gzipService;
    }
}
