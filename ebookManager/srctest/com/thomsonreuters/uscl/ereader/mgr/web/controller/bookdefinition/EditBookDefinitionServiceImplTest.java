package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.DocumentTypeCodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.JurisTypeCodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.KeywordTypeCodeSevice;
import com.thomsonreuters.uscl.ereader.core.book.service.PublisherCodeService;
import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCode;
import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionServiceImpl;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.sap.comparsion.MaterialComponentComparatorProvider;
import com.thomsonreuters.uscl.ereader.sap.component.Material;
import com.thomsonreuters.uscl.ereader.sap.component.MaterialComponent;
import com.thomsonreuters.uscl.ereader.sap.component.MaterialComponentsResponse;
import com.thomsonreuters.uscl.ereader.sap.service.SapService;
import org.apache.commons.io.FileUtils;
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
public final class EditBookDefinitionServiceImplTest {
    private static final String VALID_SUB_NUMBER = "12345678";
    private static final String EMPTY_SUB_NUMBER = "12345679";
    private static final String INVALID_SUB_NUMBER = "12345670";
    private static final String UNAVAILABLE_SUB_NUMBER = "1234567";
    private static final String TITLE_ID = "titleId";
    private static final String USCL_PUBLISHER_NAME = "uscl";
    private static final String CW_PUBLISHER_NAME = "cw";
    private static final String ANALYTICAL_DOCUMENT_TYPE_NAME = "Analytical";
    private static final String ONE_TIME_SALE_DOCUMENT_TYPE_NAME = "One Time Sale";

    private EditBookDefinitionServiceImpl bookService;
    private File tempRootDir;
    @Mock
    private CodeService codeService;
    @Mock
    private KeywordTypeCodeSevice keywordTypeCodeSevice;
    @Mock
    private PublisherCodeService publisherCodeService;
    @Mock
    private DocumentTypeCodeService documentTypeCodeService;
    @Mock
    private JurisTypeCodeService jurisTypeCodeService;
    @Mock
    private StateCodeService stateCodeService;
    @Mock
    private SapService sapService;
    @Mock
    private HttpStatusCodeException invalidParamException;
    @Mock
    private HttpStatusCodeException componentNotFoundException;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private MaterialComponentComparatorProvider materialComponentComparatorProvider;

    @Before
    public void setUp() {
        initMocks();

        tempRootDir = new File(System.getProperty("java.io.tmpdir") + "\\EvenMoreTemp");
        tempRootDir.mkdir();

        bookService = new EditBookDefinitionServiceImpl(
            codeService,
            keywordTypeCodeSevice,
            publisherCodeService,
            documentTypeCodeService,
            jurisTypeCodeService,
            stateCodeService,
            tempRootDir,
            sapService,
            materialComponentComparatorProvider);
    }

    private void initMocks() {
        when(sapService.getMaterialByNumber(VALID_SUB_NUMBER)).thenReturn(getMaterialComponents(VALID_SUB_NUMBER));
        when(sapService.getMaterialByNumber(EMPTY_SUB_NUMBER)).thenReturn(getMaterialComponents(EMPTY_SUB_NUMBER));

        when(invalidParamException.getResponseBodyAsString()).thenReturn("enter a valid");
        when(componentNotFoundException.getResponseBodyAsString()).thenReturn("not found");

        when(sapService.getMaterialByNumber(INVALID_SUB_NUMBER)).thenThrow(invalidParamException);
        when(sapService.getMaterialByNumber(UNAVAILABLE_SUB_NUMBER)).thenThrow(componentNotFoundException);

        when(
            materialComponentComparatorProvider.getComparator(TITLE_ID)
                .compare(any(MaterialComponent.class), any(MaterialComponent.class))).thenReturn(0);
    }

    private Material getMaterialComponents(final String subNumber) {
        final List<MaterialComponent> materialComponents = new ArrayList<>();

        MaterialComponent component = new MaterialComponent();
        component.setDchainStatus("Z7");
        component.setMediahlRule("Print");
        component.setMediallRule("30-blah");
        component.setEffectiveDate(new Date(100));
        component.setBomComponent("1");
        component.setProdDate(new Date());
        materialComponents.add(component);

        component = new MaterialComponent();
        component.setMediahlRule("CDROM");
        component.setMediallRule("30-blah");
        component.setEffectiveDate(new Date(100));
        component.setBomComponent("2");
        component.setProdDate(new Date());
        materialComponents.add(component);

        component = new MaterialComponent();
        component.setMediahlRule("Print");
        component.setMediallRule("30-blah");
        component.setEffectiveDate(new Date(Long.MAX_VALUE));
        component.setBomComponent("3");
        component.setProdDate(new Date());
        materialComponents.add(component);

        component = new MaterialComponent();
        component.setDchainStatus("Z7");
        component.setMediahlRule("Print");
        component.setMediallRule("66-blah");
        component.setEffectiveDate(new Date(100));
        component.setBomComponent("5");
        component.setProdDate(new Date());
        materialComponents.add(component);

        if (VALID_SUB_NUMBER.equals(subNumber)) {
            component = new MaterialComponent();
            component.setMediahlRule("Print");
            component.setMediallRule("34-blah");
            component.setEffectiveDate(new Date(100));
            component.setBomComponent("4");
            component.setProdDate(new Date());
            materialComponents.add(component);
        }

        component = new MaterialComponent();
        component.setMediahlRule("Print");
        component.setMediallRule("30-blah");
        component.setEffectiveDate(new Date(100));
        component.setBomComponent("6");
        materialComponents.add(component);

        component = new MaterialComponent();
        component.setMediahlRule("Print");
        component.setMediallRule("34-blah");
        component.setEffectiveDate(new Date(100));
        component.setBomComponent("7");
        component.setMaterialType("ZFNV");
        materialComponents.add(component);

        final Material material = new Material();
        material.setMaterialNumber(subNumber);
        material.setComponents(materialComponents);
        return material;
    }

    @After
    public void tearDown() throws Exception {
        /*
         * recursively deletes the root directory, and all its subdirectories
         * and files
         */
        FileUtils.deleteDirectory(tempRootDir);
    }

    @Test
    public void testGetStates() {
        // given
        final StateCode code = new StateCode();
        code.setName("aAa");
        final List<StateCode> codes = new ArrayList<>();
        codes.add(code);
        given(stateCodeService.getAllStateCodes()).willReturn(codes);
        // when
        final Map<String, String> states = bookService.getStates();
        // then
        Assert.assertNotNull(states);
        assertEquals(1, states.size());
    }

    @Test
    public void testGetJurisdictions() {
        // given
        final JurisTypeCode code = new JurisTypeCode();
        code.setName("aAa");
        final List<JurisTypeCode> codes = new ArrayList<>();
        codes.add(code);
        given(jurisTypeCodeService.getAllJurisTypeCodes()).willReturn(codes);
        // when
        final Map<String, String> juris = bookService.getJurisdictions();
        // then
        Assert.assertNotNull(juris);
        assertEquals(1, juris.size());
    }

    @Test
    public void testGetPubTypes() {
        // given
        final PubTypeCode code = new PubTypeCode();
        code.setName("aAa");
        final List<PubTypeCode> codes = new ArrayList<>();
        codes.add(code);
        given(codeService.getAllPubTypeCodes()).willReturn(codes);
        // when
        final Map<String, String> pubType = bookService.getPubTypes();
        // then
        Assert.assertNotNull(pubType);
        assertEquals(1, pubType.size());
    }

    @Test
    public void testGetPublishers() {
        // given
        final PublisherCode code = new PublisherCode();
        code.setName("aAa");
        final List<PublisherCode> codes = new ArrayList<>();
        codes.add(code);
        given(publisherCodeService.getAllPublisherCodes()).willReturn(codes);
        // when
        final Map<String, String> pubs = bookService.getPublishers();
        // then
        Assert.assertNotNull(pubs);
        assertEquals(1, pubs.size());
    }

    @Test
    public void testGetDocumentTypesByPublishers() {
        List<PublisherCode> publishers = setUpPublishers();
        when(publisherCodeService.getAllPublisherCodes()).thenReturn(publishers);

        Map<String, List<DocumentTypeCode>> documentTypes = bookService.getDocumentTypesByPublishers();

        checkUsclDocumentTypes(documentTypes.get(USCL_PUBLISHER_NAME));
        checkCwDocumentTypes(documentTypes.get(CW_PUBLISHER_NAME));
    }

    @Test
    public void testGetCodesWorkbenchDirectory() throws IOException {
        final File folder = new File(tempRootDir, "folder");
        folder.mkdir();
        final File file = new File(folder, "file");
        file.createNewFile();

        List<String> fileList = bookService.getCodesWorkbenchDirectory(folder.getName());
        assertEquals(1, fileList.size());

        fileList = bookService.getCodesWorkbenchDirectory("");
        assertEquals(1, fileList.size());
    }

    @Test
    public void shouldReturnListWithOneElement() {
        //given
        //when
        final MaterialComponentsResponse response =
            bookService.getMaterialBySubNumber(VALID_SUB_NUMBER, VALID_SUB_NUMBER, TITLE_ID);
        //then
        assertThat(response.getMessage(), equalTo("OK"));
        assertThat(response.getMaterialComponents(), hasSize(1));
        assertThat(response.getMaterialComponents().get(0).getBomComponent(), equalTo("4"));
    }

    @Test
    public void shouldReturnEmptyResponse() {
        //given
        //when
        final MaterialComponentsResponse response =
            bookService.getMaterialBySubNumber(EMPTY_SUB_NUMBER, EMPTY_SUB_NUMBER, TITLE_ID);
        //then
        assertThat(response.getMessage(), equalTo("Material components not found, for Print Set/Sub Number: 12345679/12345679"));
        assertThat(response.getMaterialComponents(), hasSize(0));
    }

    @Test
    public void shouldReturnInvalidResponse() {
        //given
        //when
        final MaterialComponentsResponse response =
            bookService.getMaterialBySubNumber(INVALID_SUB_NUMBER, INVALID_SUB_NUMBER, TITLE_ID);
        //then
        assertThat(response.getMessage(), equalTo("Invalid Set/Sub Number: 12345670/12345670"));
        assertThat(response.getMaterialComponents(), hasSize(0));
    }

    @Test
    public void shouldReturnUnavailableResponse() {
        //given
        //when
        final MaterialComponentsResponse response =
            bookService.getMaterialBySubNumber(UNAVAILABLE_SUB_NUMBER, UNAVAILABLE_SUB_NUMBER, TITLE_ID);
        //then
        assertThat(response.getMessage(), equalTo("Material components not found, for Print Set/Sub Number: 1234567/1234567"));
        assertThat(response.getMaterialComponents(), hasSize(0));
    }

    @Test
    public void shouldReturnValuesBySetNumberResponse() {
        //given
        //when
        final MaterialComponentsResponse response =
            bookService.getMaterialBySubNumber(EMPTY_SUB_NUMBER, VALID_SUB_NUMBER, TITLE_ID);
        //then
        assertThat(response.getMessage(), equalTo("OK"));
        assertThat(response.getMaterialComponents(), hasSize(1));
        assertThat(response.getMaterialComponents().get(0).getBomComponent(), equalTo("4"));
    }

    @Test
    public void shouldReturnValuesBySubNumberResponse() {
        //given
        //when
        final MaterialComponentsResponse response =
            bookService.getMaterialBySubNumber(VALID_SUB_NUMBER, EMPTY_SUB_NUMBER, TITLE_ID);
        //then
        assertThat(response.getMessage(), equalTo("OK"));
        assertThat(response.getMaterialComponents(), hasSize(1));
        assertThat(response.getMaterialComponents().get(0).getBomComponent(), equalTo("4"));
    }

    private List<PublisherCode> setUpPublishers() {
        final List<PublisherCode> publishers = new ArrayList<>();
        publishers.add(getPublisherUscl());
        publishers.add(getPublisherCw());
        return publishers;
    }

    private PublisherCode getPublisherUscl() {
        final PublisherCode uscl = new PublisherCode();
        uscl.setName(USCL_PUBLISHER_NAME);
        uscl.setDocumentTypeCodes(getUsclDocumentTypeCodes(uscl));
        return uscl;
    }

    private List<DocumentTypeCode> getUsclDocumentTypeCodes(final PublisherCode uscl) {
        DocumentTypeCode usclDocumentType = new DocumentTypeCode();
        usclDocumentType.setName(ANALYTICAL_DOCUMENT_TYPE_NAME);
        usclDocumentType.setPublisherCode(uscl);
        return Collections.singletonList(usclDocumentType);
    }

    private PublisherCode getPublisherCw() {
        final PublisherCode cw = new PublisherCode();
        cw.setName(CW_PUBLISHER_NAME);
        cw.setDocumentTypeCodes(getCwDocumentTypeCodes(cw));
        return cw;
    }

    private List<DocumentTypeCode> getCwDocumentTypeCodes(final PublisherCode cw) {
        DocumentTypeCode cwDocumentType = new DocumentTypeCode();
        cwDocumentType.setName(ONE_TIME_SALE_DOCUMENT_TYPE_NAME);
        cwDocumentType.setPublisherCode(cw);
        return Collections.singletonList(cwDocumentType);
    }

    private void checkUsclDocumentTypes(final List<DocumentTypeCode> documentTypes) {
        DocumentTypeCode usclDocumentType = documentTypes.get(0);
        assertNotNull(usclDocumentType);
        assertEquals(usclDocumentType.getName(), ANALYTICAL_DOCUMENT_TYPE_NAME);
    }

    private void checkCwDocumentTypes(final List<DocumentTypeCode> documentTypes) {
        DocumentTypeCode cwDocumentType = documentTypes.get(0);
        assertNotNull(cwDocumentType);
        assertEquals(cwDocumentType.getName(), ONE_TIME_SALE_DOCUMENT_TYPE_NAME);
    }
}
