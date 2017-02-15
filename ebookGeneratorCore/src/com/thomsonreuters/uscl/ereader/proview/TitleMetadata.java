package com.thomsonreuters.uscl.ereader.proview;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * This class represents the metadata within a title. Instances of this class are mutable.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class TitleMetadata implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private final String apiVersion = "v1";
    private final String language = "eng";
    private final String status = "Review";
    private String onlineexpiration = "29991231";

    private String titleId;
    private String titleVersion;
    private String lastUpdated;
    private String displayName;
    private String materialId;
    private String copyright;
    private String frontMatterTocLabel;
    private String isbn;
    private List<FrontMatterPage> frontMatterPages;

    private Artwork artwork;

    private TableOfContents tableOfContents;
    private List<String> authorNames;
    private List<Doc> documents;
    private List<Asset> assets;
    private List<Feature> proviewFeatures;
    private List<Keyword> keywords;
    private boolean isPilotBook;

    public TitleMetadata()
    {
        addDefaults();
    }

    public TitleMetadata(final String titleId, final String titleVersion)
    {
        this.titleId = titleId;
        this.titleVersion = titleVersion;
        lastUpdated = DATE_FORMAT.format(new Date());

        addDefaults();
    }

    public TitleMetadata(
        final String fullyQualifiedTitleId,
        final String versionNumber,
        final List<Feature> proviewFeatures,
        final List<Keyword> keyWords,
        final List<Author> authors,
        final boolean isPilotBook,
        final String isbn)
    {
        titleId = fullyQualifiedTitleId;
        titleVersion = versionNumber;
        lastUpdated = DATE_FORMAT.format(new Date());

        this.proviewFeatures = proviewFeatures;
        keywords = keyWords;
        authorNames = new ArrayList<>();
        if (authors.size() > 0)
        {
            for (final Author author : authors)
            {
                authorNames.add(author.getFullName());
            }
        }
        else
        {
            authorNames.add(".");
        }
        this.isPilotBook = isPilotBook;
        this.isbn = isbn;
    }

    private void addDefaults()
    {
        proviewFeatures = new ArrayList<>();
        proviewFeatures.add(new Feature("AutoUpdate"));
        proviewFeatures.add(new Feature("SearchIndex"));
        proviewFeatures.add(new Feature("OnePassSSO", "www.westlaw.com"));

        keywords = new ArrayList<>();
        keywords.add(new Keyword("publisher", "Thomson Reuters"));
        keywords.add(new Keyword("jurisdiction", ".")); //TODO: Confirm with the business exactly how they want to use jurisdiction.
    }

    public void addFeature(final String featureName)
    {
        proviewFeatures.add(new Feature(featureName));
    }

    public void addFeature(final String featureName, final String featureValue)
    {
        proviewFeatures.add(new Feature(featureName, featureValue));
    }

    public void setAuthors(final List<String> authorNames)
    {
        this.authorNames = authorNames;
    }

    public void setDisplayName(final String displayName)
    {
        this.displayName = displayName;
    }

    public void setDocuments(final List<Doc> documents)
    {
        this.documents = documents;
    }

    public void setKeywords(final List<Keyword> keywords)
    {
        this.keywords = keywords;
    }

    public void setMaterialId(final String materialId)
    {
        this.materialId = materialId;
    }

    public void setTableOfContents(final TableOfContents tableOfContents)
    {
        this.tableOfContents = tableOfContents;
    }

    public void setCopyright(final String copyright)
    {
        this.copyright = copyright;
    }

    public void setArtwork(final Artwork artwork)
    {
        this.artwork = artwork;
    }

    public void setAssets(final List<Asset> assetsForSplitBook)
    {
        assets = assetsForSplitBook;
    }

    public void setFrontMatterTocLabel(final String frontMatterTocLabel)
    {
        this.frontMatterTocLabel = frontMatterTocLabel;
    }

    public void setTitleId(final String titleId)
    {
        this.titleId = titleId;
    }

    public String getIsbn()
    {
        return isbn;
    }

    public void setIsbn(final String isbn)
    {
        this.isbn = isbn;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals(final Object obj)
    {
        final boolean retVal;
        if (this == obj)
        {
            retVal = true;
        }
        else if (obj instanceof TitleMetadata)
        {
            final TitleMetadata rhs = (TitleMetadata) obj;
            retVal = compareFieldsForEquality(rhs);
        }
        else
        {
            retVal = false;
        }
        return retVal;
    }

    public String getStatus()
    {
        return status;
    }

    public String getTitleId()
    {
        return titleId;
    }

    public String getTitleVersion()
    {
        return titleVersion;
    }

    public String getFrontMatterTocLabel()
    {
        return frontMatterTocLabel;
    }

    private boolean compareFieldsForEquality(final TitleMetadata rhs)
    {
        final EqualsBuilder builder = new EqualsBuilder();
        builder.append(getTitleId(), rhs.getTitleId());
        builder.append(getTitleVersion(), rhs.getTitleVersion());
        builder.append(getStatus(), rhs.getStatus());
        builder.append(getOnlineexpiration(), rhs.getOnlineexpiration());
        return builder.isEquals();
    }

    /**
     * Returns a hash code value for the object. This method is supported for the benefit of
     * hashtables such as those provided by <code>java.util.Hashtable</code>.
     *
     * @return a hash code value for this object.
     *
     * @see Object#equals(Object)
     * @see java.util.Hashtable
     */
    @Override
    public int hashCode()
    {
        final HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getTitleId());
        builder.append(getTitleVersion());
        builder.append(getStatus());
        builder.append(getOnlineexpiration());

        return builder.toHashCode();
    }

    public Artwork getCoverArt()
    {
        return artwork;
    }

    public List<Asset> getAssets()
    {
        return assets;
    }

    public List<Keyword> getKeywords()
    {
        return keywords;
    }

    public TableOfContents getTableOfContents()
    {
        return tableOfContents;
    }

    public String getApiVersion()
    {
        return apiVersion;
    }

    public String getLanguage()
    {
        return language;
    }

    public String getLastUpdated()
    {
        return lastUpdated;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getMaterialId()
    {
        return materialId;
    }

    public String getCopyright()
    {
        return copyright;
    }

    public Artwork getArtwork()
    {
        return artwork;
    }

    public List<String> getAuthorNames()
    {
        return authorNames;
    }

    public List<Doc> getDocuments()
    {
        return documents;
    }

    public List<Feature> getProviewFeatures()
    {
        return proviewFeatures;
    }

    public void setFrontMatterPages(final List<FrontMatterPage> frontMatterPages)
    {
        this.frontMatterPages = frontMatterPages;
    }

    public List<FrontMatterPage> getFrontMatterPages()
    {
        return frontMatterPages;
    }

    public void setProviewFeatures(final List<Feature> proviewFeatures)
    {
        this.proviewFeatures = proviewFeatures;
    }

    public String getOnlineexpiration()
    {
        return onlineexpiration;
    }

    public void setIsPilotBook(final boolean isPilotBook)
    {
        this.isPilotBook = isPilotBook;
    }

    public boolean getIsPilotBook()
    {
        return isPilotBook;
    }
}
