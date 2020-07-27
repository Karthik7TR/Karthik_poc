package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.domain.NortFileLocation;
import com.thomsonreuters.uscl.ereader.core.book.domain.PilotBook;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.TableViewer;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.DocumentTypeCodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.KeywordTypeCodeSevice;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionFormValidator;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public final class EditBookDefinitionFormValidatorTest {
    private static final String CW = "cw";
    private static final String USCL = "uscl";
    private static final long PUBLISHER_KEYWORD_ID_CW = 1L;
    private static final long PUBLISHER_KEYWORD_ID_USCL = 2L;
    private static final long JURISDICTION_KEYWORD_ID_CW = 3L;
    private static final long JURISDICTION_KEYWORD_ID_USCL = 4L;
    private static final long SOME_ADDITIONAL_KEYWORD_ID = 5L;
    private static final long SUBJECT_KEYWORD_ID_USCL = 6L;
    private static final long SUBJECT_KEYWORD_ID_CW = 7L;
    private static final String KEYWORDS_ERROR_PATTERN = "keywords[%d]";
    private static final String SUBJECT_KEYWORD = "subject";
    private static final String JURISDICTION_KEYWORD = "jurisdiction";
    private static final String PUBLISHER_KEYWORD = "publisher";
    private static final String SOME_ADDITIONAL_KEYWORD = "some additional keyword";
    private static final String PUBLISHED_DATE = "publishedDate";
    private static final String WRONG_DATE_PATTERN = "dd-mm-yyyy";
    private static final String SUBJECT_KEYWORD_ERROR = "error.keyword.max.subjecs.number.exceeded";
    private static final String WRONG_DATE_FORMAT_ERROR = "error.date.format";
    private static final String REQUIRED_ERROR = "error.required";
    private static final Map<String, String> TITLE_ID_BY_PUBLISHER = new HashMap<>();

    private List<KeywordTypeCode> KEYWORD_CODES;

    @Autowired
    private BookDefinitionService mockBookDefinitionService;
    @Autowired
    private KeywordTypeCodeSevice keywordTypeCodeSevice;
    @Autowired
    private DocumentTypeCodeService mockDocumentTypeCodeService;
    private EditBookDefinitionForm form;
    @Autowired
    private EditBookDefinitionFormValidator validator;
    private Errors errors;

    private DocumentTypeCode analyticalCode;

    @BeforeClass
    public static void setUpClass() {
        TITLE_ID_BY_PUBLISHER.put(CW, "cw/eg/title_en");
        TITLE_ID_BY_PUBLISHER.put(USCL, "uscl/an/title_title");
    }
    @Before
    public void setUp() {
        form = new EditBookDefinitionForm();

        errors = new BindException(form, "form");

        analyticalCode = new DocumentTypeCode();
        analyticalCode.setId(Long.parseLong("1"));
        analyticalCode.setAbbreviation(WebConstants.DOCUMENT_TYPE_ANALYTICAL_ABBR);
        analyticalCode.setName(WebConstants.DOCUMENT_TYPE_ANALYTICAL);

        KEYWORD_CODES = new ArrayList<>();
        KEYWORD_CODES.add(getKeywordTypeCode(PUBLISHER_KEYWORD + " " + CW, true, PUBLISHER_KEYWORD_ID_CW, "Carswell"));
        KEYWORD_CODES.add(getKeywordTypeCode(PUBLISHER_KEYWORD + " " + USCL, true, PUBLISHER_KEYWORD_ID_USCL, "Thomson Reuters"));
        KEYWORD_CODES.add(getKeywordTypeCode(JURISDICTION_KEYWORD + " " + CW, true, JURISDICTION_KEYWORD_ID_CW, "Canada"));
        KEYWORD_CODES.add(getKeywordTypeCode(JURISDICTION_KEYWORD + " " + USCL, true, JURISDICTION_KEYWORD_ID_USCL, "Alaska"));
        KEYWORD_CODES.add(getKeywordTypeCode(SOME_ADDITIONAL_KEYWORD, false, SOME_ADDITIONAL_KEYWORD_ID, "Some value"));
        KEYWORD_CODES.add(getKeywordTypeCode(SUBJECT_KEYWORD + " " + USCL, false, SUBJECT_KEYWORD_ID_USCL, "Banking and Finance Law", "Civil Procedure", "Motor Vehicles", "Aboriginal"));
        KEYWORD_CODES.add(getKeywordTypeCode(SUBJECT_KEYWORD + " " + CW, false, SUBJECT_KEYWORD_ID_CW, "Aboriginal", "Constitutional", "Criminal", "Immigration"));

        EasyMock.reset(mockBookDefinitionService, mockDocumentTypeCodeService, keywordTypeCodeSevice);
    }

    /**
     * Test No data set on form
     */
    @Test
    public void testNoTitleId() {
        // verify errors
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("publisher").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("titleId").getCode());
        Assert.assertEquals("mesg.errors.form", errors.getFieldError("validateForm").getCode());
        Assert.assertEquals(8, errors.getAllErrors().size());
    }

    /**
     * Test No data set on form
     */
    @Test
    public void testNoContentType() {
        form.setTitleId(USCL);
        form.setPublisher(USCL);
        // verify errors
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("pubInfo").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("contentTypeId").getCode());
        Assert.assertEquals("mesg.errors.form", errors.getFieldError("validateForm").getCode());
        Assert.assertEquals(4, errors.getAllErrors().size());
    }

    @Test
    public void testNoGroups() {
        form.setGroupsEnabled(false);
        // verify errors
        validator.validate(form, errors);
        Assert.assertEquals("mesg.errors.form", errors.getFieldError("validateForm").getCode());
        Assert.assertEquals(7, errors.getAllErrors().size());
    }

    @Test
    public void testGroupName() {
        form.setGroupsEnabled(true);
        // verify errors
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("groupName").getCode());
        Assert.assertEquals("mesg.errors.form", errors.getFieldError("validateForm").getCode());
        Assert.assertEquals(8, errors.getAllErrors().size());
    }

    @Test
    public void testSubGroupRequiredForSplit() {
        form.setSplitBook(true);
        form.setGroupsEnabled(true);
        // verify errors
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("groupName").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("subGroupHeading").getCode());
        Assert.assertEquals("mesg.errors.form", errors.getFieldError("validateForm").getCode());
        Assert.assertEquals(9, errors.getAllErrors().size());
    }

    @Test
    public void testNullPrintSubNumber() {
        form.setIsComplete(true);
        form.setSourceType(SourceType.XPP);

        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(KEYWORD_CODES);
        EasyMock.replay(keywordTypeCodeSevice);
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("printSubNumber").getCode());
    }

    @Test
    public void testPrintSetNumber() {
        form.setIsComplete(true);
        form.setSourceType(SourceType.XPP);
        form.setPrintSetNumber("abcd");

        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(KEYWORD_CODES);
        EasyMock.replay(keywordTypeCodeSevice);
        validator.validate(form, errors);
        Assert.assertEquals("mesg.errors.form", errors.getFieldError("validateForm").getCode());
    }

    @Test
    public void testPrintSubNumber() {
        form.setIsComplete(true);
        form.setSourceType(SourceType.XPP);
        form.setPrintSubNumber("abcd");

        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(KEYWORD_CODES);
        EasyMock.replay(keywordTypeCodeSevice);
        validator.validate(form, errors);
        Assert.assertEquals("mesg.errors.form", errors.getFieldError("validateForm").getCode());
    }

    @Test
    public void testFileExist() throws Exception {
        form.setIsComplete(true);
        form.setSourceType(SourceType.FILE);
        EasyMock.expect(mockDocumentTypeCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class)))
            .andReturn(analyticalCode);
        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(KEYWORD_CODES);
        EasyMock.replay(keywordTypeCodeSevice);
        form.setCodesWorkbenchBookName("/FamEbook20");
        validator.validate(form, errors);

        Assert.assertEquals("error.not.exist", errors.getFieldError("codesWorkbenchBookName").getCode());
        Assert.assertEquals(15, errors.getAllErrors().size());
    }

    /**
     * Test Analytical Title Validation
     */
    @Test
    public void testAnalyticalTitleId() {
        setupPublisherAndTitleId("uscl/an/abcd", analyticalCode, 4, 2);

        // Check Valid Analytical Title
        validator.validate(form, errors);
        Assert.assertFalse(errors.hasErrors());

        // Verify Analytical content type requirements
        form.setPubAbbr(null);
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("pubAbbr").getCode());

        form.setContentTypeId(null);
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("pubAbbr").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("contentTypeId").getCode());

        form.setPublisher(null);
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("publisher").getCode());

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(mockDocumentTypeCodeService);
    }

    /**
     * Test Court Rule Title Validation
     */
    @Test
    public void testCourtRuleTitleId() {
        final DocumentTypeCode courtRulesCode = new DocumentTypeCode();
        courtRulesCode.setId(Long.parseLong("1"));
        courtRulesCode.setAbbreviation(WebConstants.DOCUMENT_TYPE_COURT_RULES_ABBR);
        courtRulesCode.setName(WebConstants.DOCUMENT_TYPE_COURT_RULES);

        setupPublisherAndTitleId("uscl/cr/tx_state", courtRulesCode, 4, 2);

        // Check Valid Analytical Title
        validator.validate(form, errors);
        Assert.assertFalse(errors.hasErrors());

        // Verify Analytical content type requirements
        form.setState(null);
        form.setPubType(null);
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("state").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("pubType").getCode());

        form.setContentTypeId(null);
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("state").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("pubType").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("contentTypeId").getCode());

        form.setPublisher(null);
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("publisher").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test Slice Code Title Validation
     */
    @Test
    public void testSliceCodeTitleId() {
        final DocumentTypeCode sliceCodesCode = new DocumentTypeCode();
        sliceCodesCode.setId(Long.parseLong("1"));
        sliceCodesCode.setAbbreviation(WebConstants.DOCUMENT_TYPE_SLICE_CODES_ABBR);
        sliceCodesCode.setName(WebConstants.DOCUMENT_TYPE_SLICE_CODES);

        setupPublisherAndTitleId("uscl/sc/us_abcd", sliceCodesCode, 4, 2);

        // Check Valid Analytical Title
        validator.validate(form, errors);
        Assert.assertFalse(errors.hasErrors());

        // Verify Analytical content type requirements
        form.setJurisdiction(null);
        form.setPubInfo(null);
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("jurisdiction").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("pubInfo").getCode());

        form.setContentTypeId(null);
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("pubInfo").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("contentTypeId").getCode());

        form.setPublisher(null);
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("publisher").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test publisher Abbreviation and publisher information
     * No special characters
     */
    @Test
    public void testSpecialCharacters() {
        setupPublisherAndTitleId("uscl/an/abcd", analyticalCode, 2, 2);

        // Check Valid Analytical Title
        validator.validate(form, errors);
        Assert.assertFalse(errors.hasErrors());

        // Verify No special characters
        form.setPubAbbr("_");
        form.setPubInfo("!@");
        validator.validate(form, errors);
        Assert.assertEquals("error.alphanumeric", errors.getFieldError("pubAbbr").getCode());
        Assert.assertEquals("error.alphanumeric.underscore", errors.getFieldError("pubInfo").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test publisher Abbreviation and publisher information
     * No spaces
     */
    @Test
    public void testNoSpaces() {
        setupPublisherAndTitleId("uscl/an/abcd", analyticalCode, 2, 2);

        // Check Valid Analytical Title
        validator.validate(form, errors);
        Assert.assertFalse(errors.hasErrors());

        // Verify No spaces
        form.setPubAbbr("abc abc");
        form.setPubInfo("abc abc");
        validator.validate(form, errors);
        Assert.assertEquals("error.no.spaces", errors.getFieldError("pubAbbr").getCode());
        Assert.assertEquals("error.no.spaces", errors.getFieldError("pubInfo").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test unique Title Id when creating new Book Definition
     */
    @Test
    public void testUniquTitleIdWhenCreating() {
        expectReplayDocTypeCode();
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        populateFormDataAnalyticalNort();
        validator.validate(form, errors);
        Assert.assertFalse(errors.hasErrors());

        EasyMock.verify(mockDocumentTypeCodeService);
        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test unique Title Id when Editing Book Definition
     * and title changed
     */
    @Test
    public void testUniquTitleIdWhenEditing() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(EasyMock.anyObject(Long.class)))
            .andReturn(initializeBookDef("asd/asd", analyticalCode));
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalNort();
        form.setBookdefinitionId(Long.parseLong("1"));
        validator.validate(form, errors);
        Assert.assertFalse(errors.hasErrors());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test title does not change after book is published in Proview
     */
    @Test
    public void testTitleDoesNotChange() {
        final BookDefinition book = initializeBookDef("uscl/an/abcd", analyticalCode);
        book.setPublishedOnceFlag(true);
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(EasyMock.anyObject(Long.class)))
            .andReturn(book);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalNort();
        form.setBookdefinitionId(Long.parseLong("1"));
        validator.validate(form, errors);
        Assert.assertFalse(errors.hasErrors());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test max length of Title Id
     */
    @Test
    public void testMaxLengthTitleId() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalNort();
        form.setTitleId("12345678901234567890123456789012345678901");
        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.max.length", errors.getFieldError("titleId").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test all required fields when put on Complete with NORT
     */
    @Test
    public void testAllRequiredFieldsNort() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(KEYWORD_CODES);
        EasyMock.replay(keywordTypeCodeSevice);

        populateFormDataAnalyticalNort();
        form.setIsComplete(true);
        form.setProviewDisplayName(null);
        form.setFrontMatterTitle(new EbookName());
        form.setCopyright(null);
        form.setMaterialId(null);
        form.setIsbn(null);
        form.setNortDomain(null);
        form.setNortFilterView(null);
        form.setFrontMatterTocLabel(null);
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("proviewDisplayName").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("frontMatterTitle.bookNameText").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("copyright").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("materialId").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("isbn").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("nortDomain").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("nortFilterView").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("frontMatterTocLabel").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("keywords[2]").getCode());
        Assert.assertEquals("error.not.exist", errors.getFieldError("validateForm").getCode());

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(keywordTypeCodeSevice);
    }

    /**
     * Test all required fields when put on Complete with TOC
     */
    @Test
    public void testAllRequiredFieldsToc() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(KEYWORD_CODES);
        EasyMock.replay(keywordTypeCodeSevice);

        populateFormDataAnalyticalToc();
        form.setIsComplete(true);
        form.setProviewDisplayName(null);
        form.setFrontMatterTitle(new EbookName());
        form.setCopyright(null);
        form.setMaterialId(null);
        form.setIsbn(null);
        form.setTocCollectionName(null);
        form.setDocCollectionName(null);
        form.setRootTocGuid(null);
        form.setFrontMatterTocLabel(null);
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("proviewDisplayName").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("frontMatterTitle.bookNameText").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("copyright").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("materialId").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("isbn").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("tocCollectionName").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("rootTocGuid").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("frontMatterTocLabel").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("keywords[2]").getCode());
        Assert.assertEquals("error.not.exist", errors.getFieldError("validateForm").getCode());

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(keywordTypeCodeSevice);
    }

    /**
     * Test all required fields when put on Complete with File source type
     */
    @Test
    public void testAllRequiredFieldsFile() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(KEYWORD_CODES);
        EasyMock.replay(keywordTypeCodeSevice);

        populateFormDataAnalyticalFile();
        form.setIsComplete(true);
        form.setProviewDisplayName(null);
        form.setFrontMatterTitle(new EbookName());
        form.setCopyright(null);
        form.setMaterialId(null);
        form.setIsbn(null);
        form.setCodesWorkbenchBookName(null);
        form.setNortFileLocations(new ArrayList<NortFileLocation>());
        form.setRootTocGuid(null);
        form.setFrontMatterTocLabel(null);
        form.setSplitBook(true);
        form.setSplitTypeAuto(false);
        form.setSplitEBookParts(null);
        form.setSubGroupHeading(null);
        form.setGroupName(null);

        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("proviewDisplayName").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("frontMatterTitle.bookNameText").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("copyright").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("materialId").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("isbn").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("codesWorkbenchBookName").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("frontMatterTocLabel").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("keywords[2]").getCode());
        Assert.assertEquals("error.not.exist", errors.getFieldError("validateForm").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("splitEBookParts").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("subGroupHeading").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("groupName").getCode());

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(keywordTypeCodeSevice);
    }

    /**
     * Test Root TOC GUID format
     */
    @Test
    public void testGuidFormat() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(KEYWORD_CODES);
        EasyMock.replay(keywordTypeCodeSevice);

        populateFormDataAnalyticalToc();
        form.setIsComplete(true);
        form.setRootTocGuid("asdwwqwe");
        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.guid.format", errors.getFieldError("rootTocGuid").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test ISBN-13 format
     */
    @Test
    public void testIsbnFormat() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();
        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(KEYWORD_CODES);
        EasyMock.replay(keywordTypeCodeSevice);

        populateFormDataAnalyticalToc();
        form.setIsComplete(true);
        form.setIsbn("1234");
        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.isbn.format", errors.getFieldError("isbn").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test Nort type fields
     */
    @Test
    public void testNortTypeFields() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);
        expectReplayDocTypeCode();
        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(KEYWORD_CODES);
        EasyMock.replay(keywordTypeCodeSevice);

        populateFormDataAnalyticalFile();
        form.setValidateForm(true);
        form.setSourceType(SourceType.NORT);
        form.setNortDomain("#$#$#$#");
        form.setNortFilterView("#$#$#$");
        validator.validate(form, errors);

        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.alphanumeric.underscore", errors.getFieldError("nortDomain").getCode());
        Assert.assertEquals("error.alphanumeric.underscore", errors.getFieldError("nortFilterView").getCode());

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(keywordTypeCodeSevice);
    }

    /**
     * Test Toc type fields
     */
    @Test
    public void testTocTypeFields() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);
        expectReplayDocTypeCode();
        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(KEYWORD_CODES);
        EasyMock.replay(keywordTypeCodeSevice);

        populateFormDataAnalyticalFile();
        form.setValidateForm(true);
        form.setSourceType(SourceType.TOC);
        form.setTocCollectionName("#$#$#$#");
        form.setDocCollectionName("#$#$#$");
        validator.validate(form, errors);

        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.alphanumeric.underscore", errors.getFieldError("tocCollectionName").getCode());
        Assert.assertEquals("error.alphanumeric.underscore", errors.getFieldError("docCollectionName").getCode());

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(keywordTypeCodeSevice);

    }

    /**
     * Test PublicationCutoffDate format
     */
    @Test
    public void testPublicationCutoffDateFormat() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();
        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(KEYWORD_CODES);
        EasyMock.replay(keywordTypeCodeSevice);

        populateFormDataAnalyticalToc();
        form.setIsComplete(true);
        form.setPublicationCutoffDate("1234");
        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.date.format", errors.getFieldError("publicationCutoffDate").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test Author required fields
     */
    @Test
    public void testAuthorRequiredFields() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalToc();
        final Author author = new Author();
        author.setAuthorFirstName("Test");
        form.getAuthorInfo().add(author);

        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.required.field", errors.getFieldError("authorInfo[0].sequenceNum").getCode());
        Assert.assertEquals("error.author.last.name", errors.getFieldError("authorInfo[0].authorLastName").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test Author required fields
     */
    @Test
    public void testPilotBookRequiredFields() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalToc();
        final PilotBook pilot = new PilotBook();
        pilot.setPilotBookTitleId("Test");
        form.getPilotBookInfo().add(pilot);

        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.required.field", errors.getFieldError("pilotBookInfo[0].sequenceNum").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test ExcludeDocument required fields
     */
    @Test
    public void testExcludeDocumentRequiredFields() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalToc();
        final ExcludeDocument document = new ExcludeDocument();
        document.setNote("test");
        form.getExcludeDocuments().add(document);

        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.required", errors.getFieldError("excludeDocuments[0].documentGuid").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test ExcludeDocument required fields
     */
    @Test
    public void testSplitDocumentRequiredFields() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalToc();
        final SplitDocument document = new SplitDocument();
        document.setNote("test");
        final BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setEbookDefinitionId(Long.valueOf(1));
        document.setBookDefinition(bookDefinition);
        form.getSplitDocuments().add(document);

        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.required", errors.getFieldError("splitDocuments[0].tocGuid").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test ExcludeDocument duplicate guids
     */
    @Test
    public void testExcludeDocumentDuplicateGuids() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalToc();
        final ExcludeDocument document = new ExcludeDocument();
        document.setNote("Test1");
        document.setDocumentGuid("123456789012345678901234567890123");

        final ExcludeDocument document2 = new ExcludeDocument();
        document2.setNote("Test2");
        document2.setDocumentGuid("123456789012345678901234567890123");

        form.getExcludeDocuments().add(document);
        form.getExcludeDocuments().add(document2);

        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.duplicate", errors.getFieldError("excludeDocuments[1].documentGuid").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test SpliteDocument duplicate guids
     */
    @Test
    public void testSplitDocumentDuplicateGuids() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalToc();

        final BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setEbookDefinitionId(Long.valueOf(1));

        final SplitDocument document1 = new SplitDocument();
        document1.setNote("Test1");
        document1.setTocGuid("123456789012345678901234567890123");
        document1.setBookDefinition(bookDefinition);

        final SplitDocument document2 = new SplitDocument();
        document2.setNote("Test2");
        document2.setTocGuid("123456789012345678901234567890123");
        document2.setBookDefinition(bookDefinition);

        form.getSplitDocuments().add(document1);
        form.getSplitDocuments().add(document2);

        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.duplicate", errors.getFieldError("splitDocuments[1].tocGuid").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test ExcludeDocument enabled but not documents listed
     */
    @Test
    public void testExcludeDocumentEnabled() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalToc();
        form.setExcludeDocumentsUsed(true);

        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.used.selected", errors.getFieldError("excludeDocuments").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test RenameTocEntry required fields
     */
    @Test
    public void testRenameTocEntryRequiredFields() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalToc();
        final RenameTocEntry document = new RenameTocEntry();
        document.setNote("test");
        form.getRenameTocEntries().add(document);

        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.required", errors.getFieldError("renameTocEntries[0].tocGuid").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("renameTocEntries[0].oldLabel").getCode());
        Assert.assertEquals("error.required", errors.getFieldError("renameTocEntries[0].newLabel").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test RenameTocEntry duplicate guids
     */
    @Test
    public void testRenameTocEntryDuplicateGuids() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalToc();
        final RenameTocEntry document = new RenameTocEntry();
        document.setNote("Test1");
        document.setTocGuid("123456789012345678901234567890123");
        document.setNewLabel("123456789012345678901234567890123");
        document.setOldLabel("123456789012345678901234567890123");

        final RenameTocEntry document2 = new RenameTocEntry();
        document2.setNote("Test2");
        document2.setTocGuid("123456789012345678901234567890123");
        document2.setNewLabel("123456789012345678901234567890123");
        document2.setOldLabel("123456789012345678901234567890123");

        form.getRenameTocEntries().add(document);
        form.getRenameTocEntries().add(document2);

        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.duplicate", errors.getFieldError("renameTocEntries[1].tocGuid").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test RenameTocEntry enabled but not documents listed
     */
    @Test
    public void testRenameTocEntryEnabled() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalToc();
        form.setRenameTocEntriesUsed(true);

        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.used.selected", errors.getFieldError("renameTocEntries").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test TableViewer required fields
     */
    @Test
    public void testTableViewerRequiredFields() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalToc();
        final TableViewer document = new TableViewer();
        document.setNote("test");
        form.getTableViewers().add(document);

        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.required", errors.getFieldError("tableViewers[0].documentGuid").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test TableViewer duplicate guids
     */
    @Test
    public void testTableViewerDuplicateGuids() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalToc();
        final TableViewer document = new TableViewer();
        document.setNote("Test1");
        document.setDocumentGuid("123456789012345678901234567890123");

        final TableViewer document2 = new TableViewer();
        document2.setNote("Test2");
        document2.setDocumentGuid("123456789012345678901234567890123");

        form.getTableViewers().add(document);
        form.getTableViewers().add(document2);

        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.duplicate", errors.getFieldError("tableViewers[1].documentGuid").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test TableViewer enabled but not documents listed
     */
    @Test
    public void testTableViewerEnabled() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalToc();
        form.setTableViewersUsed(true);

        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.used.selected", errors.getFieldError("tableViewers").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test Author duplicate sequence numbers
     */
    @Test
    public void testAuthorDuplicateSequence() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalToc();
        final Author author = new Author();
        author.setAuthorLastName("Test");
        author.setSequenceNum(1);
        form.getAuthorInfo().add(author);

        final Author author2 = new Author();
        author2.setAuthorLastName("Test2");
        author2.setSequenceNum(1);
        form.getAuthorInfo().add(author2);

        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.sequence.number", errors.getFieldError("authorInfo[1].sequenceNum").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test Author duplicate sequence numbers
     */
    @Test
    public void testPilotBookDuplicateSequence() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalToc();
        final PilotBook pilot = new PilotBook();
        pilot.setPilotBookTitleId("Test");
        pilot.setSequenceNum(1);
        form.getPilotBookInfo().add(pilot);

        final PilotBook pilot2 = new PilotBook();
        pilot2.setPilotBookTitleId("Test2");
        pilot2.setSequenceNum(1);
        form.getPilotBookInfo().add(pilot2);

        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.sequence.number", errors.getFieldError("pilotBookInfo[1].sequenceNum").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test FrontMatterPage required fields
     */
    @Test
    public void testFrontMatterPageRequiredFields() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalToc();
        final FrontMatterPage page = new FrontMatterPage();
        form.getFrontMatters().add(page);
        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.required.field", errors.getFieldError("frontMatters[0].pageTocLabel").getCode());
        Assert.assertEquals("error.required.field", errors.getFieldError("frontMatters[0].sequenceNum").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test FrontMatterSection required fields
     */
    @Test
    public void testFrontMatterSectionRequiredFields() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalToc();
        final FrontMatterPage page = new FrontMatterPage();
        page.setPageTocLabel("Toc Label");
        page.setSequenceNum(1);

        final FrontMatterSection section = new FrontMatterSection();
        page.getFrontMatterSections().add(section);
        form.getFrontMatters().add(page);
        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals(
            "error.required.field",
            errors.getFieldError("frontMatters[0].frontMatterSections[0].sequenceNum").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test FrontMatterPdf all fields required if one field is set
     */
    @Test
    public void testFrontMatterPdfRequiredFields() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();

        populateFormDataAnalyticalToc();
        final FrontMatterPage page = new FrontMatterPage();
        page.setPageTocLabel("Toc Label");
        page.setSequenceNum(1);

        final FrontMatterSection section = new FrontMatterSection();
        section.setSectionText("text section");
        section.setSequenceNum(1);

        final FrontMatterPdf pdf = new FrontMatterPdf();
        pdf.setPdfFilename("filename");
        section.getPdfs().add(pdf);
        page.getFrontMatterSections().add(section);
        form.getFrontMatters().add(page);
        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals(
            "error.required.pdf",
            errors.getFieldError("frontMatters[0].frontMatterSections[0].pdfs[0].pdfFilename").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test FrontMatterPdf file does not exist
     */
    @Test
    public void testFrontMatterPdfDoesNotExist() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();
        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(KEYWORD_CODES);
        EasyMock.replay(keywordTypeCodeSevice);

        populateFormDataAnalyticalToc();
        form.setValidateForm(true);
        final FrontMatterPage page = new FrontMatterPage();
        page.setPageTocLabel("Toc Label");
        page.setSequenceNum(1);

        final FrontMatterSection section = new FrontMatterSection();
        section.setSectionText("text section");
        section.setSequenceNum(1);

        final FrontMatterPdf pdf = new FrontMatterPdf();
        pdf.setPdfFilename("filename");
        pdf.setPdfLinkText("Link");
        pdf.setSequenceNum(1);
        section.getPdfs().add(pdf);
        page.getFrontMatterSections().add(section);
        form.getFrontMatters().add(page);
        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals(
            "error.not.exist",
            errors.getFieldError("frontMatters[0].frontMatterSections[0].pdfs[0].pdfFilename").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    @Test
    public void testGroupsRequiredForSplitBook() {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        expectReplayDocTypeCode();
        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(KEYWORD_CODES);
        EasyMock.replay(keywordTypeCodeSevice);

        populateFormDataAnalyticalToc();
        form.setValidateForm(true);
        form.setSplitBook(true);
        form.setGroupsEnabled(false);

        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.required", errors.getFieldError("groupsEnabled").getCode());

        EasyMock.verify(mockBookDefinitionService);
    }

    @Test
    public void testNoIndexTocCollectionName() {
        form.setIndexTocCollectionName(null);
        testIndexFieldIsEmpty("indexTocCollectionName");
    }

    @Test
    public void testNoIndexTocRootGuid() {
        form.setIndexTocRootGuid(null);
        testIndexFieldIsEmpty("indexTocRootGuid");
    }

    private void testIndexFieldIsEmpty(final String fieldName) {
        form.setIndexIncluded(true);

        validator.validate(form, errors);

        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("error.required", errors.getFieldError(fieldName).getCode());
    }

    @Test
    public void testPublishedDateWrongFormat() {
        DateFormat wrongDateFormat = new SimpleDateFormat(WRONG_DATE_PATTERN);
        form.setPublishedDate(wrongDateFormat.format(new Date()));

        validator.validate(form, errors);

        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals(WRONG_DATE_FORMAT_ERROR,
                errors.getFieldError(PUBLISHED_DATE).getCode());
    }

    @Test
    public void testProviewKeywordsAllRequiredFilledUS() {
        final Map<Long, Collection<Long>> keywords = new HashMap<>();
        keywords.put(PUBLISHER_KEYWORD_ID_USCL, Collections.singletonList(1L));
        keywords.put(JURISDICTION_KEYWORD_ID_USCL, Collections.singletonList(1L));
        testKeywords(USCL, keywords, Arrays.asList(
                err -> Assert.assertNull(err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, PUBLISHER_KEYWORD_ID_USCL))),
                err -> Assert.assertNull(err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, JURISDICTION_KEYWORD_ID_USCL)))));
    }

    @Test
    public void testProviewKeywordsOneRequiredMissingUS() {
        final Map<Long, Collection<Long>> keywords = new HashMap<>();
        keywords.put(PUBLISHER_KEYWORD_ID_USCL, Collections.singletonList(1L));
        testKeywords(USCL, keywords, Arrays.asList(
                err -> Assert.assertNull(err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, PUBLISHER_KEYWORD_ID_USCL))),
                err -> Assert.assertEquals(REQUIRED_ERROR, err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, JURISDICTION_KEYWORD_ID_USCL)).getCode())));
    }

    @Test
    public void testProviewKeywordsAllRequiredMissingUS() {
        testKeywords(USCL, new HashMap<>(), Arrays.asList(
                err -> Assert.assertEquals(REQUIRED_ERROR, err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, PUBLISHER_KEYWORD_ID_USCL)).getCode()),
                err -> Assert.assertEquals(REQUIRED_ERROR, err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, JURISDICTION_KEYWORD_ID_USCL)).getCode())));
    }

    @Test
    public void testProviewKeywordsAllRequiredFilledCW() {
        final Map<Long, Collection<Long>> keywords = new HashMap<>();
        keywords.put(PUBLISHER_KEYWORD_ID_CW, Collections.singletonList(1L));
        keywords.put(JURISDICTION_KEYWORD_ID_CW, Collections.singletonList(1L));
        testKeywords(CW, keywords, Arrays.asList(
                err -> Assert.assertNull(err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, PUBLISHER_KEYWORD_ID_CW))),
                err -> Assert.assertNull(err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, JURISDICTION_KEYWORD_ID_CW)))));
    }

    @Test
    public void testProviewKeywordsOneRequiredMissingCW() {
        final Map<Long, Collection<Long>> keywords = new HashMap<>();
        keywords.put(PUBLISHER_KEYWORD_ID_CW, Collections.singletonList(1L));
        testKeywords(CW, keywords, Arrays.asList(
                err -> Assert.assertNull(err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, PUBLISHER_KEYWORD_ID_CW))),
                err -> Assert.assertEquals(REQUIRED_ERROR, err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, JURISDICTION_KEYWORD_ID_CW)).getCode())));
    }

    @Test
    public void testProviewKeywordsAllRequiredMissingCW() {
        testKeywords(CW, new HashMap<>(), Arrays.asList(
                err -> Assert.assertEquals(REQUIRED_ERROR, err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, PUBLISHER_KEYWORD_ID_CW)).getCode()),
                err -> Assert.assertEquals(REQUIRED_ERROR, err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, JURISDICTION_KEYWORD_ID_CW)).getCode())));
    }

    @Test
    public void testNoSubjectKeywordsUS() {
        testKeywords(USCL, getSubjectKeywordsFormData(SUBJECT_KEYWORD_ID_USCL, 0),
                Collections.singletonList(err -> Assert.assertNull(err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, SUBJECT_KEYWORD_ID_USCL)))));
    }

    @Test
    public void testSubjectKeywordsUSRequired() {
        setSubjectKeywordRequired(USCL);
        testKeywords(USCL, getSubjectKeywordsFormData(SUBJECT_KEYWORD_ID_USCL, 0),
                Collections.singletonList(err -> Assert.assertEquals(REQUIRED_ERROR, err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, SUBJECT_KEYWORD_ID_USCL)).getCode())));
    }

    @Test
    public void testSubjectKeywordsCWRequired() {
        setSubjectKeywordRequired(CW);
        testKeywords(CW, getSubjectKeywordsFormData(SUBJECT_KEYWORD_ID_CW, 0),
                Collections.singletonList(err -> Assert.assertEquals(REQUIRED_ERROR, err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, SUBJECT_KEYWORD_ID_CW)).getCode())));
    }

    @Test
    public void testMultipleSubjectKeywordsUS() {
        testKeywords(USCL, getSubjectKeywordsFormData(SUBJECT_KEYWORD_ID_USCL, 3),
                Collections.singletonList(err -> Assert.assertNull(err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, SUBJECT_KEYWORD_ID_USCL)))));
    }

    @Test
    public void testMultipleSubjectKeywordsMaxNumberExceededUS() {
        testKeywords(USCL, getSubjectKeywordsFormData(SUBJECT_KEYWORD_ID_USCL, 4), Collections.singletonList(err -> Assert.assertEquals(
                SUBJECT_KEYWORD_ERROR,
                err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, SUBJECT_KEYWORD_ID_USCL)).getCode())));
    }

    @Test
    public void testNoSubjectKeywordsCW() {
        testKeywords(CW, getSubjectKeywordsFormData(SUBJECT_KEYWORD_ID_CW, 0),
                Collections.singletonList(err -> Assert.assertNull(err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, SUBJECT_KEYWORD_ID_CW)))));
    }

    @Test
    public void testMultipleSubjectKeywordsCW() {
        testKeywords(CW, getSubjectKeywordsFormData(SUBJECT_KEYWORD_ID_CW, 3),
                Collections.singletonList(err -> Assert.assertNull(err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, SUBJECT_KEYWORD_ID_CW)))));
    }

    @Test
    public void testMultipleSubjectKeywordsMaxNumberExceededCW() {
        testKeywords(CW, getSubjectKeywordsFormData(SUBJECT_KEYWORD_ID_CW, 4), Collections.singletonList(err -> Assert.assertEquals(
                SUBJECT_KEYWORD_ERROR,
                err.getFieldError(String.format(KEYWORDS_ERROR_PATTERN, SUBJECT_KEYWORD_ID_CW)).getCode())));
    }

    private void testKeywords(final String publisher, final Map<Long, Collection<Long>> keywords, final List<Consumer<Errors>> assertions) {
        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(KEYWORD_CODES);
        EasyMock.replay(keywordTypeCodeSevice);

        form.setValidateForm(true);
        form.setPublisher(publisher);
        form.setKeywords(keywords);

        setupPublisherAndTitleId(TITLE_ID_BY_PUBLISHER.get(publisher), analyticalCode, 2, 2);

        validator.validate(form, errors);

        assertions.forEach(assertion -> assertion.accept(errors));
    }

    private void setSubjectKeywordRequired(final String publisher) {
        KEYWORD_CODES.stream()
                .filter(item -> item.getName().equals(SUBJECT_KEYWORD + " " + publisher))
                .forEach(item -> item.setIsRequired(true));
    }

    private Map<Long, Collection<Long>> getSubjectKeywordsFormData(final long subjectKeywordId, final int numberOfSubjectKeywords) {
        final Map<Long, Collection<Long>> keywords = new HashMap<>();
        final Collection<Long> keywordValues = numberOfSubjectKeywords == 0
                ? null
                : LongStream.range(0, numberOfSubjectKeywords).boxed().collect(Collectors.toList());
        keywords.put(subjectKeywordId, keywordValues);
        return keywords;
    }

    private KeywordTypeCode getKeywordTypeCode(final String name, final boolean isRequired, final Long keywordId, final String...values) {
        final KeywordTypeCode keyword = new KeywordTypeCode();
        keyword.setId(keywordId);
        keyword.setIsRequired(isRequired);
        keyword.setName(name);

        keyword.setValues(IntStream.range(0, values.length).boxed()
            .map(index -> createKeywordTypeValue(index, values[index], keyword))
            .collect(Collectors.toList()));

        return keyword;
    }

    private KeywordTypeValue createKeywordTypeValue(final int index, final String value, final KeywordTypeCode typeCode) {
        final KeywordTypeValue typeValue = new KeywordTypeValue();
        typeValue.setId((long) index);
        typeValue.setKeywordTypeCode(typeCode);
        typeValue.setName(value);
        return typeValue;
    }

    private void setupPublisherAndTitleId(
        final String titleId,
        final DocumentTypeCode contentType,
        final int mockBookIdReplay,
        final int mockDocTypeReplay) {
        final BookDefinition book = initializeBookDef(titleId, contentType);

        form.initialize(book, KEYWORD_CODES);
        form.setIsComplete(book.getEbookDefinitionCompleteFlag());
        form.setBookdefinitionId(Long.parseLong("1"));

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(EasyMock.anyObject(Long.class)))
            .andReturn(book)
            .times(mockBookIdReplay);
        EasyMock.replay(mockBookDefinitionService);

        EasyMock.expect(mockDocumentTypeCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class)))
            .andReturn(contentType)
            .times(mockDocTypeReplay);
        EasyMock.replay(mockDocumentTypeCodeService);
    }

    private BookDefinition populateFormDataAnalyticalFile() {
        final String titleId = "uscl/an/abcd";
        final BookDefinition book = initializeBookDef(titleId, analyticalCode);
        book.setSourceType(SourceType.FILE);
        populateFormData(book);
        form.setCodesWorkbenchBookName("book");
        final EbookName nameLine = new EbookName();
        nameLine.setEbookNameId(1);
        nameLine.setBookNameText("Book Title");
        nameLine.setSequenceNum(1);
        form.setFrontMatterTitle(nameLine);
        form.setFrontMatterTocLabel("Label");
        final NortFileLocation fileLocation = new NortFileLocation();
        fileLocation.setNortFileLocationId(1L);
        fileLocation.setSequenceNum(1);
        fileLocation.setLocationName("content");
        form.getNortFileLocations().add(fileLocation);

        return book;
    }

    private BookDefinition populateFormDataAnalyticalNort() {
        final String titleId = "uscl/an/abcd";
        final BookDefinition book = initializeBookDef(titleId, analyticalCode);
        book.setSourceType(SourceType.NORT);
        populateFormData(book);
        form.setNortDomain("1234");
        form.setNortFilterView("1234");
        final EbookName nameLine = new EbookName();
        nameLine.setEbookNameId(1);
        nameLine.setBookNameText("Book Title");
        nameLine.setSequenceNum(1);
        form.setFrontMatterTitle(nameLine);
        form.setFrontMatterTocLabel("Label");

        return book;
    }

    private BookDefinition populateFormDataAnalyticalToc() {
        final String titleId = "uscl/an/abcd";
        final BookDefinition book = initializeBookDef(titleId, analyticalCode);
        book.setSourceType(SourceType.TOC);
        populateFormData(book);
        form.setTocCollectionName("1234");
        form.setDocCollectionName("doc collection");
        form.setRootTocGuid("i12345678123456781234567812345678");
        final EbookName nameLine = new EbookName();
        nameLine.setEbookNameId(1);
        nameLine.setBookNameText("Book Title");
        nameLine.setSequenceNum(1);
        form.setFrontMatterTitle(nameLine);
        form.setFrontMatterTocLabel("Label");

        return book;
    }

    private void populateFormData(final BookDefinition book) {
        form.initialize(book, KEYWORD_CODES);
        form.setProviewDisplayName("Proview Display Name");
        form.setCopyright("copyright");
        form.setMaterialId("a12345678123456781234567812345678");
        form.setIsbn("978-193-5-18235-1");
        form.setKeyCiteToplineFlag(book.getKeyciteToplineFlag());
        form.setIsComplete(book.getEbookDefinitionCompleteFlag());
        form.setValidateForm(false);
        form.setSourceType(book.getSourceType());
    }

    private BookDefinition initializeBookDef(final String titleId, final DocumentTypeCode contentType) {
        final BookDefinition book = new BookDefinition();
        book.setDocumentTypeCodes(contentType);
        book.setFullyQualifiedTitleId(titleId);
        book.setSourceType(SourceType.NORT);
        book.setIsDeletedFlag(false);
        book.setEbookDefinitionCompleteFlag(false);
        book.setAutoUpdateSupportFlag(true);
        book.setSearchIndexFlag(true);
        book.setPublishedOnceFlag(false);
        book.setOnePassSsoLinkFlag(true);
        book.setKeyciteToplineFlag(true);
        book.setIsAuthorDisplayVertical(true);
        book.setEnableCopyFeatureFlag(false);

        book.setIsSplitBook(true);
        book.setIsSplitTypeAuto(false);
        book.setSplitEBookParts(Integer.valueOf(1));
        book.setSubGroupHeading("groupHeading");
        book.setGroupName("groupName");
        return book;
    }

    private void expectReplayDocTypeCode() {
        EasyMock.expect(mockDocumentTypeCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class)))
            .andReturn(analyticalCode);
        EasyMock.replay(mockDocumentTypeCodeService);
    }
}
