package com.thomsonreuters.uscl.ereader.common.group.service;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class SplitNodesInfoServiceImplTest
{
    @InjectMocks
    private SplitNodesInfoServiceImpl service;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private File splitNodeInfoFile;
    private File incorrectFile;

    @Before
    public void setUp() throws URISyntaxException
    {
        splitNodeInfoFile = new File(SplitNodesInfoServiceImplTest.class.getResource("splitNodeInfo.txt").toURI());
        incorrectFile = new File("splitNodeInfoIncorrect.txt");
    }

    @Test
    public void shouldReturnListOfTitles()
    {
        //given
        final String fullyQualifiedTitleId = "uscl/an/split_splitpro";
        //when
        final List<String> list = service.readSplitNodeInforFile(splitNodeInfoFile, fullyQualifiedTitleId);
        //then
        assertThat(list, contains("uscl/an/split_splitpro", "uscl/an/split_splitpro_pt2"));
    }

    @Test
    public void shouldThrowExceptionIfFileNotFound()
    {
        //given
        thrown.expect(RuntimeException.class);
        final String fullyQualifiedTitleId = "uscl/an/split_splitpro";
        //when
        service.readSplitNodeInforFile(incorrectFile, fullyQualifiedTitleId);
        //then
    }
}
