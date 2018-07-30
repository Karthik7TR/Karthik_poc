package com.thomsonreuters.uscl.ereader.xpp.transformation.toc.group;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import java.util.List;

import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class FileGroupHelperTest {
    private static final String ROOT = "1-TRG_CADEBT_6.DIVXML.xml";
    private static final String CHILD = "2-TRG_CADEBT_6_6A.DIVXML.xml";
    private static final String OTHER = "3-TRG_CADEBT_7.DIVXML.xml";
    private static final List<String> FILE_NAMES = asList(ROOT, CHILD, OTHER);

    private FileGroupHelper sut = new FileGroupHelper();
    @Mock
    private XppBundle bundle;

    @Before
    public void setup() {
        given(bundle.getOrderedFileList()).willReturn(FILE_NAMES);
    }

    @Test
    public void shouldCheckIfFileIsGroupPart() {
        //when
        final boolean groupPartRoot = sut.isGroupPart(ROOT, bundle);
        final boolean groupPartChild = sut.isGroupPart(CHILD, bundle);
        final boolean groupPartOther = sut.isGroupPart(OTHER, bundle);
        //then
        assertTrue(groupPartRoot);
        assertTrue(groupPartChild);
        assertFalse(groupPartOther);
    }

    @Test
    public void shouldCheckIfFileIsGroupRoot() {
        //when
        final boolean groupRoot = sut.isGroupRoot(ROOT, bundle);
        final boolean groupRootChild = sut.isGroupRoot(CHILD, bundle);
        final boolean groupRootOther = sut.isGroupRoot(OTHER, bundle);
        //then
        assertTrue(groupRoot);
        assertFalse(groupRootChild);
        assertFalse(groupRootOther);
    }

    @Test
    public void shouldGetGroupFileNames() {
        //when
        final List<String> rootFiles = sut.getGroupFileNames(ROOT, bundle);
        final List<String> childFiles = sut.getGroupFileNames(CHILD, bundle);
        final List<String> otherFiles = sut.getGroupFileNames(OTHER, bundle);
        //then
        assertEquals(rootFiles, childFiles);
        assertEquals(singletonList(OTHER), otherFiles);
    }
}
