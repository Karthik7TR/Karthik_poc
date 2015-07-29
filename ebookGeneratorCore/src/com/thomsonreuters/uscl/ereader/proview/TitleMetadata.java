/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.proview;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;

/**
 * This class represents the metadata within a title. Instances of this class are mutable.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class TitleMetadata implements Serializable {
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
	private List<FrontMatterPage> frontMatterPages;

	private Artwork artwork;

	private TableOfContents tableOfContents;
	private ArrayList<String> authorNames;
	private ArrayList<Doc> documents;
	private ArrayList<Asset> assets;
	private ArrayList<Feature> proviewFeatures;
	private ArrayList<Keyword> keywords = null;
	private boolean isPilotBook;
	
	public TitleMetadata() {
		addDefaults();
	}
	
	public TitleMetadata(String titleId, String titleVersion){
		this.titleId = titleId;
		this.titleVersion = titleVersion;
		this.lastUpdated = DATE_FORMAT.format(new Date());
		
		addDefaults();
	}

	public TitleMetadata(String fullyQualifiedTitleId, String versionNumber,
			ArrayList<Feature> proviewFeatures, ArrayList<Keyword> keyWords,
			ArrayList<Author> authors, boolean isPilotBook) {
		this.titleId = fullyQualifiedTitleId;
		this.titleVersion = versionNumber;
		this.lastUpdated = DATE_FORMAT.format(new Date());

		this.proviewFeatures = proviewFeatures;
		this.keywords = keyWords;
		this.authorNames = new ArrayList<String>();
		if (authors.size() > 0) {
			for (Author author : authors) {
				this.authorNames.add(author.getFullName());
			}
		} else {
			this.authorNames.add(".");
		}
		this.isPilotBook=isPilotBook;
	}

	private void addDefaults() {
		this.proviewFeatures = new ArrayList<Feature>();
		this.proviewFeatures.add(new Feature("AutoUpdate"));
		this.proviewFeatures.add(new Feature("SearchIndex"));
		this.proviewFeatures.add(new Feature("OnePassSSO", "www.westlaw.com"));
		
		this.keywords = new ArrayList<Keyword>();
		this.keywords.add(new Keyword("publisher", "Thomson Reuters"));
		this.keywords.add(new Keyword("jurisdiction", ".")); //TODO: Confirm with the business exactly how they want to use jurisdiction.
	}
	
	public void addFeature(String featureName)
	{
		this.proviewFeatures.add(new Feature(featureName));
	}
	
	public void addFeature(String featureName, String featureValue)
	{
		this.proviewFeatures.add(new Feature(featureName, featureValue));
	}
	
	public void setAuthors(ArrayList<String> authorNames) {
		this.authorNames = authorNames;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public void setDocuments(ArrayList<Doc> documents) {
		this.documents = documents;
	}

	public void setKeywords(ArrayList<Keyword> keywords) {
		this.keywords = keywords;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public void setTableOfContents(TableOfContents tableOfContents) {
		this.tableOfContents = tableOfContents;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public void setArtwork(Artwork artwork) {
		this.artwork = artwork;
	}
	
	public void setAssets(ArrayList<Asset> assets) {
		this.assets = assets;
	}

	public void setFrontMatterTocLabel(String frontMatterTocLabel) {
		this.frontMatterTocLabel = frontMatterTocLabel;
	}
	
	
	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
    @Override
    public boolean equals(final Object obj)
    {
        boolean retVal;
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

    public String getStatus() {
		return status;
	}

	public String getTitleId() {
		return titleId;
	}

	public String getTitleVersion() {
		return titleVersion;
	}
	
	public String getFrontMatterTocLabel() {
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
    public int hashCode()
    {
        final HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getTitleId());
        builder.append(getTitleVersion());
        builder.append(getStatus());
        builder.append(getOnlineexpiration());

        return builder.toHashCode();
    }

	public Artwork getCoverArt() {
		return artwork;
	}

	public ArrayList<Asset> getAssets() {
		return assets;
	}
	
	public ArrayList<Keyword> getKeywords() {
		return keywords;
	}

	public TableOfContents getTableOfContents() {
		return tableOfContents;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public String getLanguage() {
		return language;
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getMaterialId() {
		return materialId;
	}

	public String getCopyright() {
		return copyright;
	}

	public Artwork getArtwork() {
		return artwork;
	}

	public ArrayList<String> getAuthorNames() {
		return authorNames;
	}

	public ArrayList<Doc> getDocuments() {
		return documents;
	}

	public ArrayList<Feature> getProviewFeatures() {
		return proviewFeatures;
	}

	public void setFrontMatterPages(List<FrontMatterPage> frontMatterPages) {
		this.frontMatterPages = frontMatterPages;
	}

	public List<FrontMatterPage> getFrontMatterPages() {
		return frontMatterPages;
	}

	public void setProviewFeatures(ArrayList<Feature> proviewFeatures) {
		this.proviewFeatures = proviewFeatures;
	}

	public String getOnlineexpiration() {
		return onlineexpiration;
	}
	
	public void setIsPilotBook(boolean isPilotBook) {
		this.isPilotBook = isPilotBook;
	}
	
	public boolean getIsPilotBook() {
	    return isPilotBook;
	}
	
	
	
}
