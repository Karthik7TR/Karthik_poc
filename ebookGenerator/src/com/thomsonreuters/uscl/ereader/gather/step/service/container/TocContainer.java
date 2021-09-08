package com.thomsonreuters.uscl.ereader.gather.step.service.container;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TocContainer implements SourceContainer {
    private static final String EBOOK_OPEN = "<EBook>";
    private static final String EBOOK_CLOSE = "</EBook>";
    private static final String EBOOK_TITLE_PATTERN = "<EBookTitle titleId=\"%s\" proviewName=\"%s\"/>";
    private static final String EBOOK_PUBLISHING_INFORMATION = "<EBookPublishingInformation/>";
    private static final String EBOOK_INLINE_TOC = "<EBookInlineToc/>";
    private static final String XML_DEFINITION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String CR_LF = StringUtils.CR + StringUtils.LF;
    private final List<String> tocFileComponents = new ArrayList<>();

    public TocContainer() {
        tocFileComponents.add(XML_DEFINITION);
        tocFileComponents.add(EBOOK_OPEN);
    }

    @SneakyThrows
    @Override
    public SourceContainer addSource(final File source, final BookDefinition bookDefinition) {
        tocFileComponents.add(String.format(EBOOK_TITLE_PATTERN, new TitleId(bookDefinition.getFullyQualifiedTitleId()).escapeSlashWithDash(), bookDefinition.getProviewDisplayName()));
        if (shouldPlaceInlineTocAnchor()) {
            tocFileComponents.add(EBOOK_INLINE_TOC);
        }
        String tocString = FileUtils.readFileToString(source)
                .replace(XML_DEFINITION + CR_LF + EBOOK_OPEN + CR_LF, StringUtils.EMPTY)
                .replace(CR_LF + EBOOK_CLOSE + CR_LF, StringUtils.EMPTY);
        tocFileComponents.add(tocString);
        return this;
    }

    @Override
    public Collection<String> getSources() {
        tocFileComponents.add(EBOOK_PUBLISHING_INFORMATION);
        tocFileComponents.add(EBOOK_CLOSE);
        return tocFileComponents;
    }

    private boolean shouldPlaceInlineTocAnchor() {
        return tocFileComponents.size() == 3;
    }
}
