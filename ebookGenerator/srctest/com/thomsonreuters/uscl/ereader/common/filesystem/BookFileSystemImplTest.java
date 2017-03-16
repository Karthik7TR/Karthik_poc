package com.thomsonreuters.uscl.ereader.common.filesystem;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileSystemMatcher.hasPath;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.File;
import java.util.GregorianCalendar;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class BookFileSystemImplTest
{
    @InjectMocks
    private BookFileSystemImpl fileSystem;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BookStep step;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldReturnWorkDirectory() throws IllegalAccessException
    {
        //given
        FieldUtils
            .writeField(fileSystem, "rootWorkDirectory", new File(temporaryFolder.getRoot(), "rootDirectory"), true);
        FieldUtils.writeField(fileSystem, "environmentName", "env", true);

        given(step.getSubmitTimestamp()).willReturn(new GregorianCalendar(2017, 2, 8).getTime());
        given(step.getBookDefinition().getTitleId()).willReturn("titleId");
        given(step.getJobInstanceId()).willReturn(1L);
        //when
        final File workDirectory = fileSystem.getWorkDirectory(step);
        assertThat(workDirectory, hasPath("rootDirectory/env/data/20170308/titleId/1"));
    }
}
