package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.common.filesystem.NasFileSystem;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCopyright;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCurrency;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.NortFileLocation;
import com.thomsonreuters.uscl.ereader.core.book.domain.PilotBook;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.TableViewer;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.DocumentTypeCodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.KeywordTypeCodeSevice;
import com.thomsonreuters.uscl.ereader.core.service.DateService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.ALL_PUBLISHERS;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.CW_PUBLISHER_NAME;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.DATE_FORMATTER;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.USCL_PUBLISHER_NAME;
import static com.thomsonreuters.uscl.ereader.mgr.web.ErrorMessageCodes.FORBIDDEN_CHARACTERS_IN_PDF_NAME;
import static com.thomsonreuters.uscl.ereader.mgr.web.ErrorMessageCodes.WRONG_PDF_FILE_EXTENSION;
import static org.apache.commons.lang3.StringUtils.LF;

@Component("editBookDefinitionFormValidator")
public class EditBookDefinitionFormValidator extends BaseFormValidator implements Validator {
    private static final String PRINT_COMPONENT = "printComponents";
    private static final String PUB_CUTOFF_DATE = "publicationCutoffDate";
    private static final String PROVIEW_DISPLAY_NAME = "proviewDisplayName";
    private static final String RELEASE_NOTES = "releaseNotes";
    private static final String COPYRIGHT = "copyright";
    private static final String AUTHOR_FIRST_NAME = "authorFirstName";
    private static final String AUTHOR_MIDDLE_NAME = "authorMiddleName";
    private static final String AUTHOR_LAST_NAME = "authorLastName";
    private static final String AUTHOR_ADDL_TEXT = "authorAddlText";
    private static final String AUTHOR_ADDL_PRE_TEXT = "authorAddlPreText";
    private static final String AUTHOR_NAME_PREFIX = "authorNamePrefix";
    private static final String AUTHOR_NAME_SUFFIX = "authorNameSuffix";
    private static final String AUTHOR_INFO_BRACKET = "authorInfo[";
    private static final String BRACKET_DOT = "].";
    private static final int MAXIMUM_CHARACTER_40 = 40;
    private static final int MAXIMUM_CHARACTER_64 = 64;
    private static final int MAXIMUM_CHARACTER_512 = 512;
    private static final int MAXIMUM_CHARACTER_1024 = 1024;
    private static final int MAXIMUM_CHARACTER_2000 = 2000;
    private static final int MAXIMUM_CHARACTER_2048 = 2048;
    private static final int MAX_NUMBER_SUBJECT_KEYWORDS = 3;
    private static final int LINE_BREAK_LENGTH = 5;
    private static final String ERROR_KEYWORD_MAX_SUBJECS_NUMBER_EXCEEDED = "error.keyword.max.subjecs.number.exceeded";
    private static final String ERROR_DOES_NOT_EXIST = "error.not.exist";
    private static final String CRLF = "\r\n";
    private static final String PDF_DOES_NOT_EXIST_MESSAGE = "PDF file does not exist on server.";
    private static final Set<String> REGISTERED_PUBLISHERS = Sets.newSet(USCL_PUBLISHER_NAME, CW_PUBLISHER_NAME);
    private static final String SUBSTITUTE_TOC_HEADERS_LEVEL = "substituteTocHeadersLevel";
    private static final String ERROR_REQUIRED = "error.required";
    private static final String ERROR_POSITIVE_INTEGER = "error.positive.integer";
    private static final String ERROR_PUBLICATION_CUT_OFF_DATE = "Publication cut-off date should be greater than today. Server date: ";

    @Autowired
    private BookDefinitionService bookDefinitionService;
    @Autowired
    private KeywordTypeCodeSevice keywordTypeCodeSevice;
    @Autowired
    private DocumentTypeCodeService documentTypeCodeService;
    @Autowired
    private String environmentName;
    @Autowired
    private IsbnValidator isbnValidator;
    @Autowired
    private NasFileSystem nasFileSystem;
    @Autowired
    private IssnValidator issnValidator;
    @Autowired
    private PdfFileNameValidator pdfFileNameValidator;
    @Autowired
    private DateService dateService;

    @Override
    public boolean supports(final Class clazz) {
        return (EditBookDefinitionForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        final EditBookDefinitionForm form = (EditBookDefinitionForm) obj;

        // Clear out empty rows in authors, nameLines, and additionalFrontMatters before validation
        form.removeEmptyRows();

        // Set validate error to prevent saving the form
        final boolean validateForm = form.isValidateForm();

        validateTitleId(form, errors);

        // MaxLength Validations
        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_1024,
            form.getProviewDisplayName(),
            "proviewDisplayName",
            new Object[] {"ProView Display Name", MAXIMUM_CHARACTER_1024});
        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_2048,
            form.getCopyrightPageText(),
            "copyrightPageText",
            new Object[] {"Copyright Page Text", MAXIMUM_CHARACTER_2048});
        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_64,
            form.getMaterialId(),
            "materialId",
            new Object[] {"Material ID", MAXIMUM_CHARACTER_64});
        checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_64,
                form.getEntitlement(),
                "entitlement",
                new Object[] {"Entitlement", MAXIMUM_CHARACTER_64});
        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_64,
            form.getRootTocGuid(),
            "rootTocGuid",
            new Object[] {"Root TOC Guid", MAXIMUM_CHARACTER_64});
        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_64,
            form.getTocCollectionName(),
            "tocCollectionName",
            new Object[] {"TOC Collection", MAXIMUM_CHARACTER_64});
        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_64,
            form.getDocCollectionName(),
            "docCollectionName",
            new Object[] {"DOC Collection", MAXIMUM_CHARACTER_64});
        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_64,
            form.getNortDomain(),
            "nortDomain",
            new Object[] {"NORT Domain", MAXIMUM_CHARACTER_64});
        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_64,
            form.getNortFilterView(),
            "nortFilterView",
            new Object[] {"NORT Filter View", MAXIMUM_CHARACTER_64});
        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_1024,
            form.getPublishDateText(),
            "publishDateText",
            new Object[] {"Publish Date Text", MAXIMUM_CHARACTER_1024});
        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_2048,
            form.getCurrency(),
            "currency",
            new Object[] {"Currentness Message", MAXIMUM_CHARACTER_2048});
        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_1024,
            form.getComment(),
            "comment",
            new Object[] {"Comment", MAXIMUM_CHARACTER_1024});
        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_2048,
            form.getAdditionalTrademarkInfo(),
            "additionalTrademarkInfo",
            new Object[] {"Additional Trademark/Patent Info", MAXIMUM_CHARACTER_2048});

        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_2048,
            form.getCopyright(),
            "copyright",
            new Object[] {"Copyright", MAXIMUM_CHARACTER_2048});

        if (form.getSourceType() != SourceType.XPP) {
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_2048,
                form.getFrontMatterTitle().getBookNameText(),
                "frontMatterTitle.bookNameText",
                new Object[] {"Main Title", MAXIMUM_CHARACTER_2048});
        }

        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_2048,
            form.getFrontMatterSubtitle().getBookNameText(),
            "frontMatterSubtitle.bookNameText",
            new Object[] {"Sub Title", MAXIMUM_CHARACTER_2048});

        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_2048,
            form.getFrontMatterSeries().getBookNameText(),
            "frontMatterSeries.bookNameText",
            new Object[] {"Series", MAXIMUM_CHARACTER_2048});

        validateReleaseNotes(form.getReleaseNotes(), errors);
        validateIndexFields(form, errors);
        validateAuthors(form, errors);
        validatePilotBooks(form, errors);
        validateExcludeDocuments(form, errors);
        validateRenameTocEntries(form, errors);
        //validateTableViewers(form, errors);
        validateAdditionalFrontMatter(form, errors);
        validateDocumentCurrencies(form, errors);
        validateDocumentCopyrights(form, errors);

        validateNoSymbolsForbiddenByProview(form, errors);

        if (form.getPublishedDate() != null) {
            checkDateFormat(errors, form.getPublishedDate(), "publishedDate");
        }

        validatePublicationCutOffDate(errors, form);

        // Only run these validation when Validate Button or Book Definition is set as Complete.
        if (form.getIsComplete() || validateForm) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "proviewDisplayName", ERROR_REQUIRED);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "materialId", ERROR_REQUIRED);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "copyright", ERROR_REQUIRED);

            if (form.getSourceType() != SourceType.XPP) {
                if (!form.isTitlePageImageIncluded()) {
                    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "frontMatterTitle.bookNameText", ERROR_REQUIRED);
                }
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "frontMatterTocLabel", ERROR_REQUIRED);
            }
            switch (form.getSourceType()) {
            case TOC:
                checkGuidFormat(errors, form.getRootTocGuid(), "rootTocGuid");
                checkSpecialCharacters(errors, form.getTocCollectionName(), "tocCollectionName", true);
                checkSpecialCharacters(errors, form.getDocCollectionName(), "docCollectionName", true);
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rootTocGuid", ERROR_REQUIRED);
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tocCollectionName", ERROR_REQUIRED);
                break;
            case NORT:
                checkSpecialCharacters(errors, form.getNortDomain(), "nortDomain", true);
                checkSpecialCharacters(errors, form.getNortFilterView(), "nortFilterView", true);
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nortDomain", ERROR_REQUIRED);
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nortFilterView", ERROR_REQUIRED);
                break;
            case FILE:
                checkMaxLength(
                    errors,
                    MAXIMUM_CHARACTER_1024,
                    form.getCodesWorkbenchBookName(),
                    "codesWorkbenchBookName",
                    new Object[] {"CWB Book Name", MAXIMUM_CHARACTER_1024});
                validateNortFileLocations(form, errors);
                break;
            case XPP:
                checkPrintSetNumber(errors, form.getPrintSetNumber(), "printSetNumber");
                checkPrintSubNumber(errors, form.getPrintSubNumber(), "printSubNumber");
                validatePrintComponentsSplitters(form.getPrintComponentsCollection(), errors);
                break;
            }

            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "isbn", ERROR_REQUIRED);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "keyCiteToplineFlag", ERROR_REQUIRED);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "frontMatterTitle", ERROR_REQUIRED);

            checkIsbnNumber(errors, form.getIsbn(), "isbn");
            checkIssnNumber(errors, form.getIssn(), "issn");

            validateProviewKeywords(form, errors);
            validateProdOnlyRequirements(form, errors);

            if (isSplitBook(form)) {
                if (!form.isGroupsEnabled()) {
                    errors.rejectValue("groupsEnabled", ERROR_REQUIRED);
                }
            }
        }

        if (form.isGroupsEnabled()) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "groupName", ERROR_REQUIRED);
            if (isSplitBook(form)) {
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subGroupHeading", ERROR_REQUIRED);
            }
        }

        if (!form.isSplitTypeAuto()) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "splitEBookParts", ERROR_REQUIRED);
            if (form.getSplitEBookParts() != null && form.getSplitEBookParts() > 0) {
                validateSplitDocuments(form, errors);
            }
        }
        validateSubstitutionTocLevel(form, errors);

        // Adding error message if any validation fails
        if (errors.hasErrors()) {
            errors.rejectValue("validateForm", "mesg.errors.form");
        }

        // Adding validation message if Validation button was pressed.
        if (validateForm) {
            errors.rejectValue("validateForm", "mesg.validate.form");
        }
    }

    private void validateNoSymbolsForbiddenByProview(final EditBookDefinitionForm form, final Errors errors) {
        checkForbiddenProviewSymbolsFor(PROVIEW_DISPLAY_NAME, form.getProviewDisplayName(), errors);
        checkForbiddenProviewSymbolsFor(RELEASE_NOTES, form.getReleaseNotes(), errors);
        checkForbiddenProviewSymbolsFor(COPYRIGHT, form.getCopyright(), errors);
        checkForbiddenProviewSymbolsForAuthors(form, errors);
    }

    private void checkForbiddenProviewSymbolsForAuthors(final EditBookDefinitionForm form, final Errors errors) {
        final List<Author> authorInfos = form.getAuthorInfo();
        for (int i = 0; i < authorInfos.size(); i++) {
            final Author author = authorInfos.get(i);
            final String authorInfoPrefixWithId = AUTHOR_INFO_BRACKET + i + BRACKET_DOT;
            checkForbiddenProviewSymbolsFor(authorInfoPrefixWithId + AUTHOR_FIRST_NAME, author.getAuthorFirstName(), errors);
            checkForbiddenProviewSymbolsFor(authorInfoPrefixWithId + AUTHOR_MIDDLE_NAME, author.getAuthorMiddleName(), errors);
            checkForbiddenProviewSymbolsFor(authorInfoPrefixWithId + AUTHOR_LAST_NAME, author.getAuthorLastName(), errors);
            checkForbiddenProviewSymbolsFor(authorInfoPrefixWithId + AUTHOR_ADDL_TEXT, author.getAuthorAddlText(), errors);
            checkForbiddenProviewSymbolsFor(authorInfoPrefixWithId + AUTHOR_ADDL_PRE_TEXT, author.getAuthorAddlPreText(), errors);
            checkForbiddenProviewSymbolsFor(authorInfoPrefixWithId + AUTHOR_NAME_PREFIX, author.getAuthorNamePrefix(), errors);
            checkForbiddenProviewSymbolsFor(authorInfoPrefixWithId + AUTHOR_NAME_SUFFIX, author.getAuthorNameSuffix(), errors);
        }
    }

    // All the validations to verify that the Title ID is formed with all requirements
    private void validateTitleId(final EditBookDefinitionForm form, final Errors errors) {
        final String titleId = form.getTitleId();
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "titleId", ERROR_REQUIRED);
        checkForSpaces(errors, titleId, "titleId", "Title ID");
        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_40,
            titleId,
            "titleId",
            new Object[] {"Title ID", MAXIMUM_CHARACTER_40});

        // Validate publisher information
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "publisher", ERROR_REQUIRED);

        // Validate publication and title ID
        if (StringUtils.isNotEmpty(titleId)) {
            final Long contentTypeId = form.getContentTypeId();
            final String publisher = form.getPublisher();
            final DocumentTypeCode contentType =
                (contentTypeId != null) ? documentTypeCodeService.getDocumentTypeCodeById(contentTypeId) : null;

            if (USCL_PUBLISHER_NAME.equalsIgnoreCase(publisher)) {
                if (contentType != null
                    && WebConstants.DOCUMENT_TYPE_ANALYTICAL.equalsIgnoreCase(contentType.getName())) {
                    // Validate Analytical fields are filled out
                    final String pubAbbr = form.getPubAbbr();
                    checkForSpaces(errors, pubAbbr, "pubAbbr", "Pub Abbreviation");
                    checkSpecialCharacters(errors, pubAbbr, "pubAbbr", false);
                    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubAbbr", ERROR_REQUIRED);
                } else if (contentType != null
                    && WebConstants.DOCUMENT_TYPE_COURT_RULES.equalsIgnoreCase(contentType.getName())) {
                    // Validate Court Rules fields are filled out
                    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "state", ERROR_REQUIRED);
                    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubType", ERROR_REQUIRED);
                } else if (contentType != null
                    && WebConstants.DOCUMENT_TYPE_SLICE_CODES.equalsIgnoreCase(contentType.getName())) {
                    // Validate Slice Codes fields are filled out
                    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jurisdiction", ERROR_REQUIRED);
                    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubInfo", ERROR_REQUIRED);
                } else {
                    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubInfo", ERROR_REQUIRED);
                }
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contentTypeId", ERROR_REQUIRED);
            } else if (CW_PUBLISHER_NAME.equalsIgnoreCase(publisher)) {
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubInfo", ERROR_REQUIRED);
            } else {
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubInfo", ERROR_REQUIRED);

                // Validate Product Code
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "productCode", ERROR_REQUIRED);
                final String productCode = form.getProductCode();
                checkForSpaces(errors, productCode, "productCode", "Product Code");
                checkSpecialCharacters(errors, productCode, "productCode", true);
            }

            final Long bookDefinitionId = form.getBookdefinitionId();
            if (bookDefinitionId != null) {
                // Lookup the book by its primary key
                final BookDefinition bookDef =
                    bookDefinitionService.findBookDefinitionByEbookDefId(form.getBookdefinitionId());

                // Bug 297047: Super user deletes Book Definition while another user is editing the Book Definition.
                if (bookDef == null) {
                    // Let controller redirect the user to error: Book Definition Deleted
                    return;
                }

                final String oldTitleId = bookDef.getFullyQualifiedTitleId();

                // Check if Book Definition is deleted
                if (bookDef.isDeletedFlag()) {
                    errors.rejectValue("validateForm", "mesg.book.deleted");
                }

                // This is from the book definition edit
                if (bookDef.getPublishedOnceFlag()) {
                    // Been published to Proview and set to F
                    if (!oldTitleId.equals(titleId)) {
                        errors.rejectValue("titleId", "error.titleid.changed");
                    }
                } else {
                    // Check new TitleId is unique if it changed
                    if (!oldTitleId.equals(titleId)) {
                        checkUniqueTitleId(errors, titleId);
                    }
                }
            } else {
                // This is from the book definition create
                checkUniqueTitleId(errors, titleId);
            }

            // Validate Publication Information
            final String pubInfo = form.getPubInfo();
            checkForSpaces(errors, pubInfo, "pubInfo", "Pub Info");
            checkSpecialCharacters(errors, pubInfo, "pubInfo", true);
        } else {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jurisdiction", ERROR_REQUIRED);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubInfo", ERROR_REQUIRED);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "productCode", ERROR_REQUIRED);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contentTypeId", ERROR_REQUIRED);
        }
    }

    private void validateAuthors(final EditBookDefinitionForm form, final Errors errors) {
        // Require last name to be filled if there are authors
        // Also check max character length for all the fields
        final List<Author> authors = form.getAuthorInfo();
        // Sort the authors before validations
        Collections.sort(authors);
        form.setAuthorInfo(authors);
        final List<Integer> authorSequenceChecker = new ArrayList<>();
        int i = 0;
        for (final Author author : authors) {
            ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "authorInfo[" + i + "].sequenceNum",
                "error.required.field",
                new Object[] {"Sequence Number"});
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_40,
                author.getAuthorNamePrefix(),
                "authorInfo[" + i + "].authorNamePrefix",
                new Object[] {"Prefix", MAXIMUM_CHARACTER_40});
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_40,
                author.getAuthorNameSuffix(),
                "authorInfo[" + i + "].authorNameSuffix",
                new Object[] {"Suffix", MAXIMUM_CHARACTER_40});
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_1024,
                author.getAuthorFirstName(),
                "authorInfo[" + i + "].authorFirstName",
                new Object[] {"First name", MAXIMUM_CHARACTER_1024});
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_1024,
                author.getAuthorMiddleName(),
                "authorInfo[" + i + "].authorMiddleName",
                new Object[] {"Middle name", MAXIMUM_CHARACTER_1024});
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_1024,
                author.getAuthorLastName(),
                "authorInfo[" + i + "].authorLastName",
                new Object[] {"Last name", MAXIMUM_CHARACTER_1024});
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_2048,
                author.getAuthorAddlText(),
                "authorInfo[" + i + "].authorAddlText",
                new Object[] {"Additional text", MAXIMUM_CHARACTER_2048});
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_2048,
                author.getAuthorAddlPreText(),
                "authorInfo[" + i + "].authorAddlPreText",
                new Object[] {"Additional prepended text", MAXIMUM_CHARACTER_2048});
            ValidationUtils
                .rejectIfEmptyOrWhitespace(errors, "authorInfo[" + i + "].authorLastName", "error.author.last.name");
            // Check duplicate sequence numbers exist
            checkDuplicateSequenceNumber(
                errors,
                author.getSequenceNum(),
                "authorInfo[" + i + "].sequenceNum",
                authorSequenceChecker);
            i++;
        }
    }

    private void validatePilotBooks(final EditBookDefinitionForm form, final Errors errors) {
        // Require last name to be filled if there are pilot books
        // Also check max character length for all the fields
        final List<PilotBook> books = form.getPilotBookInfo();
        // Sort the pilot books before validations
        Collections.sort(books);
        form.setPilotBookInfo(books);
        final List<Integer> pilotBookSequenceChecker = new ArrayList<>();
        int i = 0;
        for (final PilotBook book : books) {
            ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "pilotBookInfo[" + i + "].sequenceNum",
                "error.required.field",
                new Object[] {"Sequence Number"});
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_40,
                book.getPilotBookTitleId(),
                "pilotBookInfo[" + i + "].pilotBookTitleId",
                new Object[] {"pilotBookTitleId", MAXIMUM_CHARACTER_40});
            if (!book.getPilotBookTitleId().startsWith("uscl/")) {
                errors.rejectValue("pilotBookInfo[" + i + "].pilotBookTitleId", "error.pilotBook.titleId");
            }
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_512,
                book.getNote(),
                "pilotBookInfo[" + i + "].note",
                new Object[] {"Notes", MAXIMUM_CHARACTER_2048});
            ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "pilotBookInfo[" + i + "].pilotBookTitleId",
                "error.pilotBook.titleId");
            // Check duplicate sequence numbers exist
            checkDuplicateSequenceNumber(
                errors,
                book.getSequenceNum(),
                "pilotBookInfo[" + i + "].sequenceNum",
                pilotBookSequenceChecker);
            i++;
        }
    }

    private void validateNortFileLocations(final EditBookDefinitionForm form, final Errors errors) {
        // Require at least one file location
        // Also check max character length for all the fields
        final List<NortFileLocation> nortFileLocations = form.getNortFileLocations();
        // Sort the list before validations
        Collections.sort(nortFileLocations);
        form.setNortFileLocations(nortFileLocations);
        final List<Integer> sequenceChecker = new ArrayList<>();

        // Check if book Folder exists
        final String bookFolderName = form.getCodesWorkbenchBookName();

        if (validateFileExists(errors, "codesWorkbenchBookName", nasFileSystem.getCodesWorkbenchRootDir(), bookFolderName)) {
            final File bookDirectory = new File(nasFileSystem.getCodesWorkbenchRootDir(), bookFolderName);
            int i = 0;
            for (final NortFileLocation fileLocation : nortFileLocations) {
                ValidationUtils.rejectIfEmptyOrWhitespace(
                    errors,
                    "nortFileLocations[" + i + "].sequenceNum",
                    "error.required.field",
                    new Object[] {"Sequence Number"});
                checkMaxLength(
                    errors,
                    MAXIMUM_CHARACTER_1024,
                    fileLocation.getLocationName(),
                    "nortFileLocations[" + i + "].locationName",
                    new Object[] {"Name", MAXIMUM_CHARACTER_1024});
                ValidationUtils.rejectIfEmptyOrWhitespace(
                    errors,
                    "nortFileLocations[" + i + "].locationName",
                    "error.required.field");
                validateFileExists(
                    errors,
                    "nortFileLocations[" + i + "].locationName",
                    bookDirectory,
                    fileLocation.getLocationName());

                // Check duplicate sequence numbers exist
                checkDuplicateSequenceNumber(
                    errors,
                    fileLocation.getSequenceNum(),
                    "nortFileLocations[" + i + "].sequenceNum",
                    sequenceChecker);
                i++;
            }

            if (i == 0) {
                errors.rejectValue(
                    "nortFileLocations",
                    "error.at.least.one",
                    new Object[] {"Content Set"},
                    "At Least 1 Content Set is required");
            }
        }
    }

    private boolean validateFileExists(
        final Errors errors,
        final String fieldName,
        final File directory,
        final String fileName) {
        if (StringUtils.isBlank(fileName)) {
            errors.rejectValue(fieldName, ERROR_REQUIRED);
            return false;
        } else {
            final File file = new File(directory, fileName);
            if (!file.exists()) {
                errors.rejectValue(
                    fieldName,
                    "error.not.exist",
                    new Object[] {fileName, directory.getPath()},
                    "File/Directory does not exist in " + directory.getPath());
                return false;
            }
        }
        return true;
    }

    private void validateSplitDocuments(final EditBookDefinitionForm form, final Errors errors) {
        int i = 0;
        final List<String> tocGuids = new ArrayList<>();
        for (final SplitDocument document : form.getSplitDocuments()) {
            Objects.requireNonNull(document);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "splitDocuments[" + i + "].tocGuid", ERROR_REQUIRED);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "splitDocuments[" + i + "].note", ERROR_REQUIRED);

            String tocGuid = null;
            // Check if there are duplicate guids
            if (!document.isEmpty()) {
                tocGuid = document.getTocGuid();
            }
            if (StringUtils.isNotBlank(tocGuid)) {
                checkMaxLength(
                    errors,
                    MAXIMUM_CHARACTER_512,
                    document.getNote(),
                    "splitDocuments[" + i + "].note",
                    new Object[] {"Note", MAXIMUM_CHARACTER_512});
                if (tocGuids.contains(tocGuid)) {
                    errors.rejectValue(
                        "splitDocuments[" + i + "].tocGuid",
                        "error.duplicate",
                        new Object[] {"TOC/NORT GUID"},
                        "Duplicate Toc Guid");
                } else {
                    checkGuidFormat(errors, tocGuid, "splitDocuments[" + i + "].tocGuid");
                    tocGuids.add(tocGuid);
                }
            }
            i++;
        }
    }

    private void validateExcludeDocuments(final EditBookDefinitionForm form, final Errors errors) {
        // Validate Exclude Documents has all required fields
        int i = 0;
        final List<String> documentGuids = new ArrayList<>();
        for (final ExcludeDocument document : form.getExcludeDocuments()) {
            ValidationUtils
                .rejectIfEmptyOrWhitespace(errors, "excludeDocuments[" + i + "].documentGuid", ERROR_REQUIRED);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "excludeDocuments[" + i + "].note", ERROR_REQUIRED);
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_512,
                document.getNote(),
                "excludeDocuments[" + i + "].note",
                new Object[] {"Note", MAXIMUM_CHARACTER_512});

            // Check if there are duplicate guids
            final String documentGuid = document.getDocumentGuid();
            if (StringUtils.isNotBlank(documentGuid)) {
                if (documentGuids.contains(documentGuid)) {
                    errors.rejectValue(
                        "excludeDocuments[" + i + "].documentGuid",
                        "error.duplicate",
                        new Object[] {"Document Guid"},
                        "Duplicate Document Guid");
                } else {
                    checkGuidFormat(errors, documentGuid, "excludeDocuments[" + i + "].documentGuid");
                    documentGuids.add(documentGuid);
                }
            }
            i++;
        }
        if (form.isExcludeDocumentsUsed()) {
            if (form.getExcludeDocuments().size() == 0) {
                errors.rejectValue(
                    "excludeDocuments",
                    "error.used.selected",
                    new Object[] {"Exclude Documents"},
                    "Please select 'No' if Exclude Documents will not be used.");
            }
        }
    }

    private void validateRenameTocEntries(final EditBookDefinitionForm form, final Errors errors) {
        // Validate RenameTocEntry has all required fields
        int i = 0;
        final List<String> tocGuids = new ArrayList<>();
        for (final RenameTocEntry label : form.getRenameTocEntries()) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "renameTocEntries[" + i + "].tocGuid", ERROR_REQUIRED);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "renameTocEntries[" + i + "].oldLabel", ERROR_REQUIRED);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "renameTocEntries[" + i + "].newLabel", ERROR_REQUIRED);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "renameTocEntries[" + i + "].note", ERROR_REQUIRED);
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_1024,
                label.getOldLabel(),
                "renameTocEntries[" + i + "].oldLabel",
                new Object[] {"Old Label", MAXIMUM_CHARACTER_1024});
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_1024,
                label.getNewLabel(),
                "renameTocEntries[" + i + "].newLabel",
                new Object[] {"New Label", MAXIMUM_CHARACTER_1024});
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_512,
                label.getNote(),
                "renameTocEntries[" + i + "].note",
                new Object[] {"Note", MAXIMUM_CHARACTER_512});

            // Check if there are duplicate guids
            final String tocGuid = label.getTocGuid();
            if (StringUtils.isNotBlank(tocGuid)) {
                if (tocGuids.contains(tocGuid)) {
                    errors.rejectValue(
                        "renameTocEntries[" + i + "].tocGuid",
                        "error.duplicate",
                        new Object[] {"Guid"},
                        "Duplicate Guid");
                } else {
                    checkGuidFormat(errors, tocGuid, "renameTocEntries[" + i + "].tocGuid");
                    tocGuids.add(tocGuid);
                }
            }
            i++;
        }

        if (form.isRenameTocEntriesUsed()) {
            if (form.getRenameTocEntries().size() == 0) {
                errors.rejectValue(
                    "renameTocEntries",
                    "error.used.selected",
                    new Object[] {"Rename TOC Labels"},
                    "Please select 'No' if Rename TOC Labels will not be used.");
            }
        }
    }

    private void validateTableViewers(final EditBookDefinitionForm form, final Errors errors) {
        // Validate Table Viewers has all required fields
        int i = 0;
        final List<String> documentGuids = new ArrayList<>();
        for (final TableViewer document : form.getTableViewers()) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tableViewers[" + i + "].documentGuid", ERROR_REQUIRED);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tableViewers[" + i + "].note", ERROR_REQUIRED);
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_512,
                document.getNote(),
                "tableViewers[" + i + "].note",
                new Object[] {"Note", MAXIMUM_CHARACTER_512});

            // Check if there are duplicate guids
            final String documentGuid = document.getDocumentGuid();
            if (StringUtils.isNotBlank(documentGuid)) {
                if (documentGuids.contains(documentGuid)) {
                    errors.rejectValue(
                        "tableViewers[" + i + "].documentGuid",
                        "error.duplicate",
                        new Object[] {"Document Guid"},
                        "Duplicate Document Guid");
                } else {
                    checkGuidFormat(errors, documentGuid, "tableViewers[" + i + "].documentGuid");
                    documentGuids.add(documentGuid);
                }
            }
            i++;
        }
        if (form.isTableViewersUsed()) {
            if (form.getTableViewers().size() == 0) {
                errors.rejectValue(
                    "tableViewers",
                    "error.used.selected",
                    new Object[] {"Table Viewer"},
                    "Please select 'No' if Table Viewer will not be used.");
            }
        }
    }

    private void validateDocumentCopyrights(final EditBookDefinitionForm form, final Errors errors) {
        // Validate document copyright has all required fields
        int i = 0;
        final List<String> copyrightGuids = new ArrayList<>();
        for (final DocumentCopyright documentCopyright : form.getDocumentCopyrights()) {
            ValidationUtils
                .rejectIfEmptyOrWhitespace(errors, "documentCopyrights[" + i + "].copyrightGuid", ERROR_REQUIRED);
            ValidationUtils
                .rejectIfEmptyOrWhitespace(errors, "documentCopyrights[" + i + "].newText", ERROR_REQUIRED);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "documentCopyrights[" + i + "].note", ERROR_REQUIRED);
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_512,
                documentCopyright.getNote(),
                "documentCopyrights[" + i + "].note",
                new Object[] {"Note", MAXIMUM_CHARACTER_512});
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_512,
                documentCopyright.getNewText(),
                "documentCopyrights[" + i + "].newText",
                new Object[] {"Note", MAXIMUM_CHARACTER_512});

            // Check if there are duplicate guids
            final String documentGuid = documentCopyright.getCopyrightGuid();
            if (StringUtils.isNotBlank(documentGuid)) {
                if (copyrightGuids.contains(documentGuid)) {
                    errors.rejectValue(
                        "documentCopyrights[" + i + "].copyrightGuid",
                        "error.duplicate",
                        new Object[] {"Copyright Guid"},
                        "Duplicate Copyright Guid");
                } else {
                    checkGuidFormat(errors, documentGuid, "documentCopyrights[" + i + "].copyrightGuid");
                    copyrightGuids.add(documentGuid);
                }
            }
            i++;
        }
    }

    private void validateDocumentCurrencies(final EditBookDefinitionForm form, final Errors errors) {
        // Validate document currency has all required fields
        int i = 0;
        final List<String> currencyGuids = new ArrayList<>();
        for (final DocumentCurrency documentCurrency : form.getDocumentCurrencies()) {
            ValidationUtils
                .rejectIfEmptyOrWhitespace(errors, "documentCurrencies[" + i + "].currencyGuid", ERROR_REQUIRED);
            ValidationUtils
                .rejectIfEmptyOrWhitespace(errors, "documentCurrencies[" + i + "].newText", ERROR_REQUIRED);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "documentCurrencies[" + i + "].note", ERROR_REQUIRED);
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_512,
                documentCurrency.getNote(),
                "documentCurrencies[" + i + "].note",
                new Object[] {"Note", MAXIMUM_CHARACTER_512});
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_512,
                documentCurrency.getNewText(),
                "documentCurrencies[" + i + "].newText",
                new Object[] {"Note", MAXIMUM_CHARACTER_512});

            // Check if there are duplicate guids
            final String documentGuid = documentCurrency.getCurrencyGuid();
            if (StringUtils.isNotBlank(documentGuid)) {
                if (currencyGuids.contains(documentGuid)) {
                    errors.rejectValue(
                        "documentCurrencies[" + i + "].currencyGuid",
                        "error.duplicate",
                        new Object[] {"Currency Guid"},
                        "Duplicate Currency Guid");
                } else {
                    checkGuidFormat(errors, documentGuid, "documentCurrencies[" + i + "].currencyGuid");
                    currencyGuids.add(documentGuid);
                }
            }
            i++;
        }
    }

    private void validateAdditionalFrontMatter(final EditBookDefinitionForm form, final Errors errors) {
        // Sort the list before validations
        final List<FrontMatterPage> pages = form.getFrontMatters();
        Collections.sort(pages);
        form.setFrontMatters(pages);

        // Check max character and required fields for Front Matter
        int i = 0;
        final List<Integer> pageSequenceChecker = new ArrayList<>();
        for (final FrontMatterPage page : pages) {
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_1024,
                page.getPageTocLabel(),
                "frontMatters[" + i + "].pageTocLabel",
                new Object[] {"Page TOC Label", MAXIMUM_CHARACTER_1024});
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_1024,
                page.getPageHeadingLabel(),
                "frontMatters[" + i + "].pageHeadingLabel",
                new Object[] {"Page Heading Label", MAXIMUM_CHARACTER_1024});
            ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "frontMatters[" + i + "].sequenceNum",
                "error.required.field",
                new Object[] {"Sequence Number"});
            ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "frontMatters[" + i + "].pageTocLabel",
                "error.required.field",
                new Object[] {"Page TOC Label"});
            // Check duplicate sequence numbers exist
            checkDuplicateSequenceNumber(
                errors,
                page.getSequenceNum(),
                "frontMatters[" + i + "].sequenceNum",
                pageSequenceChecker);

            // Check Front Matter sections for max characters and required fields
            int j = 0;
            final List<Integer> sectionSequenceChecker = new ArrayList<>();

            // Sort the list before validations
            final List<FrontMatterSection> sections = page.getFrontMatterSections();
            Collections.sort(sections);
            page.setFrontMatterSections(sections);
            for (final FrontMatterSection section : sections) {
                checkMaxLength(
                    errors,
                    MAXIMUM_CHARACTER_1024,
                    section.getSectionHeading(),
                    "frontMatters[" + i + "].frontMatterSections[" + j + "].sectionHeading",
                    new Object[] {"Section Heading", MAXIMUM_CHARACTER_1024});
                ValidationUtils.rejectIfEmptyOrWhitespace(
                    errors,
                    "frontMatters[" + i + "].frontMatterSections[" + j + "].sequenceNum",
                    "error.required.field",
                    new Object[] {"Sequence Number"});
                // Check duplicate sequence numbers exist
                checkDuplicateSequenceNumber(
                    errors,
                    section.getSequenceNum(),
                    "frontMatters[" + i + "].frontMatterSections[" + j + "].sequenceNum",
                    sectionSequenceChecker);

                // Check Front Matter Pdf for max characters and required fields
                int k = 0;
                final List<Integer> pdfSequenceChecker = new ArrayList<>();

                // Sort the list before validations
                final List<FrontMatterPdf> pdfs = section.getPdfs();
                Collections.sort(pdfs);
                section.setPdfs(pdfs);
                for (final FrontMatterPdf pdf : pdfs) {
                    String pdfFileNameField = "frontMatters[" + i + "].frontMatterSections[" + j + "].pdfs[" + k + "].pdfFilename";
                    String pdfFileName = pdf.getPdfFilename();
                    if (StringUtils.isNotEmpty(pdfFileName)) {
                        validatePdfFileName(pdfFileName, pdfFileNameField, errors);
                    }
                    checkMaxLength(
                        errors,
                        MAXIMUM_CHARACTER_1024,
                        pdf.getPdfLinkText(),
                        "frontMatters[" + i + "].frontMatterSections[" + j + "].pdfs[" + k + "].pdfLinkText",
                        new Object[] {"PDF Link Text", MAXIMUM_CHARACTER_1024});
                    ValidationUtils.rejectIfEmptyOrWhitespace(
                        errors,
                        "frontMatters[" + i + "].frontMatterSections[" + j + "].pdfs[" + k + "].sequenceNum",
                        "error.required.field",
                        new Object[] {"Sequence Number"});
                    // Check duplicate sequence numbers exist
                    checkDuplicateSequenceNumber(
                        errors,
                        pdf.getSequenceNum(),
                        "frontMatters[" + i + "].frontMatterSections[" + j + "].pdfs[" + k + "].sequenceNum",
                        pdfSequenceChecker);

                    // Check both fields of PDF is filled
                    if (StringUtils.isBlank(pdfFileName) || StringUtils.isBlank(pdf.getPdfLinkText())) {
                        errors.rejectValue(
                            pdfFileNameField,
                            "error.required.pdf");
                    }
                    k++;
                }
                j++;
            }
            i++;
        }
    }

    private void validatePdfFileName(final String pdfFileName, final String pdfFileNameField, final Errors errors) {
        if (pdfFileNameValidator.isFileExtensionNotPdf(pdfFileName)) {
            errors.rejectValue(pdfFileNameField, WRONG_PDF_FILE_EXTENSION);
        }
        checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_1024,
                pdfFileName,
                pdfFileNameField,
                new Object[] {"PDF Filename", MAXIMUM_CHARACTER_1024});
        if (pdfFileNameValidator.isFileNameContainsForbiddenCharacters(pdfFileName)) {
            errors.rejectValue(pdfFileNameField, FORBIDDEN_CHARACTERS_IN_PDF_NAME);
        }
    }

    private void validateProviewKeywords(final EditBookDefinitionForm form, final Errors errors) {
        final List<KeywordTypeCode> keywordTypeCodes = keywordTypeCodeSevice.getAllKeywordTypeCodes();

        // Validate that required keyword are selected
        keywordTypeCodes.stream()
            .filter(code -> code.getIsRequired() && !code.getValues().isEmpty() &&
                    (getPublisherForValidation(form.getPublisher()).equals(code.getPublisher()) || ALL_PUBLISHERS.equals(code.getPublisher())))
            .map(KeywordTypeCode::getId)
            .map(
                keywordTypeId -> new ImmutablePair<>(
                    keywordTypeId,
                    form.getKeywords().computeIfAbsent(keywordTypeId, k -> Collections.emptyList())))
            .filter(idValuesPair -> idValuesPair.getRight().isEmpty() || idValuesPair.getRight().contains(-1L))
            .forEach(idValuesPair -> errors.rejectValue("keywords[" + idValuesPair.getLeft() + "]", ERROR_REQUIRED));

        validateSubjectMatterKeywords(form, errors, keywordTypeCodes);
    }

    private void validateSubjectMatterKeywords(final EditBookDefinitionForm form, final Errors errors, final List<KeywordTypeCode> keywordTypeCodes) {
        // Validate that subject matter keywords number does not exceed predefined value
        keywordTypeCodes.stream()
            .filter(code -> getPublisherForValidation(form.getPublisher()).equals(code.getPublisher()) && WebConstants.KEY_SUBJECT_MATTER.equalsIgnoreCase(code.getBaseName())) // add subject matter Canada
            .map(KeywordTypeCode::getId)
            .findAny()
            .ifPresent(subjectKeywordTypeId -> validateMaxNumberOfSubjects(form, errors, subjectKeywordTypeId));
    }

    @NotNull
    private String getPublisherForValidation(final String publisher) {
        if (publisher == null || !REGISTERED_PUBLISHERS.contains(publisher)) {
            return USCL_PUBLISHER_NAME;
        } else {
            return publisher;
        }
    }

    private void validateMaxNumberOfSubjects(final EditBookDefinitionForm form, final Errors errors, final long subjectKeywordTypeId) {
        if (form.getKeywords().computeIfAbsent(subjectKeywordTypeId, k -> Collections.emptyList()).size() > MAX_NUMBER_SUBJECT_KEYWORDS) {
            errors.rejectValue("keywords[" + subjectKeywordTypeId + "]", ERROR_KEYWORD_MAX_SUBJECS_NUMBER_EXCEEDED);
        }
    }

    private void validateReleaseNotes(String releaseNotes, final Errors errors) {
        if (!StringUtils.isBlank(releaseNotes) && countReleaseNotesLength(releaseNotes) > MAXIMUM_CHARACTER_2000) {
            errors.rejectValue(
                    "releaseNotes",
                    "error.max.length",
                    new Object[] {"Release notes", MAXIMUM_CHARACTER_2000},
                    "Must be maximum of " + MAXIMUM_CHARACTER_2000 + " characters or under");
        }
    }

    private int countReleaseNotesLength(String releaseNotes) {
        int releaseNotesLength = 0;
        releaseNotesLength += StringUtils.countMatches(releaseNotes, CRLF) * LINE_BREAK_LENGTH;
        releaseNotes = releaseNotes.replace(CRLF, "");
        releaseNotesLength += StringUtils.countMatches(releaseNotes, LF) * LINE_BREAK_LENGTH;
        releaseNotes = releaseNotes.replace(LF, "");
        releaseNotesLength += releaseNotes.replaceAll(" +", " ").length();
        return releaseNotesLength;
    }

    private void validateIndexFields(final EditBookDefinitionForm form, final Errors errors) {
        if (form.isIndexIncluded() && SourceType.TOC.equals(form.getSourceType())) {
            checkSpecialCharacters(errors, form.getTocCollectionName(), "indexTocCollectionName", true);
            checkGuidFormat(errors, form.getRootTocGuid(), "indexTocRootGuid");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "indexTocCollectionName", ERROR_REQUIRED);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "indexTocRootGuid", ERROR_REQUIRED);

            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_64,
                form.getIndexTocCollectionName(),
                "indexTocCollectionName",
                new Object[] {"Index Collection", MAXIMUM_CHARACTER_64});
            checkMaxLength(
                errors,
                MAXIMUM_CHARACTER_64,
                form.getIndexTocRootGuid(),
                "indexTocRootGuid",
                new Object[] {"Index Root Guid", MAXIMUM_CHARACTER_64});
        }
    }

    private void validateProdOnlyRequirements(final EditBookDefinitionForm form, final Errors errors) {
        // Check cover image exists
        if (environmentName.equalsIgnoreCase(CoreConstants.PROD_ENVIRONMENT_NAME)) {
            if (StringUtils.isNotBlank(form.getTitleId())) {
                fileExist(
                    errors,
                    form.createCoverImageName(),
                    nasFileSystem.getCoverImagesDirectory().getAbsolutePath(),
                    "validateForm",
                    "error.not.exist");
                if (form.getPilotBookStatus() == PilotBookStatus.TRUE) {
                    fileExist(
                        errors,
                        form.createPilotBookCsvName(),
                        nasFileSystem.getPilotBookCsvDirectory().getAbsolutePath(),
                        "pilotBook",
                        "error.pilot.boo.file.not.exist");
                }
            }
            // Check all pdfs on Front Matter
            int i = 0;
            for (final FrontMatterPage page : form.getFrontMatters()) {
                int j = 0;
                for (final FrontMatterSection section : page.getFrontMatterSections()) {
                    int k = 0;
                    for (final FrontMatterPdf pdf : section.getPdfs()) {
                        final String filename = pdf.getPdfFilename();
                        if (StringUtils.isNotBlank(filename)) {
                            checkPdfExists(
                                    filename,
                                    form.getPublisher(),
                                    "frontMatters[" + i + "].frontMatterSections[" + j + "].pdfs[" + k + "].pdfFilename",
                                    errors);
                        }
                        k++;
                    }
                    j++;
                }
                i++;
            }
        }
    }

    private void checkPdfExists(final String fileName, final String publisher, final String fieldName,
        final Errors errors) {
        final File pdfInCwFolder = new File(nasFileSystem.getFrontMatterCwPdfDirectory(), fileName);
        final File pdfInGeneralFolder = new File(nasFileSystem.getFrontMatterUsclPdfDirectory(), fileName);
        if (CW_PUBLISHER_NAME.equalsIgnoreCase(publisher)) {
            if (!pdfInCwFolder.exists() && !pdfInGeneralFolder.exists()) {
                rejectPdfFileField(errors, fieldName, fileName, nasFileSystem.getFrontMatterCwPdfDirectory().getAbsolutePath());
            }
        } else {
            if (!pdfInGeneralFolder.exists()) {
                rejectPdfFileField(errors, fieldName, fileName, nasFileSystem.getFrontMatterUsclPdfDirectory().getAbsolutePath());
            }
        }
    }

    private void fileExist(
        final Errors errors,
        final String filename,
        final String location,
        final String fieldName,
        final String errorMessage) {
        final File file = new File(location, filename);
        if (!file.isFile()) {
            errors.rejectValue(
                fieldName,
                errorMessage,
                new Object[] {filename, location},
                "File does not exist on server.");
        }
    }

    private void rejectPdfFileField(final Errors errors, final String fieldName, final String fileName,
        final String location) {
        errors.rejectValue(
                fieldName,
                ERROR_DOES_NOT_EXIST,
                new Object[] {fileName, location},
                PDF_DOES_NOT_EXIST_MESSAGE);
    }

    private void checkUniqueTitleId(final Errors errors, final String titleId) {
        final BookDefinition newBookDef = bookDefinitionService.findBookDefinitionByTitle(titleId);

        if (newBookDef != null) {
            errors.rejectValue("titleId", "error.titleid.exist");
        }
    }

    private void checkDuplicateSequenceNumber(
        final Errors errors,
        final Integer sequenceNumber,
        final String fieldName,
        final List<Integer> sequenceChecker) {
        // Check duplicate sequence numbers exist
        if (sequenceNumber != null) {
            if (sequenceChecker.contains(sequenceNumber)) {
                errors.rejectValue(fieldName, "error.sequence.number");
            } else {
                sequenceChecker.add(sequenceNumber);
            }
        }
    }

    private void checkPrintSetNumber(final Errors errors, final String text, final String fieldName) {
        if (StringUtils.isNotEmpty(text)) {
            final Pattern pattern = Pattern.compile("\\d");
            final Matcher matcher = pattern.matcher(text);
            if (!matcher.find()) {
                errors.rejectValue(fieldName, "typeMismatch.java.lang.Long");
            }
        }
    }

    private void checkPrintSubNumber(final Errors errors, final String text, final String fieldName) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, fieldName, ERROR_REQUIRED);
        if (StringUtils.isNotEmpty(text)) {
            final Pattern pattern = Pattern.compile("\\d");
            final Matcher matcher = pattern.matcher(text);
            if (!matcher.find()) {
                errors.rejectValue(fieldName, "typeMismatch.java.lang.Long");
            }
        }
    }

    private void checkIsbnNumber(final Errors errors, final String text, final String fieldName) {
        if (StringUtils.isNotEmpty(text)) {
            try {
                isbnValidator.validateIsbn(text);
            } catch (EBookException e) {
                errors.rejectValue(fieldName, e.getMessage());
            }
        }
    }

    private void checkIssnNumber(final Errors errors, final String text, final String fieldName) {
        if (StringUtils.isNotEmpty(text)) {
            try {
                issnValidator.validateIssn(text);
            } catch (EBookException e) {
                errors.rejectValue(fieldName, e.getMessage());
            }
        }
    }

    private void validatePublicationCutOffDate(final Errors errors, final EditBookDefinitionForm form) {
        if (form.isPublicationCutoffDateUsed()) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, PUB_CUTOFF_DATE, "error.publication.cutoff.date");
            String publicationCutOffDate = form.getPublicationCutoffDate();
            checkDateFormat(errors, publicationCutOffDate, PUB_CUTOFF_DATE);
            if (errors.getFieldError(PUB_CUTOFF_DATE) == null) {
                validatePublicationCutOffDateValue(errors, LocalDate.parse(publicationCutOffDate, DATE_FORMATTER));
            }
        }
    }

    private void validatePublicationCutOffDateValue(final Errors errors, final LocalDate publicationCutOffDate) {
        if (!dateService.isDateGreaterThanToday(publicationCutOffDate)) {
            String formattedDate = dateService.getFormattedServerDateTime();
            final Object[] args = {formattedDate};
            errors.rejectValue(PUB_CUTOFF_DATE, "error.publication.cutoff.date.value", args, ERROR_PUBLICATION_CUT_OFF_DATE + formattedDate);
        }
    }

    private void validatePrintComponentsSplitters(final Collection<PrintComponent> printComponents, final Errors errors) {
        final List<Integer> splitterOrders = printComponents.stream()
            .filter(PrintComponent::getSplitter)
            .map(PrintComponent::getComponentOrder)
            .sorted()
            .collect(Collectors.toList());

        if (!printComponents.isEmpty() && printComponents.size() - splitterOrders.size() <= splitterOrders.size()) {
            errors.rejectValue(PRINT_COMPONENT, "error.print.component.splitter.count.exceeded");
        }

        validateSplitterInbound(splitterOrders, 1, errors, "error.print.component.splitter.first");
        validateSplitterInbound(splitterOrders, printComponents.size(), errors, "error.print.component.splitter.last");
        validateSplitterPositions(splitterOrders, errors);
    }

    private void validateSplitterInbound(final List<Integer> splitterOrders, final Integer position,
                                         final Errors errors, final String messageId) {
        splitterOrders.stream()
            .filter(position::equals)
            .findAny()
            .ifPresent(order -> errors.rejectValue(PRINT_COMPONENT, messageId));
    }

    private void validateSplitterPositions(final List<Integer> splitterOrders, final Errors errors) {
        for (int index = 0; index < splitterOrders.size() - 1;) {
            final Integer currentOrder = splitterOrders.get(index++) + 1;
            if (currentOrder.equals(splitterOrders.get(index))) {
                errors.rejectValue(PRINT_COMPONENT, "error.print.component.splitter.followed");
                break;
            }
        }
    }

    private void validateSubstitutionTocLevel(final EditBookDefinitionForm form, final Errors errors) {
        if (form.isSubstituteTocHeaders()) {
            if (form.getSubstituteTocHeadersLevel() == null) {
                errors.rejectValue(SUBSTITUTE_TOC_HEADERS_LEVEL, ERROR_REQUIRED);
            } else if (form.getSubstituteTocHeadersLevel() <= 0) {
                errors.rejectValue(SUBSTITUTE_TOC_HEADERS_LEVEL, ERROR_POSITIVE_INTEGER);
            }
        }
    }

    private boolean isSplitBook(final EditBookDefinitionForm form) {
        return form.getSourceType() == SourceType.XPP
            ? form.getPrintComponentsCollection().stream().anyMatch(PrintComponent::getSplitter)
                : form.isSplitBook();
    }
}
