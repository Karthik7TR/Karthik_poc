package com.thomsonreuters.uscl.ereader.frontmatter.service;

import java.io.File;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.frontmatter.exception.EBookFrontMatterGenerationException;

/**
 * Service that generates HTML for all the Front Matter pages.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public interface CreateFrontMatterService {
    /**
     * Creates all the Front Matter pages for this eBook and writes them to the specified NAS directory.
     *
     * @param outputDir the target directory to which the generated front matter pages will be written
     * @param bookDefinition defines the book for which front matter is being generated
     */
    void generateAllFrontMatterPages(File outputDir, BookDefinition bookDefinition, boolean withPageNumbers)
        throws EBookFrontMatterGenerationException;

    /**
     * Helper method that generates the preview HTML for the Title page that is displayed in the Manager.
     *
     * @param bookDefinition defines the book for which front matter is being previewed
     * @return HTML that will be rendered for the Title page
     */
    String getTitlePage(BookDefinition bookDefinition) throws EBookFrontMatterGenerationException;

    /**
     * Helper method that generates the preview HTML for the Copyright page that is displayed in the Manager.
     *
     * @param bookDefinition defines the book for which front matter is being previewed
     * @return HTML that will be rendered for the Copyright page
     */
    String getCopyrightPage(BookDefinition bookDefinition) throws EBookFrontMatterGenerationException;

    /**
     * Helper method that generates the preview HTML for the Additional Front Matter page requested.
     *
     * @param bookDefinition defines the book for which front matter is being previewed
     * @param front_matter_page_id identifier of the additional front matter page
     * @return HTML that will be rendered for the identified additional front matter page
     */
    String getAdditionalFrontPage(BookDefinition bookDefinition, Long front_matter_page_id)
        throws EBookFrontMatterGenerationException;

    /**
     * Helper method that generates the preview HTML for the Research Assistance page that
     * is displayed in the Manager.
     *
     * @param bookDefinition defines the book for which front matter is being previewed
     * @return HTML that will be rendered for the Research Assistance page
     */
    String getResearchAssistancePage(BookDefinition bookDefinition) throws EBookFrontMatterGenerationException;

    /**
     * Helper method that generates the preview HTML for the WestlawNext page that
     * is displayed in the Manager.
     *
     * @param bookDefinition defines the book for which front matter is being previewed
     * @return HTML that will be rendered for the WestlawNext page
     */
    String getWestlawNextPage(BookDefinition bookDefinition) throws EBookFrontMatterGenerationException;
}
