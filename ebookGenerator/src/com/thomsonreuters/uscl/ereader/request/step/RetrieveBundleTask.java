package com.thomsonreuters.uscl.ereader.request.step;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.request.EBookRequest;
import com.thomsonreuters.uscl.ereader.request.service.EBookRequestValidator;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Required;

public class RetrieveBundleTask extends AbstractSbTasklet
{
    public static final String ARCHIVE_FILE_PATTERN = "/apps/eBookBuilder/%s/xpp/archive/%s";
    public static final SimpleDateFormat DATE_DIRECTORY_FORMATER = new SimpleDateFormat("YYYY/MM");

    private EBookRequestValidator eBookRequestValidator;
    private static final Logger log = LogManager.getLogger(RetrieveBundleTask.class);

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext) throws Exception
    {
        log.debug("Staging Bundle...");

        final EBookRequest request =
            (EBookRequest) getJobExecutionContext(chunkContext).get(JobParameterKey.KEY_EBOOK_REQUEST);
        final String jobEnvironment = getJobParameters(chunkContext).getString(JobParameterKey.ENVIRONMENT_NAME);

        final File ebookFile = request.getEBookSrcFile();
        final File destDir = new File(
            String.format(ARCHIVE_FILE_PATTERN, jobEnvironment, DATE_DIRECTORY_FORMATER.format(request.getDateTime())));

        if (!destDir.exists() && !destDir.mkdirs())
        {
            throw new IOException("Cannot create directory: " + destDir.getAbsolutePath());
        }
        try
        {
            FileUtils.moveFileToDirectory(ebookFile, destDir, false);
        }
        catch (final IOException e)
        {
            if (e.getMessage().contains("Failed to delete original file"))
            {
                // bundle moved, but not deleted (likely permissions issue)
                // TODO handle this type of error
                log.error(e.getMessage());
            }
            else
            {
                throw e;
            }
        }
        // set request file location to the archived location and revalidate the bundle
        request.setEBookSrcFile(new File(destDir, ebookFile.getName()));
        eBookRequestValidator.validate(request);
        log.debug("Bundle moved successfully: integrity verified");
        return ExitStatus.COMPLETED;
    }

    @Required
    public void setEBookRequestValidator(final EBookRequestValidator validator)
    {
        eBookRequestValidator = validator;
    }

    public EBookRequestValidator getEBookRequestValidator()
    {
        return eBookRequestValidator;
    }
}
