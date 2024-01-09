package com.thomsonreuters.uscl.ereader.gather.step;

import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherImgRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import org.apache.commons.io.FileUtils;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;

/**
 * Auxiliary class for generating dummy objects.
 */
public class GatherDynamicImagesTestUtil {
    public static ChunkContext getChunkContext() {
        final JobParameters jobParameters = new JobParameters();
        final ChunkContext chunkContext = new ChunkContext(
            new StepContext(
                new StepExecution("stepName", new JobExecution(new JobInstance(0L, "jobName"), jobParameters))));
        final ExecutionContext context =
            chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();

        context.put(JobExecutionKey.EBOOK_DEFINITION, new BookDefinition());

        return chunkContext;
    }

    public static GatherImgRequest captureImageRequest(final GatherService gatherService) {
        final ArgumentCaptor<GatherImgRequest> argument = ArgumentCaptor.forClass(GatherImgRequest.class);
        verify(gatherService).getImg(argument.capture());
        final GatherImgRequest request = argument.getValue();
        return request;
    }

    public static GatherResponse getGatherResponse() {
        final GatherResponse response = new GatherResponse();
        response.setImageMetadataList(Collections.singletonList(new ImgMetadataInfo()));
        return response;
    }

    public static File getManifestFile(final TemporaryFolder tempFolder) throws IOException {
        final File manifestFile = tempFolder.newFile(JobExecutionKey.IMAGE_TO_DOC_MANIFEST_FILE);
        FileUtils.writeStringToFile(manifestFile, "docId|I2943f88028b911e69ed7fcedf0a72426");
        return manifestFile;
    }
}
