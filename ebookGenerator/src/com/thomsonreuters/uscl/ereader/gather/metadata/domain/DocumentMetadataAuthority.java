package com.thomsonreuters.uscl.ereader.gather.metadata.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.util.NormalizationRulesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jetbrains.annotations.Nullable;

import static com.thomsonreuters.uscl.ereader.util.NormalizationRulesUtil.normalizeNoDashesNoWhitespaces;
import static java.util.Optional.ofNullable;

/**
 * Instances of this class represent the collection of document metadata for a given publishing run.
 *
 * <p>Once created, DocumentMetadataAuthority instances are immutable.</p>
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a>u0081674
 */
public class DocumentMetadataAuthority {
    private static final String CITE_WITHOUT_DATE = "citeWithoutDate";
    private static final Pattern NORMALIZED_CITE_WITHOUT_DATE_PATTERN = Pattern.compile(String.format("(?<%s>.*)(\\(\\d+/\\d+/\\d+\\))", CITE_WITHOUT_DATE));

    //this represents the document metadata record for a run of an ebook.
    private Set<DocMetadata> docMetadataSet = new HashSet<>();

    //these are keyed maps used to search for the corresponding metadata without hitting the database.
    private Map<String, List<DocMetadata>> docMetadataKeyedByCite = new HashMap<>();
    private Map<String, DocMetadata> docMetadataKeyedByCiteWithoutDate = new HashMap<>();
    private Map<Long, DocMetadata> docMetadataKeyedBySerialNumber = new HashMap<>();
    private Map<String, DocMetadata> docMetadataKeyedByDocumentUuid = new HashMap<>();
    private Map<String, DocMetadata> docMetadataKeyedByPubIdAndPubPage = new HashMap<>();
    private Map<String, DocMetadata> docMetadataKeyedByProViewId = new HashMap<>();
    private Map<String, DocMetadata> docMetadataKeyedByThirdLineCite = new HashMap<>();

    public DocumentMetadataAuthority(final Set<DocMetadata> docMetadataSet) {
        initializeMaps(docMetadataSet);
    }

    /**
     * public view is for testing purposes
     */
    public void initializeMaps(final Set<DocMetadata> docMetadataSet) {
        this.docMetadataSet = ofNullable(docMetadataSet)
            .orElseThrow(() -> new IllegalArgumentException(
                "Cannot instantiate DocumentMetadataAuthority without a set of document metadata"));

        docMetadataSet.forEach(docMetadata -> {
            addDocMetadataToCiteMap(docMetadata);
            addDocMetadataToCiteMapWithoutDate(docMetadata);
            // Prevent overwriting of value with duplicate serial number
            docMetadataKeyedBySerialNumber.putIfAbsent(docMetadata.getSerialNumber(), docMetadata);

            docMetadataKeyedByDocumentUuid.put(docMetadata.getDocUuid(), docMetadata);
            docMetadataKeyedByProViewId.put(docMetadata.getProViewId(), docMetadata);

            if (StringUtils.isNotBlank(docMetadata.getFirstlineCitePubpage())) {
                final String firstlineCitePubId = docMetadata.getFirstlineCitePubpage();
                addDocMetadataToPubPageMap(docMetadata, firstlineCitePubId, docMetadata::getFirstlineCitePubId);
                addDocMetadataToPubPageMap(docMetadata, firstlineCitePubId, docMetadata::getSecondlineCitePubId);
                addDocMetadataToPubPageMap(docMetadata, firstlineCitePubId, docMetadata::getThirdlineCitePubId);
            }
            addThirdLineCiteMapping(docMetadata);
        });
    }

    private void addThirdLineCiteMapping(final DocMetadata docMetadata) {
        ofNullable(docMetadata.getThirdlineCite())
                .map(NormalizationRulesUtil::normalizeThirdLineCite)
                .ifPresent(thirdLineCite -> docMetadataKeyedByThirdLineCite.putIfAbsent(thirdLineCite, docMetadata));
    }

    private void addDocMetadataToCiteMap(final DocMetadata docMetadata) {
        Stream.of(docMetadata.getNormalizedFirstlineCite(), docMetadata.getFindOrig())
                .filter(Objects::nonNull)
                .map(NormalizationRulesUtil::normalizeNoDashesNoWhitespaces)
                .peek(cite -> docMetadataKeyedByCite.putIfAbsent(cite, new ArrayList<>()))
                .map(docMetadataKeyedByCite::get)
                .findFirst()
                .ifPresent(list -> list.add(docMetadata));
    }

    private void addDocMetadataToCiteMapWithoutDate(final DocMetadata docMetadata) {
        ofNullable(docMetadata.getNormalizedFirstlineCite())
                .map(this::getCiteWithoutDate)
                .ifPresent(cite -> docMetadataKeyedByCiteWithoutDate.put(cite, docMetadata));
    }

    @Nullable
    private String getCiteWithoutDate(final String item) {
        Matcher matcher = NORMALIZED_CITE_WITHOUT_DATE_PATTERN.matcher(item);
        if (matcher.matches()) {
            return matcher.group(CITE_WITHOUT_DATE);
        } else {
            return null;
        }
    }

    private void addDocMetadataToPubPageMap(final DocMetadata docMetadata,
                                            final String firstlineCitePubId,
                                            final Supplier<Long> pubIdSupplier) {
        ofNullable(pubIdSupplier.get())
            .map(cite -> cite + firstlineCitePubId)
            .ifPresent(key -> docMetadataKeyedByPubIdAndPubPage.put(key, docMetadata));
    }

    /**
     * Retrieves a <em>read-only</em> copy of the document metadata for a given publishing run.
     *
     * <p>Note: the underlying {@link DocMetadata} instances are mutable, so use caution if they need to be modified.</p>
     * @return the {@link DocMetadata} for all documents contained within the book.
     */
    public Set<DocMetadata> getAllDocumentMetadata() {
        return Collections.unmodifiableSet(docMetadataSet);
    }

    /**
     * Returns a <em>read-only</em> {@link Map} of the {@link DocMetadata} keyed by normalized citation.
     *
     * <p>Note: the underlying {@link DocMetadata} instances are mutable, so use caution if they need to be modified.</p>
     * @return the association between normalized citations and the corresponding {@link DocMetadata}
     */
    public Map<String, List<DocMetadata>> getDocMetadataKeyedByCite() {
        return Collections.unmodifiableMap(docMetadataKeyedByCite);
    }

    public Map<String, DocMetadata> getDocMetadataKeyedByCiteWithoutDate() {
        return Collections.unmodifiableMap(docMetadataKeyedByCiteWithoutDate);
    }

    public DocMetadata getDocMetadataByCite(final String cite, final String guid) {
        final String normalizedCite = normalizeNoDashesNoWhitespaces(cite);
        DocMetadata docMetadata;
        if (!docMetadataKeyedByCite.isEmpty() && docMetadataKeyedByCite.containsKey(normalizedCite)
            && docMetadataKeyedByDocumentUuid.containsKey(guid)) {
            final List<DocMetadata> list = docMetadataKeyedByCite.get(normalizedCite);
            final DocMetadata originatingMetadata = docMetadataKeyedByDocumentUuid.get(guid);
            docMetadata = list.stream()
                .filter(item -> item.isDocumentEffective() != originatingMetadata.isDocumentEffective())
                .findAny()
                .orElse(list.get(0));
        } else if (docMetadataKeyedByCiteWithoutDate.containsKey(cite)) {
            docMetadata = docMetadataKeyedByCiteWithoutDate.get(cite);
        } else {
            docMetadata = docMetadataKeyedByThirdLineCite.get(normalizedCite);
        }
        return docMetadata;
    }

    /**
     * Returns a <em>read-only</em> {@link Map} of the {@link DocMetadata} keyed by serial number.
     *
     * <p>Note: the underlying {@link DocMetadata} instances are mutable, so use caution if they need to be modified.</p>
     * @return the association between serial numbers and the corresponding {@link DocMetadata}
     */
    public Map<Long, DocMetadata> getDocMetadataKeyedBySerialNumber() {
        return Collections.unmodifiableMap(docMetadataKeyedBySerialNumber);
    }

    /**
     * Returns a <em>read-only</em> {@link Map} of the {@link DocMetadata} keyed by document uuid.
     *
     * <p>Note: the underlying {@link DocMetadata} instances are mutable, so use caution if they need to be modified.</p>
     * @return the association between document uuids and the corresponding {@link DocMetadata}
     */
    public Map<String, DocMetadata> getDocMetadataKeyedByDocumentUuid() {
        return Collections.unmodifiableMap(docMetadataKeyedByDocumentUuid);
    }

    public Map<String, DocMetadata> getDocMetadataKeyedByPubIdAndPubPage() {
        return Collections.unmodifiableMap(docMetadataKeyedByPubIdAndPubPage);
    }

    public Map<String, DocMetadata> getDocMetadataKeyedByProViewId() {
        return Collections.unmodifiableMap(docMetadataKeyedByProViewId);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
