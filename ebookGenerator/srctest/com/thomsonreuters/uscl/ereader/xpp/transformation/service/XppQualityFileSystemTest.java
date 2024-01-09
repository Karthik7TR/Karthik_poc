package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableCollection;

import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class XppQualityFileSystemTest {
    private static final String MATERIAL_NUMBER_1 = "123";
    private static final String MATERIAL_NUMBER_2 = "456";
    private static final String FILE1_NAME = "file1.";
    private static final String FILE2_1_NAME = "file2_1.";
    private static final String FILE2_2_NAME = "file2_2.";
    @InjectMocks
    private XppQualityFileSystem sut;
    @Mock
    private XppFormatFileSystem fileSystem;
    @Mock
    private BookStep step;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Map<String, Collection<File>> fileMap = new HashMap<>();
    private File file1;
    private File file2_1;
    private File file2_2;

    @Before
    public void setUp() throws IOException {
        final File folder1 = temporaryFolder.newFolder(MATERIAL_NUMBER_1);
        final File folder2 = temporaryFolder.newFolder(MATERIAL_NUMBER_2);

        file1 = new File(folder1, FILE1_NAME + "DIVXML.html");
        file2_1 = new File(folder2, FILE2_1_NAME + "DIVXML.html");
        file2_2 = new File(folder2, FILE2_2_NAME + "DIVXML.html");
        file1.createNewFile();
        file2_1.createNewFile();
        file2_2.createNewFile();

        fileMap.put(MATERIAL_NUMBER_1, singleton(file1));
        fileMap.put(MATERIAL_NUMBER_2, unmodifiableCollection(asList(file2_1, file2_2)));
    }

    @After
    public void cleanUp() {
        temporaryFolder.delete();
    }

    @Test
    public void shouldGetHtmlFileMap() {
        //given
        doReturn(fileMap).when(fileSystem)
                .getFiles(eq(step), eq(XppFormatFileSystemDir.UNESCAPE_DIR));
        //when
        final MultiKeyMap<String, Collection<File>> actualFileMap = sut.getHtmlFileMap(step);
        //then
        assertThat(file1, isIn(actualFileMap.get(MATERIAL_NUMBER_1, FILE1_NAME)));
        assertThat(file2_1, isIn(actualFileMap.get(MATERIAL_NUMBER_2, FILE2_1_NAME)));
        assertThat(file2_2, isIn(actualFileMap.get(MATERIAL_NUMBER_2, FILE2_2_NAME)));
    }
}
