package com.thomsonreuters.uscl.ereader.frontmatter;

import java.util.Collection;

import com.thomsonreuters.uscl.ereader.config.BookDefinitionUtils;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.DocumentTypeCodeService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = FrontMatterIntegrationTestConf.class)
@ActiveProfiles("IntegrationTests")
public final class FrontMatterIntegrationTest {
    private static String BOOK_TITLE = "uscl/an/frontmatter_test_123";
    @Autowired
    private BookDefinitionService bookDefinitionService;
    private BookDefinition eBook;

    @Autowired
    private CodeService codeService;
    @Autowired
    private DocumentTypeCodeService documentTypeCodeService;

    /**
     * Operation Unit Test Save an existing Audit entity
     *
     */
    private void saveBook() {
        final BookDefinition filledBook =
            BookDefinitionUtils.fillBookDefinition(codeService, documentTypeCodeService, BOOK_TITLE);
        eBook = bookDefinitionService.saveBookDefinition(filledBook);
    }

    /**
     * Operation Unit Test
     *
     * @author Dong Kim
     */
    @Ignore
    @Test
    public void addFrontMatterSection() {
        saveBook();
        eBook = bookDefinitionService.findBookDefinitionByTitle(BOOK_TITLE);

        Collection<FrontMatterPage> frontMatterPages = eBook.getFrontMatterPages();
        for (final FrontMatterPage page : frontMatterPages) {
            final FrontMatterSection fms = new FrontMatterSection();
            fms.setSectionHeading("Section Heading 2");
            fms.setSectionText("some text 2");
            fms.setSequenceNum(2);
            fms.setFrontMatterPage(page);
            page.getFrontMatterSections().add(fms);

            final FrontMatterPdf frontMatterPdf = new FrontMatterPdf();
            frontMatterPdf.setPdfFilename("somefile.pdf 2");
            frontMatterPdf.setPdfLinkText("Link Text 2");
            frontMatterPdf.setSection(fms);
            fms.getPdfs().add(frontMatterPdf);
        }

        Assert.assertEquals(1, frontMatterPages.size());

        eBook = bookDefinitionService.saveBookDefinition(eBook);
        frontMatterPages = eBook.getFrontMatterPages();
        for (final FrontMatterPage page : frontMatterPages) {
            Assert.assertEquals(2, page.getFrontMatterSections().size());
        }
    }

    @Test
    public void deleteFrontMatterPage() {
        saveBook();
        final BookDefinition book = bookDefinitionService.findBookDefinitionByTitle(BOOK_TITLE);
        book.getFrontMatterPages().clear();
        eBook = bookDefinitionService.saveBookDefinition(book);
        Assert.assertEquals(0, eBook.getFrontMatterPages().size());
    }
}
