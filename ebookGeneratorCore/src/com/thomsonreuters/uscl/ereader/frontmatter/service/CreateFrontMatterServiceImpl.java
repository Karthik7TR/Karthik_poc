package com.thomsonreuters.uscl.ereader.frontmatter.service;

import com.thomsonreuters.uscl.ereader.FrontMatterFileName;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.frontmatter.exception.EBookFrontMatterGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CreateFrontMatterServiceImpl implements CreateFrontMatterService {
    private static final String HTML_EXTENSION = ".html";
    private final BaseFrontMatterService baseFrontMatterService;

    @Autowired
    public CreateFrontMatterServiceImpl(BaseFrontMatterService baseFrontMatterService) {
        this.baseFrontMatterService = baseFrontMatterService;
    }

    @Override
    public void generateAllFrontMatterPages(final File outputDir, final BookDefinition bookDefinition, final boolean withPageNumbers, final Map<String, List<String>> frontMatterPdfImageNames)
        throws EBookFrontMatterGenerationException {
        final File titlePage = new File(outputDir, FrontMatterFileName.FRONT_MATTER_TITLE + HTML_EXTENSION);
        writeHTMLFile(titlePage, baseFrontMatterService.generateTitlePage(bookDefinition, withPageNumbers));

        log.debug("Front Matter Title HTML page generated.");

        final File copyrightPage = new File(outputDir, FrontMatterFileName.COPYRIGHT + HTML_EXTENSION);
        writeHTMLFile(copyrightPage, baseFrontMatterService.generateCopyrightPage(bookDefinition, withPageNumbers));

        log.debug("Front Matter Copyright HTML page generated.");

        for (final FrontMatterPage page : bookDefinition.getFrontMatterPages()) {
            final File additionalPage =
                new File(outputDir, FrontMatterFileName.ADDITIONAL_FRONT_MATTER + page.getId() + HTML_EXTENSION);
            writeHTMLFile(additionalPage, baseFrontMatterService.generateAdditionalFrontMatterPage(bookDefinition, page.getId(), frontMatterPdfImageNames));

            log.debug("Front Matter Additional HTML page " + page.getId() + " generated.");
        }

        if (!bookDefinition.isCwBook()) {
            final File researchAssistancePage =
                    new File(outputDir, FrontMatterFileName.RESEARCH_ASSISTANCE + HTML_EXTENSION);
            writeHTMLFile(researchAssistancePage, baseFrontMatterService.generateResearchAssistancePage(bookDefinition, withPageNumbers));
            log.debug("Front Matter Research Assistance HTML page generated.");

            final File westlawNextPage = new File(outputDir, FrontMatterFileName.WESTLAW + HTML_EXTENSION);
            writeHTMLFile(westlawNextPage, baseFrontMatterService.generateWestlawNextPage(withPageNumbers));
            log.debug("Front Matter WestlawNext HTML page generated.");
        }
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

}
