package com.thomsonreuters.uscl.ereader.request.step;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.request.XPPConstants;
import com.thomsonreuters.uscl.ereader.request.XppMessageException;
import com.thomsonreuters.uscl.ereader.request.dao.XppBundleArchiveDao;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.request.service.XppMessageValidator;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Required;

public class RetrieveBundleTask extends AbstractSbTasklet {
    private static final Logger log = LogManager.getLogger(RetrieveBundleTask.class);

    public static final SimpleDateFormat DATE_DIRECTORY_FORMATER =
        new SimpleDateFormat(XPPConstants.PATTERN_BUDNLE_ARCHIVE_DATE_DIRECTORY);

    private XppMessageValidator xppMessageValidator;
    private XppBundleArchiveDao xppArchiveDao;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
        throws Exception {
        log.debug("Archiving Bundle...");

        final String jobEnvironment = getJobParameters(chunkContext).getString(JobParameterKey.ENVIRONMENT_NAME);
        final XppBundleArchive request =
            (XppBundleArchive) getJobExecutionContext(chunkContext).get(JobParameterKey.KEY_XPP_BUNDLE);

        // throws exception and exits if the request ID is already archived
        invalidateDuplicateRequest(request);

        final File ebookFile = request.getEBookSrcFile();
        final File destDir = new File(
            String.format(
                XPPConstants.PATTERN_BUNDLE_ARCHIVE_FILE,
                jobEnvironment,
                DATE_DIRECTORY_FORMATER.format(request.getDateTime())));

        if (!destDir.exists() && !destDir.mkdirs()) {
            throw new IOException("Cannot create directory: " + destDir.getAbsolutePath());
        }
        try {
            FileUtils.moveFileToDirectory(ebookFile, destDir, false);
        } catch (final IOException e) {
            if (e.getMessage().contains("Failed to delete original file")) {
                // bundle copied, but not deleted (likely permissions issue)
                // TODO handle this type of error
                log.error(e.getMessage(), e);
            } else {
                throw e;
            }
        }
        // set request file location to the archived location and revalidate the bundle integrity
        request.setEBookSrcFile(new File(destDir, ebookFile.getName()));
        try {
            xppMessageValidator.validate(request);
        } catch (final XppMessageException e) {
            log.error("Bundle invalidated during move to archive location " + request.getEBookSrcPath(), e);
            throw e;
        }
        log.debug("Bundle moved successfully: integrity verified");
        // archive request to database
        archiveRequest(request);
        log.debug("Bundle archived with ID " + request.getXppBundleArchiveId());
        return ExitStatus.COMPLETED;
    }

    private void invalidateDuplicateRequest(@NotNull final XppBundleArchive request) throws XppMessageException {
        final XppBundleArchive dup = xppArchiveDao.findByRequestId(request.getMessageId());
        if (dup != null) {
            if (dup.isSimilar(request)) {
                final String msg = XPPConstants.ERROR_DUPLICATE_REQUEST + request;
                throw new XppMessageException(msg);
            } else {
                // two non-identical requests have been received with the same ID
                // TODO identify steps for processing this scenario
                throw new XppMessageException("non-identical duplicate request received");
            }
        }
    }

    private void archiveRequest(@NotNull final XppBundleArchive request) {
        final long pk = xppArchiveDao.saveRequest(request);
        request.setXppBundleArchiveId(pk);
    }

    @Required
    public void setXppMessageValidator(final XppMessageValidator validator) {
        xppMessageValidator = validator;
    }

    @Required
    public void setXppBundleArchiveDao(final XppBundleArchiveDao xppArchiveDao) {
        this.xppArchiveDao = xppArchiveDao;
    }
}
