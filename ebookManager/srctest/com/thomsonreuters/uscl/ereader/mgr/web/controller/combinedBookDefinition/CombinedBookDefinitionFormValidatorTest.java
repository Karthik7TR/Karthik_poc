package com.thomsonreuters.uscl.ereader.mgr.web.controller.combinedBookDefinition;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinitionSource;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.CombinedBookDefinitionSourceService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.thomsonreuters.uscl.ereader.mgr.web.controller.combinedBookDefinition.CombinedBookDefinitionFormValidator.ERROR_COMBINED_BOOK_DEFINITION_PRIMARY;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.combinedBookDefinition.CombinedBookDefinitionFormValidator.ERROR_COMBINED_BOOK_DEFINITION_PRIMARY_EXIST;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.combinedBookDefinition.CombinedBookDefinitionFormValidator.ERROR_COMBINED_BOOK_DEFINITION_TITLE_ID_DUPLICATED;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.combinedBookDefinition.CombinedBookDefinitionFormValidator.ERROR_COMBINED_BOOK_DEFINITION_TITLE_ID_EMPTY;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.combinedBookDefinition.CombinedBookDefinitionFormValidator.ERROR_COMBINED_BOOK_DEFINITION_TITLE_ID_NOT_EXIST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CombinedBookDefinitionFormValidatorTest {

    private static final String Y = "Y";
    private static final String N = "N";
    private final String TITLE_ID_1 = "titleId1";
    private final String TITLE_ID_2 = "titleId2";
    private final String TITLE_ID_NOT_EXIST = "titleIdNotExist";
    private final Long EBOOK_DEFINITION_ID_1 = 1L;
    private final Long EBOOK_DEFINITION_ID_2 = 2L;
    @InjectMocks
    private CombinedBookDefinitionFormValidator combinedBookDefinitionFormValidator;
    @Mock
    private BookDefinitionService bookDefinitionService;
    @Mock
    private CombinedBookDefinitionSourceService combinedBookDefinitionSourceService;

    private CombinedBookDefinitionForm form;

    private Errors errors;

    @Before
    public void setUp() {
        form = new CombinedBookDefinitionForm();
        errors = new BindException(form, "form");
        when(bookDefinitionService.findBookDefinitionByTitle(TITLE_ID_1)).thenReturn(bookDefinition(TITLE_ID_1, EBOOK_DEFINITION_ID_1));
        when(bookDefinitionService.findBookDefinitionByTitle(TITLE_ID_2)).thenReturn(bookDefinition(TITLE_ID_2, EBOOK_DEFINITION_ID_2));
        when(bookDefinitionService.findBookDefinitionByTitle(TITLE_ID_NOT_EXIST)).thenReturn(null);
    }

    @Test
    public void shouldValidateFormSuccess() {
        form.setSourcesSet(Stream.of(combinedBookDefinitionSource(TITLE_ID_1, Y, 0),
                combinedBookDefinitionSource(TITLE_ID_2, N, 1)
        ).collect(Collectors.toSet()));
        combinedBookDefinitionFormValidator.validate(form, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void shouldValidateFormDuplicate() {
        form.setSourcesSet(Stream.of(combinedBookDefinitionSource(TITLE_ID_1, Y, 0),
                combinedBookDefinitionSource(TITLE_ID_1, N, 1)
        ).collect(Collectors.toSet()));
        combinedBookDefinitionFormValidator.validate(form, errors);
        assertTrue(errors.hasErrors());
        assertEquals(TITLE_ID_1, errors.getAllErrors().get(0).getArguments()[0]);
        assertEquals(ERROR_COMBINED_BOOK_DEFINITION_TITLE_ID_DUPLICATED, errors.getAllErrors().get(0).getCode());
    }

    @Test
    public void shouldValidateFormPrimary() {
        form.setSourcesSet(Stream.of(combinedBookDefinitionSource(TITLE_ID_1, Y, 0),
                combinedBookDefinitionSource(TITLE_ID_2, Y, 1)
        ).collect(Collectors.toSet()));
        combinedBookDefinitionFormValidator.validate(form, errors);
        assertTrue(errors.hasErrors());
        assertEquals(ERROR_COMBINED_BOOK_DEFINITION_PRIMARY, errors.getAllErrors().get(0).getCode());
    }

    @Test
    public void shouldValidateFormPrimary2() {
        form.setSourcesSet(Stream.of(combinedBookDefinitionSource(TITLE_ID_1, N, 0),
                combinedBookDefinitionSource(TITLE_ID_2, N, 1)
        ).collect(Collectors.toSet()));
        combinedBookDefinitionFormValidator.validate(form, errors);
        assertTrue(errors.hasErrors());
        assertEquals(ERROR_COMBINED_BOOK_DEFINITION_PRIMARY, errors.getAllErrors().get(0).getCode());
    }

    @Test
    public void shouldValidateFormPrimaryBookDefinitionAlreadyExist() {
        when(combinedBookDefinitionSourceService.findPrimarySourceWithBookDefinition(EBOOK_DEFINITION_ID_1))
                .thenReturn(Optional.of(combinedBookDefinitionSource(Y,3)));
        form.setSourcesSet(Stream.of(combinedBookDefinitionSource(TITLE_ID_1, Y, 0),
                combinedBookDefinitionSource(TITLE_ID_2, N, 1)
        ).collect(Collectors.toSet()));
        combinedBookDefinitionFormValidator.validate(form, errors);
        assertTrue(errors.hasErrors());
        assertEquals(ERROR_COMBINED_BOOK_DEFINITION_PRIMARY_EXIST, errors.getAllErrors().get(0).getCode());
    }

    @Test
    public void shouldValidateFormEmpty() {
        form.setSourcesSet(Stream.of(combinedBookDefinitionSource(StringUtils.EMPTY, N, 0),
                combinedBookDefinitionSource(TITLE_ID_1, Y, 1)
        ).collect(Collectors.toSet()));
        combinedBookDefinitionFormValidator.validate(form, errors);
        assertTrue(errors.hasErrors());
        assertEquals(ERROR_COMBINED_BOOK_DEFINITION_TITLE_ID_EMPTY, errors.getAllErrors().get(0).getCode());
    }

    @Test
    public void shouldValidateFormNotExist() {
        form.setSourcesSet(Stream.of(combinedBookDefinitionSource(TITLE_ID_NOT_EXIST, N, 0),
                combinedBookDefinitionSource(TITLE_ID_1, Y, 1)
        ).collect(Collectors.toSet()));
        combinedBookDefinitionFormValidator.validate(form, errors);
        assertTrue(errors.hasErrors());
        assertEquals(TITLE_ID_NOT_EXIST, errors.getAllErrors().get(0).getArguments()[0]);
        assertEquals(ERROR_COMBINED_BOOK_DEFINITION_TITLE_ID_NOT_EXIST, errors.getAllErrors().get(0).getCode());
    }

    @Test
    public void shouldInitializeForm() {
        CombinedBookDefinition combinedBookDefinition = new CombinedBookDefinition();
        combinedBookDefinition.setSources(Stream.of(
                        combinedBookDefinitionSource(TITLE_ID_1, N, 1),
                        combinedBookDefinitionSource(Y, 2))
                .collect(Collectors.toSet()));
        form.initialize(combinedBookDefinition);
        Assert.assertTrue(form.getSourcesSet().stream().anyMatch(source -> source.getBookDefinition() == null));
    }

    private CombinedBookDefinitionSource combinedBookDefinitionSource(final String fullyQualifiedTitleId, final String isPrimarySource, final Integer sequenceNum) {
        return CombinedBookDefinitionSource.builder()
                .isPrimarySource(isPrimarySource)
                .sequenceNum(sequenceNum)
                .bookDefinition(bookDefinition(fullyQualifiedTitleId))
                .build();
    }

    private CombinedBookDefinitionSource combinedBookDefinitionSource( final String isPrimarySource, final Integer sequenceNum) {
        return CombinedBookDefinitionSource.builder()
                .isPrimarySource(isPrimarySource)
                .sequenceNum(sequenceNum)
                .bookDefinition(null)
                .build();
    }

    private BookDefinition bookDefinition(final String fullyQualifiedTitleId) {
        BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setFullyQualifiedTitleId(fullyQualifiedTitleId);
        return bookDefinition;
    }

    private BookDefinition bookDefinition(final String fullyQualifiedTitleId, final long ebookDefinitionId) {
        BookDefinition bookDefinition = bookDefinition(fullyQualifiedTitleId);
        bookDefinition.setEbookDefinitionId(ebookDefinitionId);
        return bookDefinition;
    }

}