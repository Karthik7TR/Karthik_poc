package com.thomsonreuters.uscl.ereader.assemble.service;

import java.util.Arrays;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.Feature;
import com.thomsonreuters.uscl.ereader.proview.Keyword;
import com.thomsonreuters.uscl.ereader.proview.TableOfContents;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.proview.TocEntry;
import com.thomsonreuters.uscl.ereader.proview.TocNode;
import org.custommonkey.xmlunit.XMLTestCase;

/**
 * Abstract base class which provides convenience methods to support testing.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a>u0081674
 *
 */
public abstract class TitleMetadataTestBase extends XMLTestCase {
    protected static final String EXPECTED_ISBN_VALUE = "987654321";
    protected static final String EXPECTED_ISBN = "<isbn>" + EXPECTED_ISBN_VALUE + "</isbn>";

    protected TitleMetadata getTitleMetadata() {
        return getTestTitleMetadata(false);
    }

    protected TitleMetadata getTitleMetadataWithPilotBook() {
        return getTestTitleMetadata(true);
    }

    private TitleMetadata getTestTitleMetadata(final boolean isPilotBook) {
        final TitleMetadata titleMetadata = TitleMetadata.builder()
            .fullyQualifiedTitleId("yarr/pirates")
            .versionNumber("1")
            .copyright("The High Seas Trading Company.")
            .artworkFileName("swashbuckling.gif")
            .documents(getTestDocuments())
            .displayName("YARR - The Comprehensive Guide to &<> Plundering the Seven Seas.")
            .authors(getTestAuthors())
            .keywords(getTestKeywords())
            .assetFileName("BlackPearl.png")
            .assetFileName("PiratesCove.png")
            .assetFileName("Tortuga.png")
            .materialId("Plunder2")
            .isbn(EXPECTED_ISBN_VALUE)
            .proviewFeatures(getTestFeatures())
            .isPilotBook(isPilotBook)
            .build();
        titleMetadata.setTableOfContents(getTestTableOfContents());
        return titleMetadata;
    }

    private List<Doc> getTestDocuments() {
        return Arrays.asList(
            new Doc("1", "pirates.htm", 0, null), new Doc("2", "scallywags.htm", 0, null), new Doc("3", "landlubbers.htm", 0, null));
    }

    private List<Keyword> getTestKeywords() {
        return Arrays.asList(
            new Keyword("publisher", "High Seas Trading Company"), new Keyword("jurisdiction", "International Waters"));
    }

    private List<Feature> getTestFeatures() {
        return Arrays.asList(
            new Feature("AutoUpdate"), new Feature("SearchIndex"), new Feature("OnePassSSO", "www.westlaw.com"));
    }

    private List<Author> getTestAuthors() {
        final Author jackSparrow = new Author();
        jackSparrow.setAuthorNamePrefix("Captain");
        jackSparrow.setAuthorFirstName("Jack");
        jackSparrow.setAuthorLastName("Sparrow");
        final Author daveyJones = new Author();
        daveyJones.setAuthorFirstName("Davey");
        daveyJones.setAuthorLastName("Jones");
        return Arrays.asList(jackSparrow, daveyJones);
    }

    private TableOfContents getTestTableOfContents() {
        final TocNode aboutPirats = new TocEntry("1", "heading", "All About Pirates", 1);
        final TocNode scallywagging = new TocEntry("2", "heading", "Scallywagging for landlubbers", 1);
        scallywagging.addChild(new TocEntry("3", "heading", "Survival", 2));
        scallywagging.addChild(new TocEntry("3.1", "heading", "Begging", 2));
        scallywagging.addChild(new TocEntry("3.2", "heading", "The Plank", 2));
        scallywagging.addChild(new TocEntry("3.3", "heading", "Swabbing", 2));
        scallywagging.addChild(new TocEntry("3.4", "heading", "Brawling", 2));
        scallywagging.addChild(new TocEntry("3.5", "heading", "Patroling", 2));
        scallywagging.addChild(new TocEntry("3.6", "heading", "Plundering", 2));
        scallywagging.addChild(new TocEntry("3.7", "heading", "Wenching", 2));

        final TableOfContents tableOfContents = new TableOfContents();
        tableOfContents.setChildren(Arrays.asList(aboutPirats, scallywagging));
        return tableOfContents;
    }
}
