package com.thomsonreuters.uscl.ereader.common.proview.feature;

import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.Feature;

/**
 * Behavior of proview features list builde
 */
public interface FeaturesListBuilder {
    /**
     * Set up title docs from previous publication
     */
    FeaturesListBuilder withTitleDocs(Map<BookTitleId, List<Doc>> titleDocs);

    /**
     * Set up new version of book
     */
    FeaturesListBuilder withBookVersion(Version version);

    /**
     * Set up title for current features list
     */
    FeaturesListBuilder forTitleId(BookTitleId titleId);

    /**
     * If page numbers feature is needed
     */
    FeaturesListBuilder withPageNumbers(boolean withPageNumbers);

    /**
     * If thesaurus is needed
     */
    FeaturesListBuilder withThesaurus(boolean withThesaurus);

    /**
     * If previous document ids differ from new document ids and user annotations transfer is needed
     */
    FeaturesListBuilder withMinorVersionMapping(boolean withMinorVersionMapping);

    /**
     * Get list of proview features
     */
    List<Feature> getFeatures();
}
