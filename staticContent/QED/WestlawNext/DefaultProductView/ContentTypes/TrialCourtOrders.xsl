<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Headnote.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeTrialCourtOrdersClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates select="n-metadata/metadata.block/md.references" />
			<xsl:call-template name="documentHeader"/>
			<xsl:apply-templates select="n-docbody/trial.court.order | n-docbody/decision" mode="body"/>
			<xsl:call-template name="RenderFootnoteSection"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template name="documentHeader">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" />
			<xsl:apply-templates select="n-docbody/trial.court.order | n-docbody/decision" />
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="trial.court.order | decision">
		<xsl:apply-templates select="content.block"/>
	</xsl:template>

	<xsl:template match="trial.court.order | decision" mode="body">
		<xsl:apply-templates select="content.block" mode="body"/>
	</xsl:template>

	<xsl:template match="content.block">
		<xsl:apply-templates select="court.block"/>
		<xsl:apply-templates select="title.block"/>
		<xsl:apply-templates select="docket.block"/>
		<xsl:apply-templates select="date.block"/>
		<xsl:apply-templates select="action.block"/>
	</xsl:template>

	<xsl:template match="content.block" mode="body">
		<xsl:apply-templates select="node()[not(self::court.block or self::title.block or self::docket.block or self::date.block or self::action.block)]"/>
	</xsl:template>
</xsl:stylesheet>
