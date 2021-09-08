package com.thomsonreuters.uscl.ereader.gather.step.service.container;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;

public class DocsGuidsContainerTest {
    private DocsGuidsContainer docsGuidsContainer;
    private File docsGuidsFile;

    @Before
    public void setUp() throws Exception {
        docsGuidsContainer = new DocsGuidsContainer();
        docsGuidsFile = new File(DocsGuidsContainerTest.class.getResource("docs-guids.txt").toURI());
    }

    @Test
    public void shouldAddSource() {
        docsGuidsContainer.addSource(docsGuidsFile, new BookDefinition());
        Collection<String> expected = Collections.singletonList("Ifd6da79174a611e094d30000837bc6dd,Ifd6da79074a611e094d30000837bc6dd|\r\n" +
                "I94ef56b4e1e311da9c26e67ca3b71335,I94ef56b3e1e311da9c26e67ca3b71335|");
        assertEquals(expected, docsGuidsContainer.getSources());
    }
}
