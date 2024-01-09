package com.thomsonreuters.uscl.ereader.common.filesystem;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileSystemMatcher.hasPath;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.File;
import java.util.Date;
import java.util.GregorianCalendar;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class BookFileSystemImplTest {
    private static final Long JOB_ID = 1L;
    private static final Long BOOK_DEF_ID = 777L;
    private static final Date JOB_DATETIME = new GregorianCalendar(2017, 2, 8).getTime();
    private static final String TITLE_ID = "titleId";
    private static final String EXPECTED_PATH = "rootDirectory/env/data/20170308/titleId/1";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private PublishingStatsService publishingStatsService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BookDefinitionService bookDefinitionService;
    @InjectMocks
    private BookFileSystemImpl fileSystem;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BookStep step;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    @SneakyThrows
    public void onTestSetUp() {
        FieldUtils
            .writeField(fileSystem, "rootWorkDirectory", new File(temporaryFolder.getRoot(), "rootDirectory"), true);
        FieldUtils.writeField(fileSystem, "environmentName", "env", true);
    }

    @Test
    public void shouldReturnWorkDirectory() {
        //given
        given(step.getSubmitTimestamp()).willReturn(JOB_DATETIME);
        given(step.getBookDefinition().getTitleId()).willReturn(TITLE_ID);
        given(step.getJobInstanceId()).willReturn(JOB_ID);

        //when
        final File workDirectory = fileSystem.getWorkDirectory(step);
        assertThat(workDirectory, hasPath(EXPECTED_PATH));
    }

    @Test
    public void shouldReturnWorkDirectoryByJobInstanceId() {
        //given
        given(publishingStatsService.findPublishingStatsByJobId(JOB_ID).getEbookDefId()).willReturn(BOOK_DEF_ID);
        given(publishingStatsService.findPublishingStatsByJobId(JOB_ID).getJobSubmitTimestamp()).willReturn(JOB_DATETIME);
        given(bookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEF_ID).getTitleId()).willReturn(TITLE_ID);

        //when
        final File workDirectory = fileSystem.getWorkDirectoryByJobId(JOB_ID);
        assertThat(workDirectory, hasPath(EXPECTED_PATH));
    }
}
