package com.thomsonreuters.uscl.ereader.frontmatter.service;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(MockitoJUnitRunner.class)
public final class CreateFrontMatterServiceImplTest {
    private static final String USCL_TITLE_ID = "uscl/an/test";
    private static final String CW_TITLE_ID = "cw/eg/test";
    private static final Set<String> ALL_PUBLISHERS_PAGES = Sets.newSet("Copyright.html", "FrontMatterTitle.html");
    private static final Set<String> USCL_ONLY_PAGES = Sets.newSet("ResearchAssistance.html", "Westlaw.html");
    private static final Set<String> ADDITIONAL_FRONT_MATTERS = Sets.newSet("AdditionalFrontMatter1.html", "AdditionalFrontMatter2.html");

    private CreateFrontMatterService createFrontMatterService;

    @Mock
    private BaseFrontMatterService baseFrontMatterService;

    @Mock
    private PdfImagesService pdfImagesService;

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() {
        createFrontMatterService = new CreateFrontMatterServiceImpl(baseFrontMatterService, pdfImagesService);
    }

    @Test
    @SneakyThrows
    public void testGenerateAllFrontMatterPagesUsclBook() {
        final BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setFullyQualifiedTitleId(USCL_TITLE_ID);
        Set<String> expected = Stream.concat(ALL_PUBLISHERS_PAGES.stream(), USCL_ONLY_PAGES.stream()).collect(Collectors.toSet());
        createFrontMatterService.generateAllFrontMatterPages(folder.getRoot(), CombinedBookDefinition.fromBookDefinition(bookDefinition), true);
        Assert.assertEquals(expected, Arrays.stream(folder.getRoot().list()).collect(Collectors.toSet()));
    }

    @Test
    @SneakyThrows
    public void testGenerateAllFrontMatterPagesCwBook() {
        final BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setFullyQualifiedTitleId(CW_TITLE_ID);
        createFrontMatterService.generateAllFrontMatterPages(folder.getRoot(), CombinedBookDefinition.fromBookDefinition(bookDefinition), true);
        Assert.assertEquals(ALL_PUBLISHERS_PAGES, Arrays.stream(folder.getRoot().list()).collect(Collectors.toSet()));
    }

    @Test
    @SneakyThrows
    public void testGenerateAllFrontMatterPagesAdditionalFrontMatters() {
        final BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setFullyQualifiedTitleId(CW_TITLE_ID);
        bookDefinition.setFrontMatterPages(getAdditionalFrontMatters());
        Set<String> expected = Stream.concat(ALL_PUBLISHERS_PAGES.stream(), ADDITIONAL_FRONT_MATTERS.stream()).collect(Collectors.toSet());
        createFrontMatterService.generateAllFrontMatterPages(folder.getRoot(), CombinedBookDefinition.fromBookDefinition(bookDefinition), true);
        Assert.assertEquals(expected, Arrays.stream(folder.getRoot().list()).collect(Collectors.toSet()));
    }

    @NotNull
    private List<FrontMatterPage> getAdditionalFrontMatters() {
        return Stream.iterate(1L, i -> i + 1)
                .limit(2)
                .map(i -> {
                    FrontMatterPage page = new FrontMatterPage();
                    page.setId(i);
                    return page;
                }).collect(Collectors.toList());
    }
}
