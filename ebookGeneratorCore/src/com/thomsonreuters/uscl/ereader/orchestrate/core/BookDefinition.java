/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * A book definition database table entity.
 * Represents the various set of job parameters that are provided to the e-book generating job.
 */
@Entity
@Table(name="EBOOK_DEFINITION", schema="EBOOK")
public class BookDefinition implements Serializable {
	private static final long serialVersionUID = -1933004529661737219L;
	
	/** Compound primary key for a book definition. Comprised of the book title ID and major version number. */

	private BookDefinitionKey primaryKey;
	
	private String bookName;
	private Long minorVersion;
	private Long majorVersion;
	// TODO: KeyWords?, Type?, Value?
	private String copyright;
	private Long materialNo;
	private String authorInfo;	// A pipe deliminated list of person first last names, like "Joe Blow | Bill Smith"
	private String rootTocGuid;	// like "I1754a5c012bb11dc8c0988fbe4566386"
	private String docCollectionName;
	private String tocCollectionName;
	private String nortDomain;
	private String nortFilterView;
	private String contentType;
	private String contentSubtype;
	private String coverImage;
	private String isbn;
	private String materialIdEmbeddedInDocText;  // true | false
	
	public BookDefinition() {
		super();
	}
	public BookDefinition(BookDefinitionKey key) {
		setPrimaryKey(key);
	}

	/**
	 * Primary key for an e-book definition object.
	 */
	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "fullyQualifiedTitleId", column = @Column(name="TITLE_ID", length=64, nullable=false))
	})
	public BookDefinitionKey getPrimaryKey() {
		return primaryKey;
	}
	
	@Column(name="AUTHOR_INFO", length=1024)
	public String getAuthorInfo() {
		return authorInfo;
	}
	@Column(name="CONTENT_SUBTYPE", length=64)
	public String getContentSubtype() {
		return contentSubtype;
	}
	@Column(name="CONTENT_TYPE", length=64)
	public String getContentType() {
		return contentType;
	}
	@Column(name="COPYRIGHT", length=1024)
	public String getCopyright() {
		return copyright;
	}
	@Column(name="COVER_IMAGE", length=256)
	public String getCoverImage() {
		return coverImage;
	}
	@Column(name="DOC_COLLECTION_NAME", length=64)
	public String getDocCollectionName() {
		return docCollectionName;
	}
	@Column(name="ISBN", length=64)
	public String getIsbn() {
		return isbn;
	}
	@Column(name="MAJOR_VERSION")
	public Long getMajorVersion() {
		return majorVersion;
	}
	@Column(name="MATERIAL_ID_EMBEDDED", length=8)
	public String getMaterialIdEmbeddedInDocText() {
		return materialIdEmbeddedInDocText;
	}
	@Column(name="MATERIAL_NO")
	public Long getMaterialNo() {
		return materialNo;
	}
	@Column(name="MINOR_VERSION")
	public Long getMinorVersion() {
		return minorVersion;
	}
	@Column(name="BOOK_NAME", length=1024)
	public String getBookName() {
		return bookName;
	}
	@Column(name="NORT_DOMAIN", length=64)
	public String getNortDomain() {
		return nortDomain;
	}
	@Column(name="NORT_FILTER_VIEW", length=64)
	public String getNortFilterView() {
		return nortFilterView;
	}
	@Column(name="ROOT_TOC_GUID", length=64)
	public String getRootTocGuid() {
		return rootTocGuid;
	}
	@Column(name="TOC_COLLECTION_NAME", length=64)
	public String getTocCollectionName() {
		return tocCollectionName;
	}
	
	public void setAuthorInfo(String authorInfo) {
		this.authorInfo = authorInfo;
	}
	public void setBookDefinitionKey(BookDefinitionKey key) {
		this.primaryKey = key;
	}
	
	public void setContentSubtype(String contentSubtype) {
		this.contentSubtype = contentSubtype;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
	}
	public void setDocCollectionName(String docCollectionName) {
		this.docCollectionName = docCollectionName;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public void setMajorVersion(Long major) {
		this.majorVersion = major;
	}
	public void setMaterialIdEmbeddedInDocText(String trueFalse) {
		this.materialIdEmbeddedInDocText = trueFalse;
	}
	public void setMaterialNo(Long materialNo) {
		this.materialNo = materialNo;
	}
	public void setMinorVersion(Long minorVersion) {
		this.minorVersion = minorVersion;
	}
	public void setBookName(String name) {
		this.bookName = name;
	}
	public void setNortDomain(String nortDomain) {
		this.nortDomain = nortDomain;
	}
	public void setNortFilterView(String nortFilterView) {
		this.nortFilterView = nortFilterView;
	}
	public void setPrimaryKey(BookDefinitionKey key) {
		this.primaryKey = key;
	}
	public void setRootTocGuid(String rootTocGuid) {
		this.rootTocGuid = rootTocGuid;
	}
	public void setTocCollectionName(String tocCollectionName) {
		this.tocCollectionName = tocCollectionName;
	}
	
	/**
	 * Parse the pipe character separated list of author names into a list of names.
	 * @param pipeSeparatedListOfNames the input string in form "name1 | name2 | name3 | ... ", may be null, in which case an empty list will be returned
	 * @return the parsed list of names, possibly empty, never null
	 */
	public static List<String> parseAuthorNames(String pipeSeparatedListOfNames) {
		List<String> names = new ArrayList<String>();
		if (pipeSeparatedListOfNames != null) {
			StringTokenizer tokenizer = new StringTokenizer(pipeSeparatedListOfNames, "|");
			while (tokenizer.hasMoreTokens()) {
				String name = tokenizer.nextToken().trim();
				names.add(name);
			}
		}
		return names;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
