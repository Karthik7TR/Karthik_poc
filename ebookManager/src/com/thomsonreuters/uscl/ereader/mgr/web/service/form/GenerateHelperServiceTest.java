package com.thomsonreuters.uscl.ereader.mgr.web.service.form;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import static com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType.XPP;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm.Version.MAJOR;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import java.util.List;
import java.util.Optional;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.request.service.XppBundleArchiveService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.JobExecution;
import org.springframework.context.support.MessageSourceAccessor;

@RunWith(MockitoJUnitRunner.class)
public final class GenerateHelperServiceTest {
    @InjectMocks
    private GenerateHelperService sut;
    @Mock
    private MessageSourceAccessor messageSourceAccessor;
    @Mock
    private ManagerService managerService;
    @Mock
    private JobRequestService jobRequestService;
    @Mock
    private XppBundleArchiveService xppBundleArchiveService;
    private GenerateBookForm form = new GenerateBookForm();
    private BookDefinition book = new BookDefinition();
    private String errMsg = "error";
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        book.setEbookDefinitionId(1L);
        form.setNewVersion(MAJOR);
        given(jobRequestService.isBookInJobRequest(eq(book.getEbookDefinitionId()))).willReturn(false);
    }

    @Test
    public void shouldReturnErrorIfAlreadyQueued() {
        //given
        given(jobRequestService.isBookInJobRequest(eq(book.getEbookDefinitionId()))).willReturn(true);
        given(messageSourceAccessor.getMessage(eq("mesg.job.enqueued.fail"), (Object[]) any())).willReturn(errMsg);
        //when
        final Optional<String> error = sut.getError(book, form);
        //then
        assertEquals(of(errMsg), error);
    }

    @Test
    public void shouldReturnErrorIfBookIsDeleted() {
        //given
        book.setIsDeletedFlag(true);
        given(messageSourceAccessor.getMessage(eq("mesg.book.deleted"))).willReturn(errMsg);
        //when
        final Optional<String> error = sut.getError(book, form);
        //then
        assertEquals(of(errMsg), error);
    }

    @Test
    public void shouldReturnErrorIfEmptyPrintComponents() {
        //given
        book.setSourceType(XPP);
        book.setPrintComponents(emptyList());
        given(messageSourceAccessor.getMessage(eq("mesg.empty.printcomponents"))).willReturn(errMsg);
        //when
        final Optional<String> error = sut.getError(book, form);
        //then
        assertEquals(of(errMsg), error);
    }

    @Test
    public void shouldReturnErrorIfBundlesMissing() {
        //given
        final PrintComponent component = new PrintComponent();
        component.setMaterialNumber("1");
        final List<String> materialNumbers = singletonList("1");
        book.setSourceType(XPP);
        book.setPrintComponents(singletonList(component));
        given(xppBundleArchiveService.findByMaterialNumberList(eq(materialNumbers))).willReturn(emptyList());
        given(messageSourceAccessor.getMessage(eq("mesg.missing.bundle"))).willReturn(errMsg);
        //when
        final Optional<String> error = sut.getError(book, form);
        //then
        assertEquals(of(errMsg), error);
    }

    @Test
    public void shouldReturnErrorIfJobAlreadyRunning() {
        //given
        final XppBundleArchive bundle = new XppBundleArchive();
        bundle.setMaterialNumber("1");
        final PrintComponent component = new PrintComponent();
        component.setMaterialNumber("1");
        final List<String> materialNumbers = singletonList("1");
        book.setSourceType(XPP);
        book.setPrintComponents(singletonList(component));
        given(xppBundleArchiveService.findByMaterialNumberList(eq(materialNumbers))).willReturn(singletonList(bundle));
        given(messageSourceAccessor.getMessage(eq("mesg.job.enqueued.in.progress"), (Object[]) any())).willReturn(errMsg);
        given(managerService.findRunningJob(eq(book))).willReturn(new JobExecution(123L));
        //when
        final Optional<String> error = sut.getError(book, form);
        //then
        assertEquals(of(errMsg), error);
    }

    @Test
    public void shouldNotReturnErrorIfEverythingOk() {
        //given
        final XppBundleArchive bundle = new XppBundleArchive();
        bundle.setMaterialNumber("1");
        final PrintComponent component = new PrintComponent();
        component.setMaterialNumber("1");
        final List<String> materialNumbers = singletonList("1");
        book.setSourceType(XPP);
        book.setPrintComponents(singletonList(component));
        given(xppBundleArchiveService.findByMaterialNumberList(eq(materialNumbers))).willReturn(singletonList(bundle));
        given(managerService.findRunningJob(eq(book))).willReturn(null);
        //when
        final Optional<String> error = sut.getError(book, form);
        //then
        assertEquals(empty(), error);
    }

    @Test
    public void shouldGetPriorityLabel() {
        //given
        final String expected = "test";
        form.setHighPriorityJob(true);
        given(messageSourceAccessor.getMessage(anyString())).willReturn(expected);
        //when
        final String actual = sut.getPriorityLabel(form);
        //then
        assertEquals(expected, actual);
    }
}
