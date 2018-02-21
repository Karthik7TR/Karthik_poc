package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.bookdefinitionlock;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.mgr.web.service.book.BookDefinitionLockService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class BookDefinitionLockControllerTest {
    @InjectMocks
    private BookDefinitionLockController sut;
    @Mock
    private BookDefinitionLockService lockService;
    @Mock
    private BookDefinitionService bookDefinitionService;
    @Mock
    private BookDefinition book;
    private static final Long ID = 1L;

    @Before
    public void setUp() {
        doNothing().when(lockService).extendLock(book);
        given(bookDefinitionService.findBookDefinitionByEbookDefId(ID)).willReturn(book);
    }

    @Test
    public void shouldExtendLock() {
        //when
        sut.extendLock(ID);
        //then
        verify(lockService).extendLock(book);
    }
}
