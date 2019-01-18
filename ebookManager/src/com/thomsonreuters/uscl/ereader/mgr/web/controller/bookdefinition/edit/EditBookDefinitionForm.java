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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
import com.thomsonreuters.uscl.ereader.core.book.domain.common.CopyAware;
import com.thomsonreuters.uscl.ereader.core.book.domain.common.EbookDefinitionAware;
import com.thomsonreuters.uscl.ereader.core.book.domain.common.SequenceNumAware;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AutoPopulatingList;

@ToString(doNotUseGetters = true)
public class EditBookDefinitionForm {
    public static final String FORM_NAME = "editBookDefinitionForm";

    private static final String NULL_VAL = "null";
    private static final int PUBLISHER_INDEX = 0;
    private static final int PRODUCT_CODE_INDEX = 1;
    private static final int TITLE_NAME_INDEX = 2;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final UuidGenerator UUID_GENERATOR = new UuidGenerator();

    @Getter @Setter
    private Long bookdefinitionId;
    @Getter @Setter
    private String titleId;
    @Getter @Setter
    private String proviewDisplayName;
    @Getter @Setter
    private String copyright = "\u00A9";
    @Getter @Setter
    private String copyrightPageText;
    @Getter @Setter
    private String materialId;
    @Getter @Setter
    private List<PilotBook> pilotBookInfo = new AutoPopulatingList<>(PilotBook.class);
    @Getter @Setter
    private List<Author> authorInfo = new AutoPopulatingList<>(Author.class);
    @Getter @Setter
    private List<NortFileLocation> nortFileLocations = new AutoPopulatingList<>(NortFileLocation.class);
    @Getter @Setter
    private List<FrontMatterPage> frontMatters = new AutoPopulatingList<>(FrontMatterPage.class);
    @Getter @Setter
    private boolean isExcludeDocumentsUsed;
    @Getter @Setter
    private Collection<ExcludeDocument> excludeDocuments = new AutoPopulatingList<>(ExcludeDocument.class);
    @Getter @Setter
    private Collection<ExcludeDocument> excludeDocumentsCopy = new AutoPopulatingList<>(ExcludeDocument.class);
    @Getter @Setter
    private boolean isRenameTocEntriesUsed;
    @Getter @Setter
    private Collection<RenameTocEntry> renameTocEntries = new AutoPopulatingList<>(RenameTocEntry.class);
    @Getter @Setter
    private Collection<RenameTocEntry> renameTocEntriesCopy = new AutoPopulatingList<>(RenameTocEntry.class);
    @Getter @Setter
    private boolean isTableViewersUsed;
    @Getter @Setter
    private Collection<TableViewer> tableViewers = new AutoPopulatingList<>(TableViewer.class);
    @Getter @Setter
    private Collection<TableViewer> tableViewersCopy = new AutoPopulatingList<>(TableViewer.class);
    @Getter @Setter
    private Collection<DocumentCopyright> documentCopyrights = new AutoPopulatingList<>(DocumentCopyright.class);
    @Getter @Setter
    private Collection<DocumentCopyright> documentCopyrightsCopy = new AutoPopulatingList<>(DocumentCopyright.class);
    @Getter @Setter
    private Collection<DocumentCurrency> documentCurrencies = new AutoPopulatingList<>(DocumentCurrency.class);
    @Getter @Setter
    private Collection<DocumentCurrency> documentCurrenciesCopy = new AutoPopulatingList<>(DocumentCurrency.class);
    @Getter @Setter
    private String codesWorkbenchBookName;
    @Getter @Setter
    private SourceType sourceType = SourceType.NORT;

    @Getter @Setter
    private boolean isFinalStage = true;
    @Getter @Setter
    private Boolean isAuthorDisplayVertical = Boolean.FALSE;
    @Getter @Setter
    private String frontMatterTocLabel = "Publishing Information";

    @Getter @Setter
    private String rootTocGuid;
    @Getter @Setter
    private String docCollectionName;
    @Getter @Setter
    private String tocCollectionName;
    @Getter @Setter
    private String nortDomain;
    @Getter @Setter
    private String nortFilterView;
    @Getter @Setter
    private Long contentTypeId;
    @Getter @Setter
    private String fmThemeText;
    @Getter @Setter
    private String printSetNumber;
    @Getter @Setter
    private String printSubNumber;
    @Getter @Setter
    private String isbn;
    @Getter @Setter
    private boolean isPublicationCutoffDateUsed;
    @Getter @Setter
    private String publicationCutoffDate;
    @Getter @Setter
    private boolean includeAnnotations;
    @Getter @Setter
    private boolean includeNotesOfDecisions = true;
    @Getter @Setter
    private String notes = StringUtils.EMPTY;
    @Getter @Setter
    private Boolean useReloadContent = Boolean.FALSE;
    @Getter @Setter
    private boolean isInsTagStyleEnabled;
    @Getter @Setter
    private boolean isDelTagStyleEnabled;
    @Getter @Setter
    private boolean isRemoveEditorNoteHeading;
    @Getter @Setter
    private boolean isSplitBook;
    @Getter @Setter
    private boolean isSplitTypeAuto = true;
    @Getter @Setter
    private Integer splitEBookParts;
    @Getter @Setter
    private Collection<SplitDocument> splitDocuments = new AutoPopulatingList<>(SplitDocument.class);

    private Collection<PrintComponent> printComponents = new AutoPopulatingList<>(PrintComponent.class);

    // Proview Group information
    @Getter @Setter
    private boolean isGroupsEnabled = true;
    @Getter @Setter
    private String subGroupHeading;
    @Getter @Setter
    private String groupName;

    @Getter @Setter
    private String publishDateText = "see Title page for currentness";

    // Keywords used in Proview
    @Getter @Setter
    private Map<Long, Collection<Long>> keywords = new HashMap<>();

    @Getter @Setter
    private String currency;
    @Getter @Setter
    private String additionalTrademarkInfo;
    @Getter @Setter
    private Boolean isComplete = Boolean.FALSE;
    @Getter @Setter
    private boolean keyCiteToplineFlag = true;
    @Getter @Setter
    private boolean autoUpdateSupport = true;
    @Getter @Setter
    private boolean searchIndex = true;
    @Getter @Setter
    private boolean enableCopyFeatureFlag = true;
    @Getter @Setter
    private PilotBookStatus pilotBookStatus = PilotBookStatus.FALSE;

    // Fully qualified title ID parts
    @Getter @Setter
    private String publisher;
    @Getter @Setter
    private String state;
    @Getter @Setter
    private String pubType;
    @Getter @Setter
    private String pubAbbr;
    @Getter @Setter
    private String jurisdiction;
    @Getter @Setter
    private String pubInfo;
    @Getter @Setter
    private String productCode;
    @Getter @Setter
    private String comment;
    @Getter @Setter
    private EbookName frontMatterTitle = new EbookName();
    @Getter @Setter
    private EbookName frontMatterSubtitle = new EbookName();
    @Getter @Setter
    private EbookName frontMatterSeries = new EbookName();

    /**
     * Used to preview front matter, holds the FM page sequence number that
     * uniquely identifies what page the user want to preview.
     */
    @Getter @Setter
    private Long selectedFrontMatterPreviewPage;

    @Getter @Setter
    private boolean validateForm;

    @Getter @Setter
    private boolean colorPrintComponentTable;

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
            isExcludeDocumentsUsed = book.getExcludeDocuments().size() > 0;

            // Determine if RenameTocEntries are present in Book Definition
            isRenameTocEntriesUsed = book.getRenameTocEntries().size() > 0;

            isTableViewersUsed = tableViewers.size() > 0;

            // Determine if Publish Cut-off Date is used
            final Date date = book.getPublishCutoffDate();
            if (date != null) {
                final SimpleDateFormat sdf = new SimpleDateFormat(CoreConstants.DATE_FORMAT_PATTERN);
                publicationCutoffDate = sdf.format(date);
                isPublicationCutoffDateUsed = true;
            }

            book.getKeywordTypeValues()
                    .stream()
                    .collect(Collectors.groupingBy(keyword -> keyword.getKeywordTypeCode().getId(),
                            Collectors.mapping(KeywordTypeValue::getId, Collectors.toList())))
                    .forEach((code, values) -> keywords.put(code, values));

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

        book.setAuthors(copyList(authorInfo, book, Author::new));

        book.setPilotBooks(copyList(pilotBookInfo, book, PilotBook::new));

        // Add Front Matter Book Names
        final Set<EbookName> ebookNames = Stream.of(frontMatterTitle, frontMatterSubtitle, frontMatterSeries)
            .filter(eBookName -> !eBookName.isEmpty())
            .peek(eBookName -> eBookName.setEbookDefinition(book))
            .collect(Collectors.toCollection(HashSet::new));
        book.setEbookNames(ebookNames);

        final List<FrontMatterPage> pages = copyList(frontMatters, book, FrontMatterPage::new);
        pages.forEach(page -> {
            IntStream.rangeClosed(1, page.getFrontMatterSections().size()).forEach(index -> {
                final FrontMatterSection section = page.getFrontMatterSections().get(index - 1);
                section.setSequenceNum(index);
                section.setFrontMatterPage(page);
                IntStream.rangeClosed(1, section.getPdfs().size()).forEach(pdfIndex -> {
                    final FrontMatterPdf pdf = section.getPdfs().get(pdfIndex - 1);
                    pdf.setSection(section);
                    pdf.setSequenceNum(pdfIndex);
                });
            });
        });

        book.setFrontMatterPages(pages);

        // Compare with copy to determine if date needs update
        for (final ExcludeDocument document : excludeDocuments) {
            document.setBookDefinition(book);
            // Update date
            if (!excludeDocumentsCopy.contains(document)) {
                document.setLastUpdated(new Date());
            }
        }
        book.setExcludeDocuments(excludeDocuments);

        splitDocuments.forEach(document -> document.setBookDefinition(book));

        book.setSplitDocuments(splitDocuments);

        loadPrintComponents(book);

        // Compare with copy to determine if date needs update
        for (final RenameTocEntry label : renameTocEntries) {
            label.setBookDefinition(book);
            // Update date
            if (!renameTocEntriesCopy.contains(label)) {
                label.setLastUpdated(new Date());
            }
        }
        book.setRenameTocEntries(renameTocEntries);

        // Compare with copy to determine if date needs update
        for (final TableViewer document : tableViewers) {
            document.setBookDefinition(book);
            // Update date
            if (!tableViewersCopy.contains(document)) {
                document.setLastUpdated(new Date());
            }
        }
        book.setTableViewers(tableViewers);

        for (final DocumentCopyright documentCopyright : documentCopyrights) {
            documentCopyright.setBookDefinition(book);
            // Update date
            if (!documentCopyrightsCopy.contains(documentCopyright)) {
                documentCopyright.setLastUpdated(new Date());
            }
        }
        book.setDocumentCopyrights(documentCopyrights);

        for (final DocumentCurrency documentCurrency : documentCurrencies) {
            documentCurrency.setBookDefinition(book);
            // Update date
            if (!documentCurrenciesCopy.contains(documentCurrency)) {
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

        book.setKeywordTypeValues(getKeywordValues(keywords));

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

        book.setNortFileLocations(copyList(nortFileLocations, book, NortFileLocation::new));
        book.setFrontMatterTheme(fmThemeText);
    }

    private <T extends CopyAware<T> & SequenceNumAware & EbookDefinitionAware> List<T> copyList(
            final List<T> list, final BookDefinition book, final Supplier<T> elementSupplier) {
        return IntStream.rangeClosed(1, list.size())
            .mapToObj(index -> {
                final T copy = elementSupplier.get();
                copy.copy(list.get(index - 1));
                copy.setEbookDefinition(book);
                copy.setSequenceNum(index);
                return copy;
            })
            .collect(Collectors.toCollection(ArrayList::new));
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
                printComponent.setPrintComponentId(UUID_GENERATOR.generateUuid());
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
        Stream<String> titleIdParts = Stream.of(titleId);

        final String typeName = documentType.getName();
        if (WebConstants.DOCUMENT_TYPE_COURT_RULES.equalsIgnoreCase(typeName)) {
            titleIdParts = titleIdParts.skip(2);
        } else if (WebConstants.DOCUMENT_TYPE_ANALYTICAL.equalsIgnoreCase(typeName)
                || WebConstants.DOCUMENT_TYPE_SLICE_CODES.equalsIgnoreCase(typeName)) {
            titleIdParts = titleIdParts.skip(1);
        }

        return titleIdParts.collect(Collectors.joining("_"));
    }

    public String getPrintComponents() throws JsonProcessingException {
        return escapeXml10(OBJECT_MAPPER.writeValueAsString(printComponents));
    }

    public Collection<PrintComponent> getPrintComponentsCollection() {
        return printComponents;
    }

    public void setPrintComponents(final String printComponents) throws IOException {
        this.printComponents = OBJECT_MAPPER.readValue(
            printComponents,
            OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, PrintComponent.class));
    }





    public String createCoverImageName() {
        return String.format("%s_%s.%s", titleId.replaceAll("/", "_"), "cover", "png");
    }

    public String createPilotBookCsvName() {
        return String.format("%s.%s", titleId.replaceAll("/", "_"), "csv");
    }

    public void removeEmptyRows() {
        // Clear out empty author
        authorInfo.removeIf(author -> author == null || author.isNameEmpty());
        // Clear out empty pilot books
        pilotBookInfo.removeIf(book -> book == null || book.isEmpty());
        // Clear out empty NortFileLocation
        nortFileLocations.removeIf(fileLocation -> fileLocation == null || fileLocation.isEmpty());
        // Clear out empty ExcludeDocument
        excludeDocuments.removeIf(document -> document == null || document.isEmpty());
        // Clear out empty RenameTocEntry
        renameTocEntries.removeIf(label -> label == null || label.isEmpty());
        // Clear out empty Tableviewer
        tableViewers.removeIf(document -> document == null || document.isEmpty());
        // Clear out empty DocumentCurrency
        documentCurrencies.removeIf(docCurrency -> docCurrency == null || docCurrency.isEmpty());
        // Clear out empty DocumentCopyright
        documentCopyrights.removeIf(docCopyright -> docCopyright == null || docCopyright.isEmpty());
        // Clear out front matter line
        frontMatters.removeIf(Objects::isNull);
        frontMatters.forEach(page -> {
            page.getFrontMatterSections().removeIf(Objects::isNull);
            page.getFrontMatterSections().forEach(section -> {
                section.getPdfs().removeIf(pdf -> pdf == null || pdf.isEmpty());
            });
        });
    }
}
