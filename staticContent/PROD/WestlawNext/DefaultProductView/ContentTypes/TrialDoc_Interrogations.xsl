<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="AnalysisTable.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Headnote.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="StarPagesWithoutRules.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadataForContentType" />
			<xsl:apply-templates select="n-metadata/metadata.block/md.references" />
			<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" />
			<xsl:apply-templates select="n-docbody"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="date.block" priority="1">
		<xsl:call-template name="dateBlock">
			<xsl:with-param name="extraClasses" select="'&centerClass;'" />
		</xsl:call-template>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="docket.block" priority="1">
		<div class="&docketsClass; &centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="date.block[following-sibling::document.head.block]" name="CenteredDate" priority="1">
		<div class="&dateClass; &centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="date.block[not(following-sibling::document.head.block)]" priority="1">
		<xsl:call-template name="CenteredDate" />
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="document.head.block[preceding-sibling::date.block]" priority="1">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="expert.report.block"/>

</xsl:stylesheet>
