package com.thomsonreuters.uscl.ereader.gather.metadata.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Instances of this class represent the collection of document metadata for a given publishing run.
 *
 * <p>Once created, DocumentMetadataAuthority instances are immutable.</p>
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a>u0081674
 */
public class DocumentMetadataAuthority {
    private static final String DASH = "-";
    //this represents the document metadata record for a run of an ebook.
    private Set<DocMetadata> docMetadataSet = new HashSet<>();

    //these are keyed maps used to search for the corresponding metadata without hitting the database.
    private Map<String, List<DocMetadata>> docMetadataKeyedByCite = new HashMap<>();
    private Map<Long, DocMetadata> docMetadataKeyedBySerialNumber = new HashMap<>();
    private Map<String, DocMetadata> docMetadataKeyedByDocumentUuid = new HashMap<>();
    private Map<String, DocMetadata> docMetadataKeyedByPubIdAndPubPage = new HashMap<>();
    private Map<String, DocMetadata> docMetadataKeyedByProViewId = new HashMap<>();

    public DocumentMetadataAuthority(final Set<DocMetadata> docMetadataSet) {
        this.docMetadataSet = Optional.ofNullable(docMetadataSet)
            .orElseThrow(() -> new IllegalArgumentException(
                "Cannot instantiate DocumentMetadataAuthority without a set of document metadata"));

        docMetadataSet.forEach(docMetadata -> {
            addDocMetadataToCiteMap(docMetadata);

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
        });
    }

    private void addDocMetadataToCiteMap(final DocMetadata docMetadata) {
        final String cite = Optional.ofNullable(docMetadata.getNormalizedFirstlineCite())
            .orElse(docMetadata.getFindOrig());
        final List<DocMetadata> list = Optional.ofNullable(docMetadataKeyedByCite.get(cite)).orElseGet(ArrayList::new);
        list.add(docMetadata);
        docMetadataKeyedByCite.putIfAbsent(cite.replaceAll(DASH, StringUtils.EMPTY), list);
    }

    private void addDocMetadataToPubPageMap(final DocMetadata docMetadata,
                                            final String firstlineCitePubId,
                                            final Supplier<Long> pubIdSupplier) {
        Optional.ofNullable(pubIdSupplier.get())
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

    public DocMetadata getDocMetadataByCite(final String cite, final String guid) {
        DocMetadata docMetadata = null;
        if (!docMetadataKeyedByCite.isEmpty() && docMetadataKeyedByCite.containsKey(cite)
            && docMetadataKeyedByDocumentUuid.containsKey(guid)) {
            final List<DocMetadata> list = docMetadataKeyedByCite.get(cite);
            final DocMetadata originatingMetadata = docMetadataKeyedByDocumentUuid.get(guid);
            docMetadata = list.stream()
                .filter(item -> item.isDocumentEffective() != originatingMetadata.isDocumentEffective())
                .findAny()
                .orElse(list.get(0));
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
