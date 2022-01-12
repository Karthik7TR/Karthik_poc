package com.thomsonreuters.uscl.ereader.request.step;

import java.io.File;
import java.io.FileInputStream;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.service.compress.GZIPService;
import com.thomsonreuters.uscl.ereader.jaxb.JAXBParser;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.request.XPPConstants;
import com.thomsonreuters.uscl.ereader.request.XppMessageException;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.request.service.XppBundleValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Required;

@Slf4j
public class StageBundleTask extends AbstractSbTasklet {
    private GZIPService gzipService;
    private XppBundleValidator bundleValidator;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
        throws Exception {
        log.debug("Staging Bundle...");

        // extract job parameters
        final XppBundleArchive request =
            (XppBundleArchive) getJobExecutionContext(chunkContext).get(JobParameterKey.KEY_XPP_BUNDLE);
        final String env = getJobParameters(chunkContext).getString(JobParameterKey.ENVIRONMENT_NAME);

        final File tarball = request.getEBookSrcFile();
        final String destName = StringUtils.substringBefore(tarball.getName(), XPPConstants.FILE_TARBALL_EXTENSION);
        final String destPath = String.format(XPPConstants.PATTERN_BUDNLE_STAGED_DIRECTORY, env, destName);
        final File destDir = new File(destPath);

        // unpack bundle
        gzipService.decompress(tarball, destDir);
        bundleValidator.validateBundleDirectory(destDir);
        final XppBundle bundle = retrieveBundleXml(destDir);
        bundleValidator.validateBundleXml(bundle);

        log.debug("request staged.");
        return ExitStatus.COMPLETED;
    }

    private @NotNull XppBundle retrieveBundleXml(@NotNull final File request) throws XppMessageException {
        final File bundleFile = new File(request, XPPConstants.FILE_BUNDLE_XML);
        try (FileInputStream inStream = new FileInputStream(bundleFile)) {
            return JAXBParser.parse(inStream, XppBundle.class);
        } catch (final Exception e) {
            throw new XppMessageException("error parsing bundle", e);
        }
    }

    @Required
    public void setGZIPService(final GZIPService gzipService) {
        this.gzipService = gzipService;
    }

    @Required
    public void setBundleValidator(final XppBundleValidator bundleValidator) {
        this.bundleValidator = bundleValidator;
    }
}
