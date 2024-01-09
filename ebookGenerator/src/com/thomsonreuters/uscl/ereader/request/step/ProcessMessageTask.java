package com.thomsonreuters.uscl.ereader.request.step;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBException;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.jaxb.JAXBParser;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.request.XppMessageException;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.request.service.XppMessageValidator;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

@Slf4j
public class ProcessMessageTask extends AbstractSbTasklet {
    private XppMessageValidator xppMessageValidator;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
        throws Exception {
        log.debug("Retrieving request...");
        final JobParameters jobParameters = getJobParameters(chunkContext);

        XppBundleArchive bundle = null;
        final String request = jobParameters.getString(JobParameterKey.KEY_REQUEST_XML);

        try {
            //log.debug(request); // log raw request
            // resolve request object
            bundle = unmarshalRequest(request);
            // validate message content
            xppMessageValidator.validate(bundle);
        } catch (final JAXBException e) {
            // TODO: handle unmarshalling exception
            log.error("XPP Message cannot be parsed: " + request, e);
            return ExitStatus.FAILED;
        } catch (final XppMessageException e) {
            log.error("XPP message is invalid: ", e);
            // cannot process
            return ExitStatus.FAILED;
        } catch (final Exception e) {
            log.error("Exception encountered: ", e);
            throw e;
        }

        log.debug("Bundle message validated " + bundle);
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
        jobExecutionContext.put(JobParameterKey.KEY_XPP_BUNDLE, bundle);
        return ExitStatus.COMPLETED;
    }

    /**
     * package-private for testing purposes
     */
    XppBundleArchive unmarshalRequest(@NotNull final String request) throws JAXBException {
        final XppBundleArchive xppBundleArchive =
            JAXBParser.parse(new ByteArrayInputStream(request.getBytes()), XppBundleArchive.class);
        xppBundleArchive.setMessageRequest(request);
        return xppBundleArchive;
    }

    @Required
    public void setXppMessageValidator(final XppMessageValidator xppMessageValidator) {
        this.xppMessageValidator = xppMessageValidator;
    }
}
