package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeServiceImpl;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionServiceImpl;
import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class EditBookDefinitionServiceImplTest
{
    private EditBookDefinitionServiceImpl bookService;

    private CodeService mockCodeService;
    private File tempRootDir;

    @Before
    public void setUp()
    {
        bookService = new EditBookDefinitionServiceImpl();

        mockCodeService = EasyMock.createMock(CodeServiceImpl.class);
        tempRootDir = new File(System.getProperty("java.io.tmpdir") + "\\EvenMoreTemp");
        tempRootDir.mkdir();

        bookService.setCodeService(mockCodeService);
        bookService.setRootCodesWorkbenchLandingStrip(tempRootDir);
    }

    @After
    public void tearDown() throws Exception
    {
        /*
         * recursively deletes the root directory, and all its subdirectories
         * and files
         */
        FileUtils.deleteDirectory(tempRootDir);
    }

    @Test
    public void testGetStates()
    {
        final StateCode code = new StateCode();
        code.setName("aAa");
        final List<StateCode> codes = new ArrayList<>();
        codes.add(code);
        EasyMock.expect(mockCodeService.getAllStateCodes()).andReturn(codes);
        EasyMock.replay(mockCodeService);

        final Map<String, String> states = bookService.getStates();
        Assert.assertNotNull(states);
        Assert.assertEquals(1, states.size());
    }

    @Test
    public void testGetJurisdictions()
    {
        final JurisTypeCode code = new JurisTypeCode();
        code.setName("aAa");
        final List<JurisTypeCode> codes = new ArrayList<>();
        codes.add(code);
        EasyMock.expect(mockCodeService.getAllJurisTypeCodes()).andReturn(codes);
        EasyMock.replay(mockCodeService);

        final Map<String, String> juris = bookService.getJurisdictions();
        Assert.assertNotNull(juris);
        Assert.assertEquals(1, juris.size());
    }

    @Test
    public void testGetPubTypes()
    {
        final PubTypeCode code = new PubTypeCode();
        code.setName("aAa");
        final List<PubTypeCode> codes = new ArrayList<>();
        codes.add(code);
        EasyMock.expect(mockCodeService.getAllPubTypeCodes()).andReturn(codes);
        EasyMock.replay(mockCodeService);

        final Map<String, String> pubType = bookService.getPubTypes();
        Assert.assertNotNull(pubType);
        Assert.assertEquals(1, pubType.size());
    }

    @Test
    public void testGetPublishers()
    {
        final PublisherCode code = new PublisherCode();
        code.setName("aAa");
        final List<PublisherCode> codes = new ArrayList<>();
        codes.add(code);
        EasyMock.expect(mockCodeService.getAllPublisherCodes()).andReturn(codes);
        EasyMock.replay(mockCodeService);

        final Map<String, String> pubs = bookService.getPublishers();
        Assert.assertNotNull(pubs);
        Assert.assertEquals(1, pubs.size());
    }

    @Test
    public void testGetCodesWorkbenchDirectory() throws IOException
    {
        final File folder = new File(tempRootDir, "folder");
        folder.mkdir();
        final File file = new File(folder, "file");
        file.createNewFile();

        List<String> fileList = bookService.getCodesWorkbenchDirectory(folder.getName());
        Assert.assertEquals(1, fileList.size());

        fileList = bookService.getCodesWorkbenchDirectory("");
        Assert.assertEquals(1, fileList.size());
    }
}
