package com.thomsonreuters.uscl.ereader.xpp.group.step;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenBook;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenBookVersion;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.group.service.GroupServiceWithRetry;
import com.thomsonreuters.uscl.ereader.common.service.splitnode.SplitNodesInfoService;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.scope.context.ChunkContext;

@RunWith(MockitoJUnitRunner.class)
public final class GroupXppStepTest {
    @InjectMocks
    private GroupXppStep step;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private GroupService groupService;
    @Mock
    private GroupServiceWithRetry groupServiceWithRetry;
    @Mock
    private SplitNodesInfoService splitNodesInfoService;
    @Mock
    private BookDefinition book;
    @Mock
    private GroupDefinition groupDefinition;
    @Mock
    private GroupDefinition anotherGroupDefinition;
    @Mock
    private PublishingStatsService publishingStatsService;

    @Mock
    private File splitBookInfoFile;
    @Mock
    private FormatFileSystem fileSystem;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        givenBook(chunkContext, book);
        given(book.getEbookDefinitionId()).willReturn(1L);
        givenBookVersion(chunkContext, "1.1");
        given(groupDefinition.isSimilarGroup(groupDefinition)).willReturn(true);
        given(groupDefinition.isSimilarGroup(anotherGroupDefinition)).willReturn(false);
    }

    @Test
    public void shouldDoNothingIfNotGrouped() throws Exception {
        //given
        given(publishingStatsService.hasBeenGrouped(1L)).willReturn(false);
        given(book.getGroupName()).willReturn("");
        //when
        step.executeStep();
        //then
        then(groupService).should(never()).removeAllPreviousGroups(book);
    }

    @Test
    public void shouldRemovePreviousGroupsIfWasGrouped() throws Exception {
        //given
        given(publishingStatsService.hasBeenGrouped(1L)).willReturn(true);
        given(book.getGroupName()).willReturn("");
        //when
        step.executeStep();
        //then
        then(groupService).should().removeAllPreviousGroups(book);
    }

    @Test
    public void shouldNotCreateGroupIfTheSameIsLast() throws Exception {
        //given
        given(book.getGroupName()).willReturn("groupName");
        given(book.isSplitBook()).willReturn(false);
        given(groupService.createGroupDefinition(book, "v1.1", null)).willReturn(groupDefinition);
        given(groupService.getLastGroup(book)).willReturn(groupDefinition);
        //when
        step.executeStep();
        //then
        then(groupServiceWithRetry).should(never()).createGroup(groupDefinition);
        assertThat(step.getGroupVersion(), nullValue());
    }

    @Test
    public void shouldCreateGroupIfDifferentIsLast() throws Exception {
        //given
        given(book.getGroupName()).willReturn("groupName");
        given(book.isSplitBook()).willReturn(true);
        given(splitNodesInfoService.getTitleIds(eq(splitBookInfoFile), anyString())).willReturn(null);
        given(groupService.createGroupDefinition(eq(book), eq("v1.1"), Matchers.anyList())).willReturn(groupDefinition);
        given(groupService.getLastGroup(book)).willReturn(anotherGroupDefinition);
        given(fileSystem.getSplitBookInfoFile(step)).willReturn(splitBookInfoFile);
        //when
        step.executeStep();
        //then
        then(groupServiceWithRetry).should().createGroup(groupDefinition);
        assertThat(step.getGroupVersion(), notNullValue());
    }
}
