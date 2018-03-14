<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="GlobalParams.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	
	<!-- CA Global Params-->
	<!-- Document Param -->
  <xsl:param name="Doc-Type" select="/Document/document-data/doc-type"/>
  
	<!-- Delivery Parameters -->
  <xsl:param name="IncludeAbridgmentClassification" select="true()" />
  <xsl:param name="IncludeCaseAnnotation" select="true()" />
  <xsl:param name="DisplayKeyCiteTreatment" select="true()" />
  <xsl:param name="IncludeNonWestHeadnotes" select="true()" />

	<!-- Carrying term highlighting across links (e.g. Legal Memos) -->
	<xsl:variable name="SearchQuery" select="/Document/document-data/searchQuery"/>
	<xsl:variable name="SearchWithinQuery" select="/Document/document-data/searchWithinQuery"/>

	<!-- Globalization param to get language from document to translate appropriately. -->
	<xsl:variable name="Language">
		<xsl:choose>
			<xsl:when test="/Document/n-metadata/metadata.block/md.descriptions/md.lang = 'EN'">
				<xsl:value-of select="'en-CA'"/>
			</xsl:when>
			<xsl:when test="/Document/n-metadata/metadata.block/md.descriptions/md.lang = 'FR'">
				<xsl:value-of select="'fr-CA'"/>
			</xsl:when>
		</xsl:choose>
	</xsl:variable>

	<!-- End CA Global Params-->
</xsl:stylesheet>
