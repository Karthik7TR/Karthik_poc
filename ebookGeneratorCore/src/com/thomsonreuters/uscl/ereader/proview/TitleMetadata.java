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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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
	
	private String titleId;
	private String titleVersion;
	private String lastUpdated;
	private String displayName;
	private String materialId;
	private String copyright;
	
	private Artwork artwork;

	private TableOfContents tableOfContents;
	private ArrayList<Author> authors;
	private ArrayList<Doc> documents;
	private ArrayList<Asset> assets;
	private ArrayList<Feature> proviewFeatures;
	private ArrayList<Keyword> keywords = null;
	
	public TitleMetadata() {
		addDefaults();
	}
	
	public TitleMetadata(String titleId, String titleVersion){
		this.titleId = titleId;
		this.titleVersion = titleVersion;
		this.lastUpdated = DATE_FORMAT.format(new Date());
		addDefaults();
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
	
	public void setAuthors(ArrayList<Author> authors) {
		this.authors = authors;
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

    String getStatus() {
		return status;
	}

	String getTitleId() {
		return titleId;
	}

	String getTitleVersion() {
		return titleVersion;
	}

	private boolean compareFieldsForEquality(final TitleMetadata rhs)
    {
        final EqualsBuilder builder = new EqualsBuilder();
        builder.append(getTitleId(), rhs.getTitleId());
        builder.append(getTitleVersion(), rhs.getTitleVersion());
        builder.append(getStatus(), rhs.getStatus());
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
	
}
