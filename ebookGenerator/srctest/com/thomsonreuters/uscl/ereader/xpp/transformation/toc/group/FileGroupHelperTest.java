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
    private static final String ROOT = "1-TRG_EALIT_1.DIVXML.xml";
    private static final String CHILD = "2-TRG_EALIT_1_1A.DIVXML.xml";
    private static final String CHILD2 = "9-TRG_EALIT_1_FRMBLK_2.DIVXML.xml";
    private static final String OTHER = "3-TRG_EALIT_3.DIVXML.xml";
    private static final String OTHER2 = "10-TRG_EALIT_10.DIVXML.xml";
    private static final String OTHER3 = "0-TRG_EALIT_Front_vol_1.DIVXML.xml";
    private static final List<String> FILE_NAMES = asList(ROOT, CHILD, CHILD2, OTHER, OTHER2, OTHER3);

    private FileGroupHelper sut = new FileGroupHelper();
    @Mock
    private XppBundle bundle;
    @Mock
    private XppBundle bundleWithoutGroup;

    @Before
    public void setup() {
        given(bundle.getOrderedFileList()).willReturn(FILE_NAMES);
        given(bundleWithoutGroup.getOrderedFileList()).willReturn(asList(ROOT, OTHER2));
    }

    @Test
    public void shouldCheckIfFileIsGroupPart() {
        //when
        final boolean groupPartRoot = sut.isGroupPart(ROOT, bundle);
        final boolean groupPartChild = sut.isGroupPart(CHILD, bundle);
        final boolean groupPartChild2 = sut.isGroupPart(CHILD2, bundle);
        final boolean groupPartOther = sut.isGroupPart(OTHER, bundle);
        final boolean groupPartOther2 = sut.isGroupPart(OTHER2, bundle);
        final boolean groupPartOther3 = sut.isGroupPart(OTHER3, bundle);
        //then
        assertTrue(groupPartRoot);
        assertTrue(groupPartChild);
        assertTrue(groupPartChild2);
        assertFalse(groupPartOther);
        assertFalse(groupPartOther2);
        assertFalse(groupPartOther3);
    }

    @Test
    public void shouldCheckIfFileIsGroupRoot() {
        //when
        final boolean groupRoot = sut.isGroupRoot(ROOT, bundle);
        final boolean groupRootChild = sut.isGroupRoot(CHILD, bundle);
        final boolean groupRootChild2 = sut.isGroupRoot(CHILD2, bundle);
        final boolean groupRootOther = sut.isGroupRoot(OTHER, bundle);
        final boolean notGroupRoot = sut.isGroupRoot(ROOT, bundleWithoutGroup);
        //then
        assertTrue(groupRoot);
        assertFalse(groupRootChild);
        assertFalse(groupRootChild2);
        assertFalse(groupRootOther);
        assertFalse(notGroupRoot);
    }

    @Test
    public void shouldGetGroupFileNames() {
        //when
        final List<String> rootFiles = sut.getGroupFileNames(ROOT, bundle);
        final List<String> childFiles = sut.getGroupFileNames(CHILD, bundle);
        final List<String> childFiles2 = sut.getGroupFileNames(CHILD2, bundle);
        final List<String> otherFiles = sut.getGroupFileNames(OTHER, bundle);
        //then
        assertTrue(rootFiles.contains(ROOT));
        assertTrue(rootFiles.contains(CHILD));
        assertTrue(rootFiles.contains(CHILD2));
        assertEquals(rootFiles, childFiles);
        assertEquals(rootFiles, childFiles2);
        assertEquals(singletonList(OTHER), otherFiles);
    }
}
