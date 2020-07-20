package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.NasFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.service.PdfToImgConverter;
import com.thomsonreuters.uscl.ereader.frontmatter.service.CreateFrontMatterService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_FRONT_MATTER_PDF_IMAGES_DIR;

/**
 * This step adds a static predefined HTML header and footer and any ProView specific document wrappers.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */

@Slf4j
@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class GenerateFrontMatterHTMLPages extends BookStepImpl {
    @Autowired
    private CreateFrontMatterService frontMatterService;
    @Autowired
    private PdfToImgConverter pdfToImgConverter;
    @Autowired
    private PublishingStatsService publishingStatsService;
    @Autowired
    private PagesAnalyzeService pagesAnalyzeService;
    @Autowired
    private FormatFileSystem formatFileSystem;
    @Autowired
    private NasFileSystem nasFileSystem;

    @Override
    public ExitStatus executeStep() throws Exception {
        final ExecutionContext jobExecutionContext = getJobExecutionContext();
        final File frontMatterTargetDir = formatFileSystem.getFrontMatterHtmlDir(this);
        final BookDefinition bookDefinition = getBookDefinition();
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
            jobstats.setJobInstanceId(getJobInstanceId());
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
                        File pdfFile = new File(nasFileSystem.getFrontMatterCwPdfDirectory(), pdfFileName);
                        if (!pdfFile.exists()) {
                            pdfFile = new File(nasFileSystem.getFrontMatterUsclPdfDirectory(), pdfFileName);
                        }
                        return pdfToImgConverter.convert(pdfFile, pdfDestDir);
                    }));
        }
        return Collections.emptyMap();
    }

    private boolean checkForPagebreaks(
        final ExecutionContext jobExecutionContext,
        final BookDefinition bookDefinition) {
        final File docsDir = new File(getJobExecutionPropertyString(JobExecutionKey.GATHER_DOCS_DIR));
        final boolean withPageNumbers = bookDefinition.isPrintPageNumbers() && pagesAnalyzeService.checkIfDocumentsContainPagebreaks(docsDir);
        jobExecutionContext.put(JobExecutionKey.WITH_PAGE_NUMBERS, withPageNumbers);
        return withPageNumbers;
    }
}
