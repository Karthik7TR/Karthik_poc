package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import static org.apache.commons.text.StringEscapeUtils.escapeXml10;
import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCopyright;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCurrency;
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
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.TableViewer;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.AutoPopulatingList;

public class EditBookDefinitionForm {
    private static final String NULL_VAL = "null";
    public static final String FORM_NAME = "editBookDefinitionForm";

    private static final int PUBLISHER_INDEX = 0;
    private static final int PRODUCT_CODE_INDEX = 1;
    private static final int TITLE_NAME_INDEX = 2;

    private Long bookdefinitionId;
    private String titleId;
    private String proviewDisplayName;
    private String copyright;
    private String copyrightPageText;
    private String materialId;
    private List<PilotBook> pilotBookInfo;
    private List<Author> authorInfo;
    private List<NortFileLocation> nortFileLocations;
    private List<FrontMatterPage> frontMatters;
    private boolean isExcludeDocumentsUsed;
    private Collection<ExcludeDocument> excludeDocuments;
    private Collection<ExcludeDocument> excludeDocumentsCopy;
    private boolean isRenameTocEntriesUsed;
    private Collection<RenameTocEntry> renameTocEntries;
    private Collection<RenameTocEntry> renameTocEntriesCopy;
    private boolean isTableViewersUsed;
    private Collection<TableViewer> tableViewers;
    private Collection<TableViewer> tableViewersCopy;
    private Collection<DocumentCopyright> documentCopyrights;
    private Collection<DocumentCopyright> documentCopyrightsCopy;
    private Collection<DocumentCurrency> documentCurrencies;
    private Collection<DocumentCurrency> documentCurrenciesCopy;
    private String codesWorkbenchBookName;
    private SourceType sourceType;

    private boolean isFinalStage;
    private boolean isAuthorDisplayVertical;
    private String frontMatterTocLabel;

    private String rootTocGuid;
    private String docCollectionName;
    private String tocCollectionName;
    private String nortDomain;
    private String nortFilterView;
    private Long contentTypeId;
    private String fmThemeText;
    private String printSetNumber;
    private String printSubNumber;
    private String isbn;
    private boolean isPublicationCutoffDateUsed;
    private String publicationCutoffDate;
    private boolean includeAnnotations;
    private boolean includeNotesOfDecisions;
    @Getter @Setter
    private String notes;
    private boolean useReloadContent;
    private boolean isInsTagStyleEnabled;
    private boolean isDelTagStyleEnabled;
    private boolean isRemoveEditorNoteHeading;
    private boolean isSplitBook;
    private boolean isSplitTypeAuto;
    private Integer splitEBookParts;
    private Collection<SplitDocument> splitDocuments;
    private Collection<PrintComponent> printComponents;

    // Proview Group information
    private boolean isGroupsEnabled;
    private String subGroupHeading;
    private String groupName;

    private String publishDateText;

    // Keywords used in Proview
    private Map<Long, Collection<Long>> keywords;

    private String currency;
    private String additionalTrademarkInfo;
    private boolean isComplete;
    private boolean keyCiteToplineFlag;
    private boolean autoUpdateSupport;
    private boolean searchIndex;
    private boolean enableCopyFeatureFlag;
    private PilotBookStatus pilotBookStatus;

    // Fully qualified title ID parts
    private String publisher;
    private String state;
    private String pubType;
    private String pubAbbr;
    private String jurisdiction;
    private String pubInfo;
    private String productCode;
    private String comment;
    private EbookName frontMatterTitle = new EbookName();
    private EbookName frontMatterSubtitle = new EbookName();
    private EbookName frontMatterSeries = new EbookName();

    /**
     * Used to preview front matter, holds the FM page sequence number that
     * uniquely identifies what page the user want to preview.
     */
    private Long selectedFrontMatterPreviewPage;

    private boolean validateForm;

    private ObjectMapper jsonMapper;
    private UuidGenerator uuidGenerator;

    private boolean colorPrintComponentTable;

    public EditBookDefinitionForm() {
        super();
        authorInfo = new AutoPopulatingList<>(Author.class);
        pilotBookInfo = new AutoPopulatingList<>(PilotBook.class);
        nortFileLocations = new AutoPopulatingList<>(NortFileLocation.class);
        frontMatters = new AutoPopulatingList<>(FrontMatterPage.class);
        excludeDocuments = new AutoPopulatingList<>(ExcludeDocument.class);
        excludeDocumentsCopy = new AutoPopulatingList<>(ExcludeDocument.class);
        renameTocEntries = new AutoPopulatingList<>(RenameTocEntry.class);
        renameTocEntriesCopy = new AutoPopulatingList<>(RenameTocEntry.class);
        tableViewers = new AutoPopulatingList<>(TableViewer.class);
        tableViewersCopy = new AutoPopulatingList<>(TableViewer.class);
        documentCopyrights = new AutoPopulatingList<>(DocumentCopyright.class);
        documentCopyrightsCopy = new AutoPopulatingList<>(DocumentCopyright.class);
        documentCurrencies = new AutoPopulatingList<>(DocumentCurrency.class);
        documentCurrenciesCopy = new AutoPopulatingList<>(DocumentCurrency.class);
        keywords = new HashMap<>();
        isComplete = false;
        validateForm = false;
        keyCiteToplineFlag = true;
        autoUpdateSupport = true;
        searchIndex = true;
        enableCopyFeatureFlag = true;
        isExcludeDocumentsUsed = false;
        isRenameTocEntriesUsed = false;
        isTableViewersUsed = false;
        isFinalStage = true;
        isPublicationCutoffDateUsed = false;
        includeAnnotations = false;
        includeNotesOfDecisions = true;
        notes = StringUtils.EMPTY;
        useReloadContent = false;
        pilotBookStatus = PilotBookStatus.FALSE;
        copyright = "\u00A9";
        frontMatterTocLabel = "Publishing Information";
        publishDateText = "see Title page for currentness";
        sourceType = SourceType.NORT;
        isInsTagStyleEnabled = false;
        isDelTagStyleEnabled = false;
        isRemoveEditorNoteHeading = false;
        isSplitBook = false;
        isSplitTypeAuto = true;
        splitDocuments = new AutoPopulatingList<>(SplitDocument.class);
        printComponents = new AutoPopulatingList<>(PrintComponent.class);
        isGroupsEnabled = true;

        jsonMapper = new ObjectMapper();
        uuidGenerator = new UuidGenerator();
    }

    /**
     * Reset some book definition fields before copying in to the form
     *
     * @param bookDef
     */
    public void copyBookDefinition(final BookDefinition bookDef, final List<KeywordTypeCode> keywordCodes) {
        bookDef.setEbookDefinitionId(null);
        bookDef.setProviewDisplayName(null);
        bookDef.setIsbn(null);
        bookDef.setMaterialId(null);
        bookDef.setRootTocGuid(null);
        bookDef.setNortFilterView(null);
        bookDef.setEbookDefinitionCompleteFlag(false);
        bookDef.setFrontMatterPages(new AutoPopulatingList<>(FrontMatterPage.class));
        bookDef.setExcludeDocuments(new AutoPopulatingList<>(ExcludeDocument.class));
        bookDef.setSplitDocuments(new AutoPopulatingList<>(SplitDocument.class));
        bookDef.setPrintComponents(new AutoPopulatingList<>(PrintComponent.class));
        bookDef.setRenameTocEntries(new AutoPopulatingList<>(RenameTocEntry.class));
        bookDef.setTableViewers(new AutoPopulatingList<>(TableViewer.class));
        bookDef.setDocumentCopyrights(new AutoPopulatingList<>(DocumentCopyright.class));
        bookDef.setDocumentCurrencies(new AutoPopulatingList<>(DocumentCurrency.class));
        bookDef.setPilotBookStatus(PilotBookStatus.FALSE);
        bookDef.setUseReloadContent(false);
        bookDef.setSubGroupHeading(null);

        // Need to null surrogate and foreign keys.
        // New keys will be made when Copy of Book Definition is saved.
        for (final EbookName name : bookDef.getEbookNames()) {
            name.setEbookDefinition(null);
            name.setEbookNameId(null);
        }
        for (final Author author : bookDef.getAuthors()) {
            author.setAuthorId(null);
            author.setEbookDefinition(null);
        }
        for (final NortFileLocation fileLocation : bookDef.getNortFileLocations()) {
            fileLocation.setNortFileLocationId(null);
            fileLocation.setEbookDefinition(null);
        }

        initialize(bookDef, keywordCodes);
    }

    public void initialize(final BookDefinition book, final List<KeywordTypeCode> keywordCodes) {
        if (book != null) {
            bookdefinitionId = book.getEbookDefinitionId();
            titleId = book.getFullyQualifiedTitleId();
            proviewDisplayName = book.getProviewDisplayName();
            copyright = book.getCopyright();
            copyrightPageText = book.getCopyrightPageText();
            materialId = book.getMaterialId();
            rootTocGuid = book.getRootTocGuid();
            tocCollectionName = book.getTocCollectionName();
            docCollectionName = book.getDocCollectionName();
            nortDomain = book.getNortDomain();
            nortFilterView = book.getNortFilterView();

            final Predicate<String> nullValuePredicate = NULL_VAL::equalsIgnoreCase;
            printSetNumber = Optional.ofNullable(book.getPrintSetNumber())
                .filter(nullValuePredicate.negate()).orElse(null);
            printSubNumber = Optional.ofNullable(book.getPrintSubNumber())
                .filter(nullValuePredicate.negate()).orElse(null);

            isbn = book.getIsbn();
            authorInfo = book.getAuthors();
            pilotBookInfo = book.getPilotBooks();
            nortFileLocations = book.getNortFileLocations();
            frontMatters = book.getFrontMatterPages();
            publishDateText = book.getPublishDateText();
            currency = book.getCurrency();
            notes = book.getNotes();
            isComplete = book.getEbookDefinitionCompleteFlag();
            keyCiteToplineFlag = book.getKeyciteToplineFlag();
            autoUpdateSupport = book.getAutoUpdateSupportFlag();
            searchIndex = book.getSearchIndexFlag();
            isAuthorDisplayVertical = book.isAuthorDisplayVertical();
            enableCopyFeatureFlag = book.getEnableCopyFeatureFlag();
            pilotBookStatus = book.getPilotBookStatus();
            frontMatterTocLabel = book.getFrontMatterTocLabel();
            additionalTrademarkInfo = book.getAdditionalTrademarkInfo();
            excludeDocuments = book.getExcludeDocuments();
            excludeDocumentsCopy = book.getExcludeDocuments();
            splitDocuments = book.getSplitDocuments();
            printComponents = book.getPrintComponents();
            renameTocEntries = book.getRenameTocEntries();
            renameTocEntriesCopy = book.getRenameTocEntries();
            tableViewers = book.getTableViewers();
            tableViewersCopy = book.getTableViewers();
            documentCopyrights = book.getDocumentCopyrights();
            documentCopyrightsCopy = book.getDocumentCopyrights();
            documentCurrencies = book.getDocumentCurrencies();
            documentCurrenciesCopy = book.getDocumentCurrencies();
            includeAnnotations = book.getIncludeAnnotations();
            includeNotesOfDecisions = book.getIncludeNotesOfDecisions();
            isFinalStage = book.isFinalStage();
            useReloadContent = book.getUseReloadContent();
            sourceType = book.getSourceType();
            codesWorkbenchBookName = book.getCwbBookName();
            isInsTagStyleEnabled = book.isInsStyleFlag();
            isDelTagStyleEnabled = book.isDelStyleFlag();
            isRemoveEditorNoteHeading = book.isRemoveEditorNoteHeadFlag();
            isSplitBook = book.isSplitBook();
            isSplitTypeAuto = book.isSplitTypeAuto();
            splitEBookParts = book.getSplitEBookParts();
            fmThemeText = book.getFrontMatterTheme();

            // Determine if ProView groups are set
            if (StringUtils.isBlank(book.getGroupName())) {
                isGroupsEnabled = false;
            } else {
                groupName = book.getGroupName();
                subGroupHeading = book.getSubGroupHeading();
            }

            // Determine if ExcludeDocuments are present in Book Definition
            if (book.getExcludeDocuments().size() > 0) {
                isExcludeDocumentsUsed = true;
            }

            // Determine if RenameTocEntries are present in Book Definition
            if (book.getRenameTocEntries().size() > 0) {
                isRenameTocEntriesUsed = true;
            }

            if (tableViewers.size() > 0) {
                isTableViewersUsed = true;
            }

            // Determine if Publish Cut-off Date is used
            final Date date = book.getPublishCutoffDate();
            if (date != null) {
                final SimpleDateFormat sdf = new SimpleDateFormat(CoreConstants.DATE_FORMAT_PATTERN);
                publicationCutoffDate = sdf.format(date);
                isPublicationCutoffDateUsed = true;
            }

            final Map<Long, List<Long>> bookKeywords = book.getKeywordTypeValues()
                    .stream()
                    .collect(Collectors.groupingBy(keyword -> keyword.getKeywordTypeCode().getId(),
                            Collectors.mapping(KeywordTypeValue::getId, Collectors.toList())));
            keywords.putAll(bookKeywords);

            setupFrontMatterNames(book.getEbookNames());

            parseTitleId(book);
        }
    }

    private void setupFrontMatterNames(final List<EbookName> names) {
        for (final EbookName name : names) {
            switch (name.getSequenceNum()) {
            case 1:
                frontMatterTitle = name;
                break;
            case 2:
                frontMatterSubtitle = name;
                break;
            case 3:
                frontMatterSeries = name;
                break;
            default:
                break;
            }
        }
    }

    public void loadBookDefinition(final BookDefinition book) throws ParseException {
        book.setEbookDefinitionId(bookdefinitionId);

        final List<Author> authors = new ArrayList<>();
        int i = 1;
        for (final Author author : authorInfo) {
            final Author authorCopy = new Author();
            authorCopy.copy(author);
            authorCopy.setEbookDefinition(book);
            // Update the sequence number to be in order
            authorCopy.setSequenceNum(i);
            authors.add(authorCopy);
            i++;
        }
        book.setAuthors(authors);

        final List<PilotBook> pilotBooks = new ArrayList<>();
        i = 1;
        for (final PilotBook pilotBook : pilotBookInfo) {
            final PilotBook pilotBookCopy = new PilotBook();
            pilotBookCopy.copy(pilotBook);
            pilotBookCopy.setEbookDefinition(book);
            // Update the sequence number to be in order
            pilotBookCopy.setSequenceNum(i);
            pilotBooks.add(pilotBookCopy);
            i++;
        }
        book.setPilotBooks(pilotBooks);

        // Add Front Matter Book Names
        final Set<EbookName> ebookNames = new HashSet<>();
        if (!frontMatterTitle.isEmpty()) {
            frontMatterTitle.setEbookDefinition(book);
            ebookNames.add(frontMatterTitle);
        }
        if (!frontMatterSubtitle.isEmpty()) {
            frontMatterSubtitle.setEbookDefinition(book);
            ebookNames.add(frontMatterSubtitle);
        }
        if (!frontMatterSeries.isEmpty()) {
            frontMatterSeries.setEbookDefinition(book);
            ebookNames.add(frontMatterSeries);
        }
        book.setEbookNames(ebookNames);

        final List<FrontMatterPage> pages = new ArrayList<>();
        i = 1;
        for (final FrontMatterPage page : frontMatters) {
            final FrontMatterPage pageCopy = new FrontMatterPage();
            pageCopy.copy(page);

            int j = 1;
            for (final FrontMatterSection section : pageCopy.getFrontMatterSections()) {
                int k = 1;
                for (final FrontMatterPdf pdf : section.getPdfs()) {
                    // Set foreign key on Pdf
                    pdf.setSection(section);
                    // Update the sequence numbers to be in order
                    pdf.setSequenceNum(k);
                    k++;
                }
                // Update the sequence numbers to be in order
                section.setSequenceNum(j);
                // Set foreign key on Section
                section.setFrontMatterPage(pageCopy);
                j++;
            }
            // Set foreign key on Page
            pageCopy.setEbookDefinition(book);
            // Update the sequence numbers to be in order
            pageCopy.setSequenceNum(i);
            pages.add(pageCopy);
            i++;
        }
        book.setFrontMatterPages(pages);

        // Compare with copy to determine if date needs update
        for (final ExcludeDocument document : excludeDocuments) {
            boolean exists = false;
            document.setBookDefinition(book);
            for (final ExcludeDocument documentCopy : excludeDocumentsCopy) {
                if (document.equals(documentCopy)) {
                    exists = true;
                }
            }
            // Update date
            if (!exists) {
                document.setLastUpdated(new Date());
            }
        }
        book.setExcludeDocuments(excludeDocuments);

        for (final SplitDocument document : splitDocuments) {
            document.setBookDefinition(book);
        }
        book.setSplitDocuments(splitDocuments);

        loadPrintComponents(book);

        // Compare with copy to determine if date needs update
        for (final RenameTocEntry label : renameTocEntries) {
            boolean exists = false;
            label.setBookDefinition(book);
            for (final RenameTocEntry labelCopy : renameTocEntriesCopy) {
                if (label.equals(labelCopy)) {
                    exists = true;
                }
            }
            // Update date
            if (!exists) {
                label.setLastUpdated(new Date());
            }
        }
        book.setRenameTocEntries(renameTocEntries);

        // Compare with copy to determine if date needs update
        for (final TableViewer document : tableViewers) {
            boolean exists = false;
            document.setBookDefinition(book);
            for (final TableViewer documentCopy : tableViewersCopy) {
                if (document.equals(documentCopy)) {
                    exists = true;
                }
            }
            // Update date
            if (!exists) {
                document.setLastUpdated(new Date());
            }
        }
        book.setTableViewers(tableViewers);

        for (final DocumentCopyright documentCopyright : documentCopyrights) {
            boolean exists = false;
            documentCopyright.setBookDefinition(book);
            for (final DocumentCopyright documentCopyrightCopy : documentCopyrightsCopy) {
                if (documentCopyright.equals(documentCopyrightCopy)) {
                    exists = true;
                }
            }
            // Update date
            if (!exists) {
                documentCopyright.setLastUpdated(new Date());
            }
        }
        book.setDocumentCopyrights(documentCopyrights);

        for (final DocumentCurrency documentCurrency : documentCurrencies) {
            boolean exists = false;
            documentCurrency.setBookDefinition(book);
            for (final DocumentCurrency documentCurrencyCopy : documentCurrenciesCopy) {
                if (documentCurrency.equals(documentCurrencyCopy)) {
                    exists = true;
                }
            }
            // Update date
            if (!exists) {
                documentCurrency.setLastUpdated(new Date());
            }
        }
        book.setDocumentCurrencies(documentCurrencies);

        book.setAutoUpdateSupportFlag(autoUpdateSupport);
        book.setCopyright(copyright);
        book.setCopyrightPageText(copyrightPageText);
        book.setCurrency(currency);
        book.setAdditionalTrademarkInfo(additionalTrademarkInfo);

        final DocumentTypeCode dtc = new DocumentTypeCode();
        dtc.setId(contentTypeId != null ? contentTypeId : 0L);
        book.setDocumentTypeCodes(dtc);
        book.setEbookDefinitionCompleteFlag(isComplete);
        book.setFullyQualifiedTitleId(titleId);
        book.setCoverImage(createCoverImageName());

        book.setIsbn(isbn);
        book.setEnableCopyFeatureFlag(enableCopyFeatureFlag);
        book.setPilotBookStatus(pilotBookStatus);
        book.setKeyciteToplineFlag(keyCiteToplineFlag);

        final Set<KeywordTypeValue> keywordValues = getKeywordValues(keywords);
        book.setKeywordTypeValues(keywordValues);

        book.setMaterialId(materialId);
        book.setNortDomain(nortDomain);
        book.setNortFilterView(nortFilterView);
        book.setPrintSetNumber(printSetNumber);
        book.setPrintSubNumber(printSubNumber);
        book.setProviewDisplayName(proviewDisplayName);

        // Parse Date
        final DateFormat formatter = new SimpleDateFormat(CoreConstants.DATE_FORMAT_PATTERN);
        final Date date = publicationCutoffDate != null ? formatter.parse(publicationCutoffDate) : null;
        book.setPublishCutoffDate(date);

        book.setPublishDateText(publishDateText);

        final PublisherCode publishercode = new PublisherCode();
        publishercode.setName(publisher);
        book.setPublisherCodes(publishercode);

        book.setRootTocGuid(rootTocGuid);
        book.setSearchIndexFlag(searchIndex);
        book.setTocCollectionName(tocCollectionName);
        book.setDocCollectionName(docCollectionName);
        book.setIsAuthorDisplayVertical(isAuthorDisplayVertical);
        book.setFrontMatterTocLabel(frontMatterTocLabel);
        book.setIncludeAnnotations(includeAnnotations);
        book.setIncludeNotesOfDecisions(includeNotesOfDecisions);
        book.setNotes(notes);
        book.setIsFinalStage(isFinalStage);
        book.setUseReloadContent(useReloadContent);

        book.setSourceType(sourceType);
        book.setCwbBookName(codesWorkbenchBookName);

        book.setIsInsStyleFlag(isInsTagStyleEnabled);
        book.setIsDelStyleFlag(isDelTagStyleEnabled);
        book.setIsRemoveEditorNoteHeadFlag(isRemoveEditorNoteHeading);
        book.setIsSplitBook(isSplitBook);
        book.setIsSplitTypeAuto(isSplitTypeAuto);
        book.setSplitEBookParts(splitEBookParts);
        book.setSubGroupHeading(subGroupHeading);
        book.setGroupName(groupName);

        final List<NortFileLocation> tempNortFileLocations = new ArrayList<>();
        i = 1;
        for (final NortFileLocation nortFileLocation : nortFileLocations) {
            final NortFileLocation fileLocationCopy = new NortFileLocation();
            fileLocationCopy.copy(nortFileLocation);
            fileLocationCopy.setEbookDefinition(book);
            // Update the sequence number to be in order
            fileLocationCopy.setSequenceNum(i);
            tempNortFileLocations.add(fileLocationCopy);
            i++;
        }
        book.setNortFileLocations(tempNortFileLocations);
        book.setFrontMatterTheme(fmThemeText);
    }

    private Set<KeywordTypeValue> getKeywordValues(final Map<Long, Collection<Long>> keywords) {
        return keywords.values()
            .stream()
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .filter(id -> id > 0) // exclude "None" field
            .map(keywordValueId -> {
                final KeywordTypeValue keywordValue = new KeywordTypeValue();
                keywordValue.setId(keywordValueId);
                return keywordValue;
            })
            .collect(Collectors.toSet());
    }

    private void loadPrintComponents(final BookDefinition book) {
        for (final PrintComponent printComponent : printComponents) {
            printComponent.setBookDefinition(book);
            if (printComponent.getPrintComponentId() == null) {
                printComponent.setPrintComponentId(uuidGenerator.generateUuid());
            }
            final String escapedName = escapeHtml4(unescapeHtml4(printComponent.getComponentName()));
            printComponent.setComponentName(escapedName);
        }
        book.setPrintComponents(printComponents);
    }

    private void parseTitleId(final BookDefinition book) {
        final DocumentTypeCode documentType = book.getDocumentTypeCodes();
        Objects.requireNonNull(documentType);
        contentTypeId = documentType.getId();

        // Parse titleId
        final String[] fullyqualifiedtitleArray = titleId.split("/");
        final String[] titleIdArray = fullyqualifiedtitleArray[TITLE_NAME_INDEX].split("_");

        publisher = fullyqualifiedtitleArray[PUBLISHER_INDEX];

        if (documentType.getName().equals(WebConstants.DOCUMENT_TYPE_ANALYTICAL)) {
            pubAbbr = titleIdArray[0];
            pubInfo = createPubInfo(documentType, titleIdArray);
        } else if (documentType.getName().equals(WebConstants.DOCUMENT_TYPE_COURT_RULES)) {
            state = titleIdArray[0];
            pubType = titleIdArray[1];
            pubInfo = createPubInfo(documentType, titleIdArray);
        } else if (documentType.getName().equals(WebConstants.DOCUMENT_TYPE_SLICE_CODES)) {
            jurisdiction = titleIdArray[0];
            pubInfo = createPubInfo(documentType, titleIdArray);
        } else {
            productCode = fullyqualifiedtitleArray[PRODUCT_CODE_INDEX];
            pubInfo = createPubInfo(documentType, titleIdArray);
        }
    }

    private String createPubInfo(final DocumentTypeCode documentType, final String[] titleId) {
        final int index;
        final StringBuilder publicationInfo = new StringBuilder();

        if (documentType.getName().equalsIgnoreCase(WebConstants.DOCUMENT_TYPE_COURT_RULES)) {
            index = 2;
        } else if (documentType.getName().equalsIgnoreCase(WebConstants.DOCUMENT_TYPE_ANALYTICAL)
            || documentType.getName().equalsIgnoreCase(WebConstants.DOCUMENT_TYPE_SLICE_CODES)) {
            index = 1;
        } else {
            index = 0;
        }

        for (int i = index; i < titleId.length; i++) {
            publicationInfo.append(titleId[i]);
            publicationInfo.append("_");
        }
        if (publicationInfo.length() > 0)
            publicationInfo.deleteCharAt(publicationInfo.length() - 1);

        return publicationInfo.toString();
    }

    public Long getBookdefinitionId() {
        return bookdefinitionId;
    }

    public void setBookdefinitionId(final Long bookdefinitionId) {
        this.bookdefinitionId = bookdefinitionId;
    }

    public String getTitleId() {
        return titleId;
    }

    public void setTitleId(final String titleId) {
        this.titleId = titleId;
    }

    public String getProviewDisplayName() {
        return proviewDisplayName;
    }

    public void setProviewDisplayName(final String proviewDisplayName) {
        this.proviewDisplayName = proviewDisplayName;
    }

    public List<FrontMatterPage> getFrontMatters() {
        return frontMatters;
    }

    public void setFrontMatters(final List<FrontMatterPage> frontMatters) {
        this.frontMatters = frontMatters;
    }

    public boolean isExcludeDocumentsUsed() {
        return isExcludeDocumentsUsed;
    }

    public void setExcludeDocumentsUsed(final boolean isExcludeDocumentsUsed) {
        this.isExcludeDocumentsUsed = isExcludeDocumentsUsed;
    }

    public Collection<ExcludeDocument> getExcludeDocuments() {
        return excludeDocuments;
    }

    public void setExcludeDocuments(final Collection<ExcludeDocument> excludeDocuments) {
        this.excludeDocuments = excludeDocuments;
    }

    public Collection<ExcludeDocument> getExcludeDocumentsCopy() {
        return excludeDocumentsCopy;
    }

    public void setExcludeDocumentsCopy(final Collection<ExcludeDocument> excludeDocumentsCopy) {
        this.excludeDocumentsCopy = excludeDocumentsCopy;
    }

    public String getSubGroupHeading() {
        return subGroupHeading;
    }

    public void setSubGroupHeading(final String subGroupHeading) {
        this.subGroupHeading = subGroupHeading;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    public boolean isRenameTocEntriesUsed() {
        return isRenameTocEntriesUsed;
    }

    public void setRenameTocEntriesUsed(final boolean isRenameTocEntriesUsed) {
        this.isRenameTocEntriesUsed = isRenameTocEntriesUsed;
    }

    public Collection<RenameTocEntry> getRenameTocEntries() {
        return renameTocEntries;
    }

    public void setRenameTocEntries(final Collection<RenameTocEntry> renameTocEntries) {
        this.renameTocEntries = renameTocEntries;
    }

    public Collection<RenameTocEntry> getRenameTocEntriesCopy() {
        return renameTocEntriesCopy;
    }

    public void setRenameTocEntriesCopy(final Collection<RenameTocEntry> renameTocEntriesCopy) {
        this.renameTocEntriesCopy = renameTocEntriesCopy;
    }

    public boolean isTableViewersUsed() {
        return isTableViewersUsed;
    }

    public void setTableViewersUsed(final boolean isTableViewersUsed) {
        this.isTableViewersUsed = isTableViewersUsed;
    }

    public Collection<TableViewer> getTableViewers() {
        return tableViewers;
    }

    public void setTableViewers(final Collection<TableViewer> tableViewers) {
        this.tableViewers = tableViewers;
    }

    public Collection<TableViewer> getTableViewersCopy() {
        return tableViewersCopy;
    }

    public void setTableViewersCopy(final Collection<TableViewer> tableViewersCopy) {
        this.tableViewersCopy = tableViewersCopy;
    }

    public Collection<DocumentCopyright> getDocumentCopyrights() {
        return documentCopyrights;
    }

    public void setDocumentCopyrights(final Collection<DocumentCopyright> documentCopyrights) {
        this.documentCopyrights = documentCopyrights;
    }

    public Collection<DocumentCopyright> getDocumentCopyrightsCopy() {
        return documentCopyrightsCopy;
    }

    public void setDocumentCopyrightsCopy(final Collection<DocumentCopyright> documentCopyrightsCopy) {
        this.documentCopyrightsCopy = documentCopyrightsCopy;
    }

    public Collection<DocumentCurrency> getDocumentCurrencies() {
        return documentCurrencies;
    }

    public void setDocumentCurrencies(final Collection<DocumentCurrency> documentCurrencies) {
        this.documentCurrencies = documentCurrencies;
    }

    public Collection<DocumentCurrency> getDocumentCurrenciesCopy() {
        return documentCurrenciesCopy;
    }

    public void setDocumentCurrenciesCopy(final Collection<DocumentCurrency> documentCurrenciesCopy) {
        this.documentCurrenciesCopy = documentCurrenciesCopy;
    }

    public boolean isFinalStage() {
        return isFinalStage;
    }

    public void setFinalStage(final boolean isFinalStage) {
        this.isFinalStage = isFinalStage;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(final String copyright) {
        this.copyright = copyright;
    }

    public String getCopyrightPageText() {
        return copyrightPageText;
    }

    public void setCopyrightPageText(final String copyrightPageText) {
        this.copyrightPageText = copyrightPageText;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(final String materialId) {
        this.materialId = materialId;
    }

    public List<Author> getAuthorInfo() {
        return authorInfo;
    }

    public void setAuthorInfo(final List<Author> authorInfo) {
        this.authorInfo = authorInfo;
    }

    public List<PilotBook> getPilotBookInfo() {
        return pilotBookInfo;
    }

    public void setPilotBookInfo(final List<PilotBook> pilotBookInfo) {
        this.pilotBookInfo = pilotBookInfo;
    }

    public boolean getIsAuthorDisplayVertical() {
        return isAuthorDisplayVertical;
    }

    public void setIsAuthorDisplayVertical(final boolean isAuthorDisplayVertical) {
        this.isAuthorDisplayVertical = isAuthorDisplayVertical;
    }

    public String getFrontMatterTocLabel() {
        return frontMatterTocLabel;
    }

    public void setFrontMatterTocLabel(final String frontMatterTocLabel) {
        this.frontMatterTocLabel = frontMatterTocLabel;
    }

    public String getRootTocGuid() {
        return rootTocGuid;
    }

    public void setRootTocGuid(final String rootTocGuid) {
        this.rootTocGuid = rootTocGuid;
    }

    public String getTocCollectionName() {
        return tocCollectionName;
    }

    public void setTocCollectionName(final String tocCollectionName) {
        this.tocCollectionName = tocCollectionName;
    }

    public String getDocCollectionName() {
        return docCollectionName;
    }

    public void setDocCollectionName(final String docCollectionName) {
        this.docCollectionName = docCollectionName;
    }

    public String getNortDomain() {
        return nortDomain;
    }

    public void setNortDomain(final String nortDomain) {
        this.nortDomain = nortDomain;
    }

    public String getNortFilterView() {
        return nortFilterView;
    }

    public void setNortFilterView(final String nortFilterView) {
        this.nortFilterView = nortFilterView;
    }

    public String getPrintSetNumber() {
        return printSetNumber;
    }

    public String getPrintSubNumber() {
        return printSubNumber;
    }

    public void setPrintSetNumber(final String printSetNumber) {
        this.printSetNumber = printSetNumber;
    }

    public void setPrintSubNumber(final String printSubNumber) {
        this.printSubNumber = printSubNumber;
    }

    public Long getContentTypeId() {
        return contentTypeId;
    }

    public void setContentTypeId(final Long contentTypeId) {
        this.contentTypeId = contentTypeId;
    }

    public String getFmThemeText() {
        return fmThemeText;
    }

    public void setFmThemeText(final String fmThemeText) {
        this.fmThemeText = fmThemeText;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(final String isbn) {
        this.isbn = isbn;
    }

    public boolean isPublicationCutoffDateUsed() {
        return isPublicationCutoffDateUsed;
    }

    public void setPublicationCutoffDateUsed(final boolean isPublicationCutoffDateUsed) {
        this.isPublicationCutoffDateUsed = isPublicationCutoffDateUsed;
    }

    public String getPublicationCutoffDate() {
        return publicationCutoffDate;
    }

    public void setPublicationCutoffDate(final String publicationCutoffDate) {
        this.publicationCutoffDate = publicationCutoffDate;
    }

    public boolean isIncludeAnnotations() {
        return includeAnnotations;
    }

    public void setIncludeAnnotations(final boolean includeAnnotations) {
        this.includeAnnotations = includeAnnotations;
    }

    public boolean isIncludeNotesOfDecisions() {
        return includeNotesOfDecisions;
    }

    public void setIncludeNotesOfDecisions(final boolean includeNotesOfDecisions) {
        this.includeNotesOfDecisions = includeNotesOfDecisions;
    }

    public String getPublishDateText() {
        return publishDateText;
    }

    public void setPublishDateText(final String publishDateText) {
        this.publishDateText = publishDateText;
    }

    public Map<Long, Collection<Long>> getKeywords() {
        return keywords;
    }

    public void setKeywords(final Map<Long, Collection<Long>> keywords) {
        this.keywords = keywords;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public boolean getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(final boolean isComplete) {
        this.isComplete = isComplete;
    }

    public boolean isEnableCopyFeatureFlag() {
        return enableCopyFeatureFlag;
    }

    public void setEnableCopyFeatureFlag(final boolean enableCopyFeatureFlag) {
        this.enableCopyFeatureFlag = enableCopyFeatureFlag;
    }

    public PilotBookStatus getPilotBook() {
        return pilotBookStatus;
    }

    public void setPilotBook(final PilotBookStatus isPilotBook) {
        pilotBookStatus = isPilotBook;
    }

    public String getAdditionalTrademarkInfo() {
        return additionalTrademarkInfo;
    }

    public void setAdditionalTrademarkInfo(final String additionalTrademarkInfo) {
        this.additionalTrademarkInfo = additionalTrademarkInfo;
    }

    public boolean isKeyCiteToplineFlag() {
        return keyCiteToplineFlag;
    }

    public void setKeyCiteToplineFlag(final boolean keyCiteToplineFlag) {
        this.keyCiteToplineFlag = keyCiteToplineFlag;
    }

    public boolean isAutoUpdateSupport() {
        return autoUpdateSupport;
    }

    public void setAutoUpdateSupport(final boolean autoUpdateSupport) {
        this.autoUpdateSupport = autoUpdateSupport;
    }

    public boolean isSearchIndex() {
        return searchIndex;
    }

    public void setSearchIndex(final boolean searchIndex) {
        this.searchIndex = searchIndex;
    }

    public boolean getUseReloadContent() {
        return useReloadContent;
    }

    public void setUseReloadContent(final boolean useReloadContent) {
        this.useReloadContent = useReloadContent;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public String getPubType() {
        return pubType;
    }

    public void setPubType(final String pubType) {
        this.pubType = pubType;
    }

    public String getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(final String jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public String getPubInfo() {
        return pubInfo;
    }

    public void setPubInfo(final String pubInfo) {
        this.pubInfo = pubInfo;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(final String productCode) {
        this.productCode = productCode;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(final String publisher) {
        this.publisher = publisher;
    }

    public String getPubAbbr() {
        return pubAbbr;
    }

    public void setPubAbbr(final String pubAbbr) {
        this.pubAbbr = pubAbbr;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public EbookName getFrontMatterTitle() {
        return frontMatterTitle;
    }

    public void setFrontMatterTitle(final EbookName frontMatterTitle) {
        this.frontMatterTitle = frontMatterTitle;
    }

    public EbookName getFrontMatterSubtitle() {
        return frontMatterSubtitle;
    }

    public void setFrontMatterSubtitle(final EbookName frontMatterSubtitle) {
        this.frontMatterSubtitle = frontMatterSubtitle;
    }

    public EbookName getFrontMatterSeries() {
        return frontMatterSeries;
    }

    public void setFrontMatterSeries(final EbookName frontMatterSeries) {
        this.frontMatterSeries = frontMatterSeries;
    }

    public boolean isValidateForm() {
        return validateForm;
    }

    public void setValidateForm(final boolean validateForm) {
        this.validateForm = validateForm;
    }

    public Long getSelectedFrontMatterPreviewPage() {
        return selectedFrontMatterPreviewPage;
    }

    public void setSelectedFrontMatterPreviewPage(final Long fmPageSeqNum) {
        selectedFrontMatterPreviewPage = fmPageSeqNum;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(final SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public String getCodesWorkbenchBookName() {
        return codesWorkbenchBookName;
    }

    public void setCodesWorkbenchBookName(final String codesWorkbenchBookName) {
        this.codesWorkbenchBookName = codesWorkbenchBookName;
    }

    public List<NortFileLocation> getNortFileLocations() {
        return nortFileLocations;
    }

    public void setNortFileLocations(final List<NortFileLocation> nortFileLocations) {
        this.nortFileLocations = nortFileLocations;
    }

    public boolean isInsTagStyleEnabled() {
        return isInsTagStyleEnabled;
    }

    public void setInsTagStyleEnabled(final boolean isInsTagStyleEnabled) {
        this.isInsTagStyleEnabled = isInsTagStyleEnabled;
    }

    public boolean isGroupsEnabled() {
        return isGroupsEnabled;
    }

    public void setGroupsEnabled(final boolean isGroupsEnabled) {
        this.isGroupsEnabled = isGroupsEnabled;
    }

    public boolean isDelTagStyleEnabled() {
        return isDelTagStyleEnabled;
    }

    public void setDelTagStyleEnabled(final boolean isDelTagStyleEnabled) {
        this.isDelTagStyleEnabled = isDelTagStyleEnabled;
    }

    public boolean isRemoveEditorNoteHeading() {
        return isRemoveEditorNoteHeading;
    }

    public void setRemoveEditorNoteHeading(final boolean isRemoveEditorNoteHeading) {
        this.isRemoveEditorNoteHeading = isRemoveEditorNoteHeading;
    }

    public boolean isSplitBook() {
        return isSplitBook;
    }

    public void setSplitBook(final boolean isSplitBook) {
        this.isSplitBook = isSplitBook;
    }

    public Collection<SplitDocument> getSplitDocuments() {
        return splitDocuments;
    }

    public void setSplitDocuments(final Collection<SplitDocument> splitDocuments) {
        this.splitDocuments = splitDocuments;
    }

    public void setColorPrintComponentTable(final boolean value) {
        colorPrintComponentTable = value;
    }

    public boolean getColorPrintComponentTable() {
        return colorPrintComponentTable;
    }

    public String getPrintComponents() throws JsonProcessingException {
        return escapeXml10(jsonMapper.writeValueAsString(printComponents));
    }

    public Collection<PrintComponent> getPrintComponentsCollection() {
        return printComponents;
    }

    public void setPrintComponents(final String printComponents) throws IOException {
        this.printComponents = jsonMapper.readValue(
            printComponents,
            jsonMapper.getTypeFactory().constructCollectionType(List.class, PrintComponent.class));
    }

    public boolean isSplitTypeAuto() {
        return isSplitTypeAuto;
    }

    public void setSplitTypeAuto(final boolean isSplitTypeAuto) {
        this.isSplitTypeAuto = isSplitTypeAuto;
    }

    public Integer getSplitEBookParts() {
        return splitEBookParts;
    }

    public void setSplitEBookParts(final Integer splitEBookParts) {
        this.splitEBookParts = splitEBookParts;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String createCoverImageName() {
        final String[] titleIdArray = titleId.split("/");
        final StringBuilder buffer = new StringBuilder(StringUtils.join(titleIdArray, "_"));
        buffer.append("_cover");
        buffer.append(".png");
        return buffer.toString();
    }

    public String createPilotBookCsvName() {
        final String[] titleIdArray = titleId.split("/");
        final StringBuilder buffer = new StringBuilder(StringUtils.join(titleIdArray, "_"));
        buffer.append(".csv");
        return buffer.toString();
    }

    public void removeEmptyRows() {
        // Clear out empty author
        for (final Iterator<Author> i = authorInfo.iterator(); i.hasNext();) {
            final Author author = i.next();
            if (author == null || author.isNameEmpty()) {
                i.remove();
            }
        }

        // Clear out empty pilot books
        for (final Iterator<PilotBook> i = pilotBookInfo.iterator(); i.hasNext();) {
            final PilotBook book = i.next();
            if (book == null || book.isEmpty()) {
                i.remove();
            }
        }

        // Clear out empty NortFileLocation
        for (final Iterator<NortFileLocation> i = nortFileLocations.iterator(); i.hasNext();) {
            final NortFileLocation fileLocation = i.next();
            if (fileLocation == null || fileLocation.isEmpty()) {
                i.remove();
            }
        }

        // Clear out empty ExcludeDocument
        for (final Iterator<ExcludeDocument> i = excludeDocuments.iterator(); i.hasNext();) {
            final ExcludeDocument document = i.next();
            if (document == null || document.isEmpty()) {
                i.remove();
            }
        }

        // Clear out empty RenameTocEntry
        for (final Iterator<RenameTocEntry> i = renameTocEntries.iterator(); i.hasNext();) {
            final RenameTocEntry label = i.next();
            if (label == null || label.isEmpty()) {
                i.remove();
            }
        }

        // Clear out empty Tableviewer
        for (final Iterator<TableViewer> i = tableViewers.iterator(); i.hasNext();) {
            final TableViewer document = i.next();
            if (document == null || document.isEmpty()) {
                i.remove();
            }
        }

        // Clear out empty DocumentCurrency
        for (final Iterator<DocumentCurrency> i = documentCurrencies.iterator(); i.hasNext();) {
            final DocumentCurrency docCurrency = i.next();
            if (docCurrency == null || docCurrency.isEmpty()) {
                i.remove();
            }
        }

        // Clear out empty DocumentCopyright
        for (final Iterator<DocumentCopyright> i = documentCopyrights.iterator(); i.hasNext();) {
            final DocumentCopyright docCopyright = i.next();
            if (docCopyright == null || docCopyright.isEmpty()) {
                i.remove();
            }
        }

        // Clear out front matter line
        for (final Iterator<FrontMatterPage> i = frontMatters.iterator(); i.hasNext();) {
            final FrontMatterPage page = i.next();

            if (page == null) {
                // Remove page from Collection
                i.remove();
            } else {
                for (final Iterator<FrontMatterSection> j = page.getFrontMatterSections().iterator(); j.hasNext();) {
                    final FrontMatterSection section = j.next();
                    if (section == null) {
                        // Remove Section from Collection
                        j.remove();
                    } else {
                        for (final Iterator<FrontMatterPdf> k = section.getPdfs().iterator(); k.hasNext();) {
                            final FrontMatterPdf pdf = k.next();
                            if (pdf == null || pdf.isEmpty()) {
                                // Remove Pdf from Collection
                                k.remove();
                            }
                        }
                    }
                }
            }
        }
    }
}
