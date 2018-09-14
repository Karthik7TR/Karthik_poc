package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.common.filesystem.GatherFileSystem;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class BundlePdfsServiceTest {
    private static final Long JOB_INSTANCE_ID = 12551L;
    private static final Long WRONG_JOB_INSTANCE_ID = 15551L;
    private static final Long BOOK_DEFINITION_ID = 12345L;
    private static final String MATERIAL_NUMBER = "41894002";
    private static final String TITLE_ID = "titleId";
    private static final String PDF_NAME = "70007-volume_7_Table_of_LRRE.pdf";

    @InjectMocks
    private BundlePdfsService bundlePdfsService;

    @Mock
    private GatherFileSystem gatherFileSystem;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Captor
    private ArgumentCaptor<ZipEntry> captor;

    @Before
    public void setUp() {
        when(gatherFileSystem.getGatherRootDirectory(JOB_INSTANCE_ID))
            .thenReturn(new File(String.format("\\data\\20180101\\%s\\%s\\Gather", TITLE_ID, JOB_INSTANCE_ID)));
    }

    @Test
    public void shouldGetMaterialNumberDir() {
        final File result = bundlePdfsService.getMaterialNumberDir(JOB_INSTANCE_ID.toString(), MATERIAL_NUMBER);
        assertTrue(result.getPath().contains(String.format("\\data\\20180101\\%s\\%s\\Gather\\Bundles\\%s", TITLE_ID, JOB_INSTANCE_ID, MATERIAL_NUMBER)));
    }

    @Test
    public void shouldAddFilesToZip() throws IOException, URISyntaxException {
        //given
        final File materialNumberDir = new File(tempFolder.getRoot(), MATERIAL_NUMBER);
        final File pdfDir = new File(materialNumberDir, "BookName/PDF");
        pdfDir.mkdirs();

        final File pdfFile = new File(BundlePdfsServiceTest.class.getResource("pdf/" + PDF_NAME).toURI());
        FileUtils.copyFileToDirectory(pdfFile, pdfDir);

        final ZipOutputStream zout = Mockito.mock(ZipOutputStream.class);

        //when
        bundlePdfsService.addFilesToZip(materialNumberDir, zout);

        //then
        then(zout).should().putNextEntry(captor.capture());
        assertEquals(PDF_NAME, captor.getValue().getName());
    }

    @Test(expected = EBookException.class)
    public void shouldThrowExceptionIfNoBookNameDir() throws IOException {
        //given
        final File materialNumberDir = new File(tempFolder.getRoot(), MATERIAL_NUMBER);
        materialNumberDir.mkdirs();

        //when
        bundlePdfsService.addFilesToZip(materialNumberDir, Mockito.mock(ZipOutputStream.class));
    }

    @Test(expected = EBookException.class)
    public void shouldThrowExceptionIfNoPDFDir() throws IOException {
        //given
        final File materialNumberDir = new File(tempFolder.getRoot(), MATERIAL_NUMBER);
        final File pdfDir = new File(materialNumberDir, "BookName");
        pdfDir.mkdirs();

        //when
        bundlePdfsService.addFilesToZip(materialNumberDir, Mockito.mock(ZipOutputStream.class));
    }
}
