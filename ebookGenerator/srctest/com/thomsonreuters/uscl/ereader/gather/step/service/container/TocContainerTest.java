package com.thomsonreuters.uscl.ereader.gather.step.service.container;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

public class TocContainerTest {
    private static final String TITLE_ID = "uscl/an/titleId";
    private static final String PROVIEW_DISPLAY_NAME = "ProView Title";
    private TocContainer tocContainer;
    private File tocXmlFile;

    @Before
    public void setUp() throws Exception {
        tocContainer = new TocContainer();
        tocXmlFile = new File(TocContainerTest.class.getResource("toc.xml").toURI());
    }

    @Test
    public void shouldAddSource() {
        BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setFullyQualifiedTitleId(TITLE_ID);
        bookDefinition.setProviewDisplayName(PROVIEW_DISPLAY_NAME);
        tocContainer.addSource(tocXmlFile, bookDefinition);
        Collection<String> expected = Arrays.asList("<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<EBook>",
                "<EBookTitle titleId=\"uscl-an-titleId\" proviewName=\"ProView Title\"/>",
                "<EBookInlineToc/>",
                "<EBookToc><Name>Alabama</Name><Guid>N2B2C9BC0709611DA941ED2CF24A3A24D-0000011</Guid>\r\n</EBookToc>",
                "<EBookPublishingInformation/>",
                "</EBook>");
        assertEquals(expected, tocContainer.getSources());
    }
}
