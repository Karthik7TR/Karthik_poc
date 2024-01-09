package com.thomsonreuters.uscl.ereader.frontmatter.service;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.frontmatter.exception.EBookFrontMatterGenerationException;

import java.util.List;
import java.util.Map;

public interface BaseFrontMatterService {
    String generateTitlePage(final BookDefinition bookDefinition, final boolean withPageNumbers) throws EBookFrontMatterGenerationException;

    String generateCopyrightPage(final BookDefinition bookDefinition, final boolean withPageNumbers)
            throws EBookFrontMatterGenerationException;

    String generateAdditionalFrontMatterPage(final BookDefinition bookDefinition, final Long pageId, final Map<String, List<String>> frontMatterPdfImageNames)
            throws EBookFrontMatterGenerationException;

    String generateResearchAssistancePage(final BookDefinition bookDefinition, final boolean withPageNumbers)
            throws EBookFrontMatterGenerationException;

    String generateWestlawNextPage(final boolean withPageNumbers) throws EBookFrontMatterGenerationException;

}
