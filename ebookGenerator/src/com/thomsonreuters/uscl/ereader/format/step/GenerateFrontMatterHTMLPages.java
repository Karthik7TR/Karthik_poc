package com.thomsonreuters.uscl.ereader.format.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.EBOOK_FRONT_MATTER_PDF_IMAGES_FILEPATH;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_FRONT_MATTER_PDF_IMAGES_DIR;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.service.PdfToImgConverter;
import com.thomsonreuters.uscl.ereader.frontmatter.service.CreateFrontMatterService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This step adds a static predefined HTML header and footer and any ProView specific document wrappers.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */

@Slf4j
public class GenerateFrontMatterHTMLPages extends AbstractSbTasklet {
    @Autowired
    private CreateFrontMatterService frontMatterService;
    @Autowired
    private PdfToImgConverter pdfToImgConverter;

    private PublishingStatsService publishingStatsService;

    @Autowired
    private PagesAnalyzeService pagesAnalyzeService;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
        throws Exception {
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);

        final String frontMatterTargetDirectory =
            getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_FRONT_MATTER_HTML_DIR);

        final File frontMatterTargetDir = new File(frontMatterTargetDirectory);

        final BookDefinition bookDefinition =
            (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);

        final Long jobInstance =
            chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();

        Map<String, List<String>> frontMatterPdfImageNames = generatePdfImages(bookDefinition, frontMatterTargetDir);

        String publishStatus = "generateFrontMatterHTML : Completed";

        final boolean withPageNumbers = checkForPagebreaks(jobExecutionContext, bookDefinition);

        try {
            frontMatterService.generateAllFrontMatterPages(frontMatterTargetDir, bookDefinition, withPageNumbers, frontMatterPdfImageNames);
        } catch (final Exception e) {
            publishStatus = "generateFrontMatterHTML : Failed";
            throw e;
        } finally {
            final PublishingStats jobstats = new PublishingStats();
            jobstats.setJobInstanceId(jobInstance);
            jobstats.setPublishStatus(publishStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
        }

        return ExitStatus.COMPLETED;
    }

    private Map<String, List<String>> generatePdfImages(final BookDefinition bookDefinition, final File frontMatterTargetDir) {
        if (bookDefinition.isCwBook()) {
            File pdfDestDir = new File(frontMatterTargetDir, FORMAT_FRONT_MATTER_PDF_IMAGES_DIR.getName());
            return bookDefinition.getFrontMatterPdfFileNames().stream()
                    .collect(Collectors.toMap(Function.identity(), pdfFileName -> {
                        File pdfFile = new File(EBOOK_FRONT_MATTER_PDF_IMAGES_FILEPATH.getName(), pdfFileName);
                        return pdfToImgConverter.convert(pdfFile, pdfDestDir);
                    }));
        }
        return Collections.emptyMap();
    }

    private boolean checkForPagebreaks(
        final ExecutionContext jobExecutionContext,
        final BookDefinition bookDefinition) {
        final File docsDir = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_DIR));
        final boolean withPageNumbers = bookDefinition.isPrintPageNumbers() && pagesAnalyzeService.checkIfDocumentsContainPagebreaks(docsDir);
        jobExecutionContext.put(JobExecutionKey.WITH_PAGE_NUMBERS, withPageNumbers);
        return withPageNumbers;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService) {
        this.publishingStatsService = publishingStatsService;
    }
}
