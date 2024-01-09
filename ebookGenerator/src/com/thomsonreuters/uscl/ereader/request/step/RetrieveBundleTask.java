package com.thomsonreuters.uscl.ereader.request.step;

import java.io.File;
import java.text.SimpleDateFormat;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.request.XPPConstants;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.request.step.service.RetrieveBundleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;

@Slf4j
public class RetrieveBundleTask extends AbstractSbTasklet {

    public static final SimpleDateFormat DATE_DIRECTORY_FORMATER =
        new SimpleDateFormat(XPPConstants.PATTERN_BUDNLE_ARCHIVE_DATE_DIRECTORY);

    private final RetrieveBundleService retrieveBundleService;

    public RetrieveBundleTask(final RetrieveBundleService retrieveBundleService) {
        this.retrieveBundleService = retrieveBundleService;
    }

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
        throws Exception {
        log.debug("Archiving Bundle...");

        final String jobEnvironment = getJobParameters(chunkContext).getString(JobParameterKey.ENVIRONMENT_NAME);
        final XppBundleArchive request =
            (XppBundleArchive) getJobExecutionContext(chunkContext).get(JobParameterKey.KEY_XPP_BUNDLE);
        final File destDir = new File(
            String.format(
                XPPConstants.PATTERN_BUNDLE_ARCHIVE_FILE,
                jobEnvironment,
                DATE_DIRECTORY_FORMATER.format(request.getDateTime())));

        retrieveBundleService.retrieveBundle(request, destDir);
        log.debug("Bundle archived with ID " + request.getXppBundleArchiveId());
        return ExitStatus.COMPLETED;
    }
}
