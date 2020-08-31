package com.thomsonreuters.uscl.ereader.frontmatter.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.frontmatter.exception.EBookFrontMatterGenerationException;

public interface CreateFrontMatterService {

    void generateAllFrontMatterPages(File outputDir, BookDefinition bookDefinition, boolean withPageNumbers, Map<String, List<String>> frontMatterPdfImageNames)
        throws EBookFrontMatterGenerationException;
}
