package com.thomsonreuters.uscl.ereader.frontmatter.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PdfImagesService {
    Map<String, List<String>> generatePdfImages(final Set<String> frontMatterPdfFileNames, final File frontMatterTargetDir);
}
