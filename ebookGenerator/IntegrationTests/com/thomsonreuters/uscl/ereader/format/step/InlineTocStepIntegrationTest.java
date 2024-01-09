package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.format.service.CssStylingService;
import com.thomsonreuters.uscl.ereader.format.service.InlineTocService;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobInstanceId;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {StepIntegrationTestRunner.Config.class, InlineTocStepIntegrationTest.Config.class})
@ActiveProfiles("IntegrationTests")
public final class InlineTocStepIntegrationTest {
    private static final String VERSION = "1.0";
    private static final String TITLE_PREFIX = "cw/eg/";
    private static final String TITLE_ID = "book";
    private static final String TITLE_ID_SPLIT_BOOK = "split_book";
    private static final String TITLE_ID_SPLIT_BOOK_PART_2 = "split_book_pt2";
    private static final String TITLE_ID_SPLIT_BOOK_PART_3 = "split_book_pt3";
    private static final String TITLE_ID_SPLIT_BOOK_PART_4 = "split_book_pt4";
    private static final long JOB_ID = 1L;
    private static final String DOC_UUID_1 = "I10000000000000000000000000000000";
    private static final String DOC_UUID_2 = "I20000000000000000000000000000000";
    private static final String DOC_UUID_3 = "I30000000000000000000000000000000";
    private static final String DOC_UUID_4 = "I40000000000000000000000000000000";
    private static final String DOC_UUID_5 = "I50000000000000000000000000000000";

    @Autowired
    private InlineTocStep step;

    @Autowired
    private StepIntegrationTestRunner runner;

    @Before
    public void setUp() throws URISyntaxException {
        runner.setUp(step, "resourceInlineToc");
        when(step.getJobParameters().getString(JobParameterKey.BOOK_VERSION_SUBMITTED)).thenReturn(VERSION);
        givenJobInstanceId(step.getChunkContext(), JOB_ID);
        step.getBookDefinition().setFullyQualifiedTitleId(TITLE_ID);
        step.getBookDefinition().setInlineTocIncluded(true);
    }

    @Test
    public void shouldCreateTocWithPages() throws Exception {
        when(step.getJobExecutionContext().get(JobExecutionKey.WITH_PAGE_NUMBERS)).thenReturn(Boolean.TRUE);
        runner.test(step, "inlineTocWithPagesTest");
        verify(step.getJobExecutionContext()).put(JobExecutionKey.WITH_INLINE_TOC, Boolean.TRUE);
    }

    @Test
    public void shouldCreateTocWithoutPages() throws Exception {
        runner.test(step, "inlineTocWithoutPagesTest");
        verify(step.getJobExecutionContext()).put(JobExecutionKey.WITH_INLINE_TOC, Boolean.TRUE);
    }

    @Test
    public void shouldCreateTocForSplitBook() throws Exception {
        step.getBookDefinition().setFullyQualifiedTitleId(TITLE_ID_SPLIT_BOOK);
        runner.test(step, "inlineTocSplitBookTest");
        verify(step.getJobExecutionContext()).put(JobExecutionKey.WITH_INLINE_TOC, Boolean.TRUE);
    }

    @Test
    public void shouldCreateTocWithDefaultStyles() throws Exception {
        runner.test(step, "noInlineTocAttributes");
        verify(step.getJobExecutionContext()).put(JobExecutionKey.WITH_INLINE_TOC, Boolean.TRUE);
    }

    @Test
    public void shouldCreateTocWithMissingDocument() throws Exception {
        runner.test(step, "inlineTocMissingDocumentTest");
        verify(step.getJobExecutionContext()).put(JobExecutionKey.WITH_INLINE_TOC, Boolean.TRUE);
    }

    @Test
    public void shouldCreateTocWithMissingDocumentSplitBook() throws Exception {
        step.getBookDefinition().setFullyQualifiedTitleId(TITLE_ID_SPLIT_BOOK);
        runner.test(step, "inlineTocMissingDocumentSplitBookTest");
        verify(step.getJobExecutionContext()).put(JobExecutionKey.WITH_INLINE_TOC, Boolean.TRUE);
    }

    @Test
    public void shouldNotCreateToc() throws Exception {
        step.getBookDefinition().setInlineTocIncluded(false);
        step.executeStep();
        verify(step.getJobExecutionContext(), never()).put(JobExecutionKey.WITH_INLINE_TOC, Boolean.TRUE);
    }

    private static Map<String, String> getFamilyGuidsMap() {
        final Map<String, String> map = new HashMap<>();

        map.put(DOC_UUID_1, "I5885e15011c411e68073fa8dc60233b8");
        map.put(DOC_UUID_2, "I0b12194095cc11e4a692d49183167da5");
        map.put(DOC_UUID_3, "I89e5c1200f5a11da9cd1b8ea82133782");
        map.put(DOC_UUID_4, "I8a0fb7510f5a11da9cd1b8ea82133782");
        map.put(DOC_UUID_5, "I957a4ba0558a11dc9c6a0000837bc6dd");

        return map;
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public InlineTocStep inlineTocStep() {
            return new InlineTocStep();
        }

        @Bean
        public InlineTocService inlineTocService() {
            return new InlineTocService();
        }

        @Bean
        public CssStylingService cssStylingService() {
            return new CssStylingService();
        }

        @Bean
        public DocMetadataService docMetadataService() {
            final DocMetadataService docMetadataService = Mockito.mock(DocMetadataService.class);
            when(docMetadataService.findDistinctProViewFamGuidsByJobId(any())).thenReturn(getFamilyGuidsMap());

            whenFindDocMetadata(docMetadataService, TITLE_ID, null, null);
            whenFindDocMetadata(docMetadataService, TITLE_ID_SPLIT_BOOK, DOC_UUID_1, TITLE_PREFIX + TITLE_ID_SPLIT_BOOK);
            whenFindDocMetadata(docMetadataService, TITLE_ID_SPLIT_BOOK, DOC_UUID_2, TITLE_PREFIX + TITLE_ID_SPLIT_BOOK_PART_2);
            whenFindDocMetadata(docMetadataService, TITLE_ID_SPLIT_BOOK, DOC_UUID_3, TITLE_PREFIX + TITLE_ID_SPLIT_BOOK_PART_3);
            whenFindDocMetadata(docMetadataService, TITLE_ID_SPLIT_BOOK, DOC_UUID_4, TITLE_PREFIX + TITLE_ID_SPLIT_BOOK_PART_4);

            return docMetadataService;
        }

        private void whenFindDocMetadata(final DocMetadataService docMetadataService,
                                         final String titleId, final String docUuid, final String splitTitleId) {
            when(docMetadataService.findDocMetadataByPrimaryKey(eq(titleId), eq(JOB_ID), docUuid == null ? any() : eq(docUuid)))
                    .thenReturn(getDocMetadata(titleId, splitTitleId));
        }

        private DocMetadata getDocMetadata(final String titleId, final String titleIdSplitBook) {
            DocMetadata docMetadata = new DocMetadata();
            docMetadata.setTitleId(titleId);
            docMetadata.setSpitBookTitle(titleIdSplitBook);
            return docMetadata;
        }
    }
}