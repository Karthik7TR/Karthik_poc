package com.thomsonreuters.uscl.ereader.xpp.archive.printcomponent.step;

import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.StepTestUtil;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.service.PrintComponentHistoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.scope.context.ChunkContext;

@RunWith(MockitoJUnitRunner.class)
public final class ArchivePrintComponentsStepTest {
    private static final String BOOK_DEFINITION_VERSION = "1.0";
    private static final long BOOK_DEFINITION_ID = 12345L;
    private static final Optional<Integer> PRINT_COMPONENTS_VERSION_CURRENT = Optional.of(1);
    private static final int PRINT_COMPONENTS_VERSION_NEW = 2;

    @InjectMocks
    private ArchivePrintComponentsStep step;
    @Mock
    private PrintComponentHistoryService printComponentsHistoryService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;

    private BookDefinition bookDefinition;
    private Set<PrintComponent> printComponents;

    @Before
    public void setUp() {
        printComponents = new HashSet<>();
        printComponents.add(new PrintComponent());
        bookDefinition = new BookDefinition();
        bookDefinition.setEbookDefinitionId(BOOK_DEFINITION_ID);
        bookDefinition.setPrintComponents(printComponents);

        StepTestUtil.givenExecutionContextParameter(chunkContext, JobParameterKey.EBOOK_DEFINITON, bookDefinition);
        StepTestUtil.givenJobParameter(chunkContext, JobParameterKey.BOOK_VERSION_SUBMITTED, BOOK_DEFINITION_VERSION);
        when(printComponentsHistoryService.getLatestPrintComponentHistoryVersion(BOOK_DEFINITION_ID, BOOK_DEFINITION_VERSION)).thenReturn(PRINT_COMPONENTS_VERSION_CURRENT);
    }

    @Test
    public void shouldSave() throws Exception {
        //given
        //when
        step.executeStep();
        //then
        then(printComponentsHistoryService).should().savePrintComponents(eq(printComponents), eq(BOOK_DEFINITION_ID), eq(BOOK_DEFINITION_VERSION), eq(PRINT_COMPONENTS_VERSION_NEW));
    }

}
