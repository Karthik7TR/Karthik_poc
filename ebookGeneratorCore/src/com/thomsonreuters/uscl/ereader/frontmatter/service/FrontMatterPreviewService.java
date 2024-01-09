package com.thomsonreuters.uscl.ereader.frontmatter.service;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.frontmatter.exception.EBookFrontMatterGenerationException;

public interface FrontMatterPreviewService {
    String getTitlePagePreview(BookDefinition bookDefinition) throws EBookFrontMatterGenerationException;

    String getCopyrightPagePreview(BookDefinition bookDefinition) throws EBookFrontMatterGenerationException;

    String getAdditionalFrontPagePreview(BookDefinition bookDefinition, Long frontMatterPageId)
            throws EBookFrontMatterGenerationException;

    String getResearchAssistancePagePreview(BookDefinition bookDefinition) throws EBookFrontMatterGenerationException;

    String getWestlawNextPagePreview(BookDefinition bookDefinition) throws EBookFrontMatterGenerationException;
}
