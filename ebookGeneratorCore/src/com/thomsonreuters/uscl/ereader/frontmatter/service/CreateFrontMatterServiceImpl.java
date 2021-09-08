package com.thomsonreuters.uscl.ereader.frontmatter.service;

import com.thomsonreuters.uscl.ereader.FrontMatterFileName;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.frontmatter.exception.EBookFrontMatterGenerationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CreateFrontMatterServiceImpl implements CreateFrontMatterService {
    private static final String HTML_EXTENSION = ".html";
    private final BaseFrontMatterService baseFrontMatterService;
    private final PdfImagesService pdfImagesService;

    public void generateAllFrontMatterPages(final File outputDir, final CombinedBookDefinition combinedBookDefinition, final boolean withPageNumbers)
            throws EBookFrontMatterGenerationException {
        final BookDefinition primaryBookDefinition = combinedBookDefinition.getPrimaryTitle().getBookDefinition();
        final List<BookDefinition> orderedBookDefinitions = combinedBookDefinition.getOrderedBookDefinitionList();
        final Map<String, List<String>> frontMatterPdfImageNames = processFrontMatterPdfImages(outputDir, primaryBookDefinition.isCwBook(), orderedBookDefinitions);
        for (BookDefinition bookDefinition : orderedBookDefinitions) {
            generateTitlePage(outputDir, withPageNumbers, bookDefinition, combinedBookDefinition.isFromBookDefinition());
            for (final FrontMatterPage page : bookDefinition.getFrontMatterPages()) {
                generateAdditionalFrontMatterPage(outputDir, frontMatterPdfImageNames, bookDefinition, page);
            }
        }
        generateCopyrightPage(outputDir, withPageNumbers, primaryBookDefinition);
        if (!primaryBookDefinition.isCwBook()) {
            generateResearchAssistancePage(outputDir, withPageNumbers, primaryBookDefinition);
            generateWestlawPage(outputDir, withPageNumbers);
        }
    }

    private void generateTitlePage(final File outputDir, final boolean withPageNumbers, final BookDefinition bookDefinition, final boolean isSingleTitle) throws EBookFrontMatterGenerationException {
        String titlePageName = getTitlePageName(bookDefinition.getFullyQualifiedTitleId(), isSingleTitle);
        final File titlePage = new File(outputDir, titlePageName);
        writeHTMLFile(titlePage, baseFrontMatterService.generateTitlePage(bookDefinition, withPageNumbers));
        log.debug("Front Matter Title HTML page generated.");
    }

    private void generateAdditionalFrontMatterPage(final File outputDir, final Map<String, List<String>> frontMatterPdfImageNames, final BookDefinition bookDefinition, final FrontMatterPage page) throws EBookFrontMatterGenerationException {
        final File additionalPage =
                new File(outputDir, FrontMatterFileName.ADDITIONAL_FRONT_MATTER + page.getId() + HTML_EXTENSION);
        writeHTMLFile(additionalPage, baseFrontMatterService.generateAdditionalFrontMatterPage(bookDefinition, page.getId(), frontMatterPdfImageNames));
        log.debug("Front Matter Additional HTML page " + page.getId() + " generated.");
    }

    private void generateCopyrightPage(final File outputDir, final boolean withPageNumbers, final BookDefinition primaryBookDefinition) throws EBookFrontMatterGenerationException {
        final File copyrightPage = new File(outputDir, FrontMatterFileName.COPYRIGHT + HTML_EXTENSION);
        writeHTMLFile(copyrightPage, baseFrontMatterService.generateCopyrightPage(primaryBookDefinition, withPageNumbers));
        log.debug("Front Matter Copyright HTML page generated.");
    }

    private void generateWestlawPage(final File outputDir, final boolean withPageNumbers) throws EBookFrontMatterGenerationException {
        final File westlawNextPage = new File(outputDir, FrontMatterFileName.WESTLAW + HTML_EXTENSION);
        writeHTMLFile(westlawNextPage, baseFrontMatterService.generateWestlawNextPage(withPageNumbers));
        log.debug("Front Matter WestlawNext HTML page generated.");
    }

    private void generateResearchAssistancePage(final File outputDir, final boolean withPageNumbers, final BookDefinition primaryBookDefinition) throws EBookFrontMatterGenerationException {
        final File researchAssistancePage = new File(outputDir, FrontMatterFileName.RESEARCH_ASSISTANCE + HTML_EXTENSION);
        writeHTMLFile(researchAssistancePage, baseFrontMatterService.generateResearchAssistancePage(primaryBookDefinition, withPageNumbers));
        log.debug("Front Matter Research Assistance HTML page generated.");
    }


    private Map<String, List<String>> processFrontMatterPdfImages(final File outputDir, final boolean isCwBook, final List<BookDefinition> orderedBookDefinitions) {
        Map<String, List<String>> frontMatterPdfImageNames;
        if (isCwBook) {
            frontMatterPdfImageNames = orderedBookDefinitions.stream()
                    .map(BookDefinition::getFrontMatterPdfFileNames)
                    .flatMap(Collection::stream)
                    .collect(Collectors.collectingAndThen(Collectors.toSet(), pdfNames -> pdfImagesService.generatePdfImages(pdfNames, outputDir)));
        } else {
            frontMatterPdfImageNames = Collections.emptyMap();
        }
        return frontMatterPdfImageNames;
    }

    protected void writeHTMLFile(final File aFile, final String text) throws EBookFrontMatterGenerationException {
        try {
            FileUtils.write(aFile, text, StandardCharsets.UTF_8.name());
        } catch (final IOException e) {
            final String errMessage = "Failed to write the following file to NAS: " + aFile.getAbsolutePath();
            log.error(errMessage);
            throw new EBookFrontMatterGenerationException(errMessage, e);
        }
    }

    @NotNull
    private String getTitlePageName(final String titleId, final boolean isSingleTitle) {
        String titlePageName = FrontMatterFileName.FRONT_MATTER_TITLE;
        if (!isSingleTitle) {
            titlePageName = titlePageName + "-" + new TitleId(titleId).escapeSlashWithDash();
        }
        titlePageName = titlePageName + HTML_EXTENSION;
        return titlePageName;
    }
}
