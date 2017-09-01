package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeServiceImpl;
import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCode;
import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionServiceImpl;
import com.thomsonreuters.uscl.ereader.sap.comparsion.MaterialComponentComparatorProvider;
import com.thomsonreuters.uscl.ereader.sap.component.Material;
import com.thomsonreuters.uscl.ereader.sap.component.MaterialComponent;
import com.thomsonreuters.uscl.ereader.sap.component.MaterialComponentsResponse;
import com.thomsonreuters.uscl.ereader.sap.service.SapService;
import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.HttpStatusCodeException;

@RunWith(MockitoJUnitRunner.class)
public final class EditBookDefinitionServiceImplTest
{
    private static final String VALID_SUB_NUMBER = "12345678";
    private static final String EMPTY_SUB_NUMBER = "12345679";
    private static final String INVALID_SUB_NUMBER = "12345670";
    private static final String UNAVAILABLE_SUB_NUMBER = "1234567";

    private static final String TITLE_ID = "titleId";

    private EditBookDefinitionServiceImpl bookService;

    private CodeService mockCodeService;
    private StateCodeService mockStateCodeService;
    private File tempRootDir;
    @Mock
    private SapService sapService;
    @Mock
    private HttpStatusCodeException invalidParamException;
    @Mock
    private HttpStatusCodeException componentNotFoundException;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private MaterialComponentComparatorProvider materialComponentComparatorProvider;

    @Before
    public void setUp()
    {
        initMocks();
        bookService = new EditBookDefinitionServiceImpl();

        mockCodeService = EasyMock.createMock(CodeServiceImpl.class);
        mockStateCodeService = EasyMock.createMock(StateCodeService.class);
        tempRootDir = new File(System.getProperty("java.io.tmpdir") + "\\EvenMoreTemp");
        tempRootDir.mkdir();

        bookService.setCodeService(mockCodeService);
        bookService.setStateCodeService(mockStateCodeService);
        bookService.setRootCodesWorkbenchLandingStrip(tempRootDir);
        bookService.setSapService(sapService);
        bookService.setMaterialComponentComparatorProvider(materialComponentComparatorProvider);
    }

    private void initMocks()
    {
        when(sapService.getMaterialByNumber(VALID_SUB_NUMBER))
            .thenReturn(getMaterialComponents(VALID_SUB_NUMBER));
        when(sapService.getMaterialByNumber(EMPTY_SUB_NUMBER))
            .thenReturn(getMaterialComponents(EMPTY_SUB_NUMBER));

        when(invalidParamException.getResponseBodyAsString())
            .thenReturn("enter a valid");
        when(componentNotFoundException.getResponseBodyAsString())
            .thenReturn("not found");

        when(sapService.getMaterialByNumber(INVALID_SUB_NUMBER))
            .thenThrow(invalidParamException);
        when(sapService.getMaterialByNumber(UNAVAILABLE_SUB_NUMBER))
            .thenThrow(componentNotFoundException);

        when(materialComponentComparatorProvider.getComparator(TITLE_ID)
                .compare(any(MaterialComponent.class), any(MaterialComponent.class)))
            .thenReturn(0);
    }

    private Material getMaterialComponents(final String subNumber)
    {
        final List<MaterialComponent> materialComponents = new ArrayList<>();

        MaterialComponent component = new MaterialComponent();
        component.setDchainStatus("Z7");
        component.setMediahlRule("Print");
        component.setEffectiveDate(new Date(100));
        component.setBomComponent("1");
        materialComponents.add(component);

        component = new MaterialComponent();
        component.setMediahlRule("CDROM");
        component.setEffectiveDate(new Date(100));
        component.setBomComponent("2");
        materialComponents.add(component);

        component = new MaterialComponent();
        component.setMediahlRule("Print");
        component.setEffectiveDate(new Date(Long.MAX_VALUE));
        component.setBomComponent("3");
        materialComponents.add(component);

        if (VALID_SUB_NUMBER.equals(subNumber))
        {
            component = new MaterialComponent();
            component.setMediahlRule("Print");
            component.setEffectiveDate(new Date(100));
            component.setBomComponent("4");
            materialComponents.add(component);
        }

        final Material material = new Material();
        material.setMaterialNumber(subNumber);
        material.setComponents(materialComponents);
        return material;
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
        EasyMock.expect(mockStateCodeService.getAllStateCodes()).andReturn(codes);
        EasyMock.replay(mockStateCodeService);

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

    @Test
    public void shouldReturnListWithOneElement()
    {
        //given
        //when
        final MaterialComponentsResponse response = bookService.getMaterialBySubNumber(VALID_SUB_NUMBER, TITLE_ID);
        //then
        assertThat(response.getMessage(), equalTo("OK"));
        assertThat(response.getMaterialComponents(), hasSize(1));
        assertThat(response.getMaterialComponents().get(0).getBomComponent(), equalTo("4"));
    }

    @Test
    public void shouldReturnEmptyResponse()
    {
        //given
        //when
        final MaterialComponentsResponse response = bookService.getMaterialBySubNumber(EMPTY_SUB_NUMBER, TITLE_ID);
        //then
        assertThat(response.getMessage(), equalTo("Material components not found, for Print Set/Sub Number: 12345679"));
        assertThat(response.getMaterialComponents(), hasSize(0));
    }

    @Test
    public void shouldReturnInvalidResponse()
    {
        //given
        //when
        final MaterialComponentsResponse response = bookService.getMaterialBySubNumber(INVALID_SUB_NUMBER, TITLE_ID);
        //then
        assertThat(response.getMessage(), equalTo("Print Set/Sub Number: 12345670 is invalid"));
        assertThat(response.getMaterialComponents(), hasSize(0));
    }

    @Test
    public void shouldReturnUnavailableResponse()
    {
        //given
        //when
        final MaterialComponentsResponse response = bookService.getMaterialBySubNumber(UNAVAILABLE_SUB_NUMBER, TITLE_ID);
        //then
        assertThat(response.getMessage(), equalTo("Material components not found, for Print Set/Sub Number: 1234567"));
        assertThat(response.getMaterialComponents(), hasSize(0));
    }
}
