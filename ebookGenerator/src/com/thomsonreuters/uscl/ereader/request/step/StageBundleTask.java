package com.thomsonreuters.uscl.ereader.request.step;

import java.io.File;
import java.io.FileInputStream;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.jaxb.JAXBParser;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.request.BundleToProcess;
import com.thomsonreuters.uscl.ereader.request.EBookBundle;
import com.thomsonreuters.uscl.ereader.request.EBookRequest;
import com.thomsonreuters.uscl.ereader.request.EBookRequestException;
import com.thomsonreuters.uscl.ereader.request.RequestConstants;
import com.thomsonreuters.uscl.ereader.request.dao.BundleToProcessDao;
import com.thomsonreuters.uscl.ereader.request.dao.EBookArchiveDao;
import com.thomsonreuters.uscl.ereader.request.service.EBookBundleValidator;
import com.thomsonreuters.uscl.ereader.request.service.GZIPService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Required;

public class StageBundleTask extends AbstractSbTasklet
{
    private static final Logger log = LogManager.getLogger(StageBundleTask.class);
    private GZIPService gzipService;
    private EBookBundleValidator bundleValidator;
    private EBookArchiveDao ebookArchiveDao;
    private BundleToProcessDao bundleToProcessDao;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext) throws Exception
    {
        log.debug("Staging Bundle...");

        // extract job parameters
        final EBookRequest request =
            (EBookRequest) getJobExecutionContext(chunkContext).get(JobParameterKey.KEY_EBOOK_REQUEST);
        final String env = getJobParameters(chunkContext).getString(JobParameterKey.ENVIRONMENT_NAME);

        final File tarball = request.getEBookSrcFile();
        final String destName = StringUtils.substringBefore(tarball.getName(), RequestConstants.FILE_TARBALL_EXTENSION);
        final String destPath = String.format(RequestConstants.PATTERN_BUDNLE_STAGED_DIRECTORY, env, destName);
        final File destDir = new File(destPath);

        // unpack bundle
        gzipService.untarzip(tarball, destDir);
        bundleValidator.validateBundleDirectory(destDir);
        final EBookBundle bundle = retrieveBundleXml(destDir);
        bundleValidator.validateBundleXml(bundle);

        // set additional request fields and archive
        request.setProductName(bundle.getProductTitle());
        request.setProductType(bundle.getProductType());
        archiveRequest(request);

        // add bundle to the BUNLE_TO_PROCESS queue
        final BundleToProcess job = new BundleToProcess(request);
        job.setSourceLocation(destDir.getAbsolutePath());
        // TODO set additional fields parsed from bundle.xml
        bundleToProcessDao.save(job);

        log.debug("request staged.");
        return ExitStatus.COMPLETED;
    }

    private @NotNull EBookBundle retrieveBundleXml(@NotNull final File request) throws EBookRequestException
    {
        final File bundleFile = new File(request, RequestConstants.FILE_BUNDLE_XML);
        try (FileInputStream inStream = new FileInputStream(bundleFile))
        {
            return JAXBParser.parse(inStream, EBookBundle.class);
        }
        catch (final Exception e)
        {
            throw new EBookRequestException("error parsing bundle", e);
        }
    }

    private void archiveRequest(@NotNull final EBookRequest request) throws EBookRequestException
    {
        final EBookRequest dup = ebookArchiveDao.findByRequestId(request.getMessageId());
        if (dup != null)
        {
            if (dup.equals(request))
            {
                final String msg = RequestConstants.ERROR_DUPLICATE_REQUEST + request;
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
            final long pk = ebookArchiveDao.saveRequest(request);
            request.setEBookArchiveId(pk);
        }
    }

    @Required
    public void setGZIPService(final GZIPService gzipService)
    {
        this.gzipService = gzipService;
    }

    @Required
    public void setBundleValidator(final EBookBundleValidator bundleValidator)
    {
        this.bundleValidator = bundleValidator;
    }

    @Required
    public void setEBookArchiveDao(final EBookArchiveDao ebookArchiveDao)
    {
        this.ebookArchiveDao = ebookArchiveDao;
    }

    @Required
    public void setBundleToProcessDao(final BundleToProcessDao bundleToProcessDao)
    {
        this.bundleToProcessDao = bundleToProcessDao;
    }
}
