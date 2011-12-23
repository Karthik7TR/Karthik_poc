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

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * This class represents the metadata within a title.
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

	private ArrayList<String> authors;
	private ArrayList<TocEntry> tocEntries;
	private ArrayList<Doc> documents;
	private ArrayList<Asset> assets;
	private ArrayList<Feature> proviewFeatures;
	private ArrayList<Keyword> keywords;
	
	public TitleMetadata() {
		
	}
	
	public TitleMetadata(String titleId, String titleVersion){
		this.titleId = titleId;
		this.titleVersion = titleVersion;
		this.lastUpdated = DATE_FORMAT.format(new Date());
		Feature autoUpdate = new Feature("AutoUpdate");
		Feature searchIndex = new Feature("SearchIndex");
		Feature onePassSSO = new Feature("OnePassSSO", "www.westlaw.com");
		this.proviewFeatures = new ArrayList<Feature>();
		this.proviewFeatures.add(autoUpdate);
		this.proviewFeatures.add(searchIndex);
		this.proviewFeatures.add(onePassSSO);
	}
	
	public void setAuthors(ArrayList<String> authors) {
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

	public void setTocEntries(ArrayList<TocEntry> tocEntries) {
		this.tocEntries = tocEntries;
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
	
}
