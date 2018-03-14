<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="AnalysisTable.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
  <xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Headnote.xsl"/>
	<xsl:include href="Title.xsl"/>
  <xsl:include href="StarPagesWithoutRules.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">	
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeBriefClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadataForContentType" />
			<xsl:apply-templates select="n-metadata/metadata.block/md.references" />
			<div class="&documentHeadClass;">
				<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" />
				<xsl:apply-templates select="n-docbody/node()[self::brief or self::trialdoc]/content.block/court.block"/>
				<xsl:apply-templates select="n-docbody/node()[self::brief or self::trialdoc]/content.block/title.block"/>
				<xsl:apply-templates select="n-docbody/node()[self::brief or self::trialdoc]/content.block/docket.block"/>
				<xsl:apply-templates select="n-docbody/node()[self::brief or self::trialdoc]/content.block/date.block"/>
				<xsl:apply-templates select="n-docbody/node()[self::brief or self::trialdoc]/content.block/action.block"/>
			</div>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			<xsl:apply-templates select="n-docbody/node()[self::brief or self::trialdoc]/content.block/node()[not(self::court.block or self::title.block or self::docket.block or self::date.block or self::action.block)]"/>
			<xsl:call-template name="RenderFootnoteSection"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>
	
</xsl:stylesheet>
