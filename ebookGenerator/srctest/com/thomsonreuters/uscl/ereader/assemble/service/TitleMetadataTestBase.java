package com.thomsonreuters.uscl.ereader.assemble.service;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.proview.Artwork;
import com.thomsonreuters.uscl.ereader.proview.Asset;
import com.thomsonreuters.uscl.ereader.proview.Doc;
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
        final TitleMetadata titleMetadata = new TitleMetadata("yarr/pirates", "v1");
        titleMetadata.setCopyright("The High Seas Trading Company.");
        titleMetadata.setArtwork(new Artwork("swashbuckling.gif"));
        final Doc pirates = new Doc("1", "pirates.htm", 0, null);
        final Doc scallywags = new Doc("2", "scallywags.htm", 0, null);
        final Doc landlubbers = new Doc("3", "landlubbers.htm", 0, null);
        final List<Doc> documents = new ArrayList<>();
        documents.add(pirates);
        documents.add(scallywags);
        documents.add(landlubbers);
        titleMetadata.setDocuments(documents);
        titleMetadata.setDisplayName("YARR - The Comprehensive Guide to &<> Plundering the Seven Seas.");
        final List<String> authors = new ArrayList<>();
        authors.add(new String("Captain Jack Sparrow"));
        authors.add(new String("Davey Jones"));
        titleMetadata.setAuthors(authors);
        final Keyword publisher = new Keyword("publisher", "High Seas Trading Company");
        final Keyword jurisdiction = new Keyword("jurisdiction", "International Waters");
        final List<Keyword> keywords = new ArrayList<>();
        keywords.add(publisher);
        keywords.add(jurisdiction);
        titleMetadata.setKeywords(keywords);
        final List<Asset> assets = new ArrayList<>();
        assets.add(new Asset("123", "BlackPearl.png"));
        assets.add(new Asset("456", "PiratesCove.png"));
        assets.add(new Asset("789", "Tortuga.png"));
        titleMetadata.setAssets(assets);
        final List<TocNode> tocEntries = new ArrayList<>();
        tocEntries.add(new TocEntry("1", "heading", "All About Pirates", 1));
        final TocNode scallywagging = new TocEntry("2", "heading", "Scallywagging for landlubbers", 1);
        final List<TocNode> scallywaggingChildren = new ArrayList<>();
        scallywaggingChildren.add(new TocEntry("3", "heading", "Survival", 2));
        scallywaggingChildren.add(new TocEntry("3.1", "heading", "Begging", 2));
        scallywaggingChildren.add(new TocEntry("3.2", "heading", "The Plank", 2));
        scallywaggingChildren.add(new TocEntry("3.3", "heading", "Swabbing", 2));
        scallywaggingChildren.add(new TocEntry("3.4", "heading", "Brawling", 2));
        scallywaggingChildren.add(new TocEntry("3.5", "heading", "Patroling", 2));
        scallywaggingChildren.add(new TocEntry("3.6", "heading", "Plundering", 2));
        scallywaggingChildren.add(new TocEntry("3.7", "heading", "Wenching", 2));
        for (final TocNode child : scallywaggingChildren) {
            scallywagging.addChild(child);
        }
        tocEntries.add(scallywagging);
        final TableOfContents tableOfContents = new TableOfContents();
        tableOfContents.setChildren(tocEntries);
        titleMetadata.setTableOfContents(tableOfContents);
        titleMetadata.setMaterialId("Plunder2");
        titleMetadata.setIsbn(EXPECTED_ISBN_VALUE);
        return titleMetadata;
    }

    protected TitleMetadata getTitleMetadataWithPilotBook() {
        final TitleMetadata titleMetadata = new TitleMetadata("yarr/pirates", "v1");
        titleMetadata.setCopyright("The High Seas Trading Company.");
        titleMetadata.setArtwork(new Artwork("swashbuckling.gif"));
        titleMetadata.setIsPilotBook(true);
        final Doc pirates = new Doc("1", "pirates.htm", 0, null);
        final Doc scallywags = new Doc("2", "scallywags.htm", 0, null);
        final Doc landlubbers = new Doc("3", "landlubbers.htm", 0, null);
        final List<Doc> documents = new ArrayList<>();
        documents.add(pirates);
        documents.add(scallywags);
        documents.add(landlubbers);
        titleMetadata.setDocuments(documents);
        titleMetadata.setDisplayName("YARR - The Comprehensive Guide to &<> Plundering the Seven Seas.");
        final List<String> authors = new ArrayList<>();
        authors.add(new String("Captain Jack Sparrow"));
        authors.add(new String("Davey Jones"));
        titleMetadata.setAuthors(authors);
        final Keyword publisher = new Keyword("publisher", "High Seas Trading Company");
        final Keyword jurisdiction = new Keyword("jurisdiction", "International Waters");
        final List<Keyword> keywords = new ArrayList<>();
        keywords.add(publisher);
        keywords.add(jurisdiction);
        titleMetadata.setKeywords(keywords);
        final List<Asset> assets = new ArrayList<>();
        assets.add(new Asset("123", "BlackPearl.png"));
        assets.add(new Asset("456", "PiratesCove.png"));
        assets.add(new Asset("789", "Tortuga.png"));
        titleMetadata.setAssets(assets);
        final List<TocNode> tocEntries = new ArrayList<>();
        tocEntries.add(new TocEntry("1", "heading", "All About Pirates", 1));
        final TocNode scallywagging = new TocEntry("2", "heading", "Scallywagging for landlubbers", 1);
        final List<TocNode> scallywaggingChildren = new ArrayList<>();
        scallywaggingChildren.add(new TocEntry("3", "heading", "Survival", 2));
        scallywaggingChildren.add(new TocEntry("3.1", "heading", "Begging", 2));
        scallywaggingChildren.add(new TocEntry("3.2", "heading", "The Plank", 2));
        scallywaggingChildren.add(new TocEntry("3.3", "heading", "Swabbing", 2));
        scallywaggingChildren.add(new TocEntry("3.4", "heading", "Brawling", 2));
        scallywaggingChildren.add(new TocEntry("3.5", "heading", "Patroling", 2));
        scallywaggingChildren.add(new TocEntry("3.6", "heading", "Plundering", 2));
        scallywaggingChildren.add(new TocEntry("3.7", "heading", "Wenching", 2));
        for (final TocNode child : scallywaggingChildren) {
            scallywagging.addChild(child);
        }
        tocEntries.add(scallywagging);
        final TableOfContents tableOfContents = new TableOfContents();
        tableOfContents.setChildren(tocEntries);
        titleMetadata.setTableOfContents(tableOfContents);
        titleMetadata.setMaterialId("Plunder2");
        titleMetadata.setIsbn(EXPECTED_ISBN_VALUE);
        return titleMetadata;
    }
}
