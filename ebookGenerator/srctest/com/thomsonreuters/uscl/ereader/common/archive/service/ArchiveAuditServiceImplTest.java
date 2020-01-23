package com.thomsonreuters.uscl.ereader.common.archive.service;

import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.splitNode;
import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.splitNodes;
import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.version;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.common.archive.step.BaseArchiveStep;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.service.splitnode.SplitNodesInfoService;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.util.BookTitlesUtil;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class ArchiveAuditServiceImplTest {

    private static final String TITLE_ID_PT_2 = "titleId_pt2";
    private static final String VERSION = "1.1";
    public static final String TITLE_ID = "titleId";
    public static final long ID = 1L;
    private static final String TITLE_ID_PT_1 = "titleId_pt1";
    private static final String TITLE_ID_PT_3 = "titleId_pt3";

    @InjectMocks
    private ArchiveAuditServiceImpl service;
    @Mock
    private BookDefinitionService bookService;
    @Mock
    private BookTitlesUtil bookTitlesUtil;
    @Mock
    private ProviewAuditService proviewAuditService;
    @Mock
    private SplitNodesInfoService splitNodesInfoService;
    @Mock
    private BaseArchiveStep step;
    @Mock
    private BookDefinition book;
    @Mock
    private FormatFileSystem fileSystem;
    @Mock
    private File splitBookInfoFile;
    @Captor
    private ArgumentCaptor<ProviewAudit> captor;

    @Before
    public void setUp() {
        given(step.getBookDefinition()).willReturn(book);
        given(step.getBookVersion()).willReturn(version("v" + VERSION));
        given(step.getBookVersionString()).willReturn(VERSION);
        given(book.getFullyQualifiedTitleId()).willReturn(TITLE_ID);
        given(book.getEbookDefinitionId()).willReturn(ID);
    }

    @Test
    public void shouldSaveAuditForSingleVolumeBook() {
        //given
        given(book.isSplitBook()).willReturn(false);
        //when
        service.saveAudit(step);
        //then
        then(proviewAuditService).should().save(captor.capture());
        final ProviewAudit audit = captor.getValue();
        assertThat(audit.getTitleId(), is(TITLE_ID));
    }

    @Test
    public void shouldSaveAuditForSplitTitles() {
        //given
        given(book.isSplitBook()).willReturn(true);
        given(step.getSplitTitles()).willReturn(Arrays.asList(TITLE_ID_PT_1, TITLE_ID_PT_2));
        //when
        service.saveAudit(step);
        //then
        then(proviewAuditService).should(times(2)).save(captor.capture());
        final ProviewAudit audit = captor.getAllValues().get(1);
        assertThat(audit.getTitleId(), is(TITLE_ID_PT_2));
    }

    @Test
    public void shouldUpdateSplitNodesIfChanged() {
        //given
        given(book.isSplitBook()).willReturn(true);
        final Set<SplitNodeInfo> submittedSplitNodes = splitNodes(splitNode(book, TITLE_ID_PT_2, VERSION));
        final Set<SplitNodeInfo> persistedSplitNodes =
            splitNodes(splitNode(book, TITLE_ID_PT_2, VERSION), splitNode(book, TITLE_ID_PT_3, VERSION));
        givenSplitNodes(submittedSplitNodes, persistedSplitNodes);
        //when
        service.saveAudit(step);
        //then
        then(bookService).should().updateSplitNodeInfoSet(ID, submittedSplitNodes, VERSION);
    }

    @Test
    public void shouldUpdateSplitNodesIfChangedAndPersistedIsEmpty() {
        given(book.isSplitBook()).willReturn(true);
        final Set<SplitNodeInfo> submittedSplitNodes = splitNodes(splitNode(book, TITLE_ID_PT_2, VERSION));
        final Set<SplitNodeInfo> persistedSplitNodes = new HashSet<>();
        givenSplitNodes(submittedSplitNodes, persistedSplitNodes);
        //when
        service.saveAudit(step);
        //then
        then(bookService).should().updateSplitNodeInfoSet(ID, submittedSplitNodes, VERSION);
    }

    private void givenSplitNodes(
        final Set<SplitNodeInfo> submittedSplitNodes,
        final Set<SplitNodeInfo> persistedSplitNodes) {
        given(fileSystem.getSplitBookInfoFile(step)).willReturn(splitBookInfoFile);
        given(
            splitNodesInfoService
                .getSubmittedSplitNodes(eq(splitBookInfoFile), any(BookDefinition.class), any(Version.class)))
                    .willReturn(submittedSplitNodes);
        given(bookTitlesUtil.getSplitNodeInfosByVersion(any(BookDefinition.class), any(Version.class)))
            .willReturn(persistedSplitNodes);
    }
}
