<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Index.xsl"/>
	<xsl:include href="Analysis.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Document.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<xsl:apply-templates select="." mode="CheckEasyEdit">
			<xsl:with-param name="contentType" select="'&contentTypeTreatisesClass;'"/>
			<xsl:with-param name="displayPublisherLogo" select="true()"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="md.cites" priority="2">
		<xsl:call-template name="getCitation" />
	</xsl:template>

	<xsl:template match="include.copyright" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<!-- override the begin.quote (from para.xsl) as it is rendering blockquote elements; this appears to be specific to this content type and should probably be fixed in content instead of XSL, but for now we'll apply this suppression-->
	<xsl:template match="paratext[/Document/document-data/collection = 'w_3rd_wgltreat']" priority="2">
		<xsl:call-template name="renderParagraphTextDiv"/>
	</xsl:template>

	<!-- Suppress the normal "*.cites" elements -->
	<xsl:template match="cmd.cites | md.cites" />

	<xsl:template match="doc.title[last()]" priority="1">
		<xsl:call-template name="titleBlock"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="prop.head[not(following::doc.title)][last()]" priority="1">
		<xsl:apply-templates />
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="title.block[not(//doc.title or //prop.head)]" priority="1">
		<xsl:call-template name="titleBlock"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="database.link">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- BUG 618922 - Indent nest paragraphs -->
	<xsl:template match="para[contains('|w_3rd_wcmaids2_doc|' , concat('|', /Document/document-data/collection, '|'))]" priority="1">
		<xsl:call-template name="nestedParas"/>
	</xsl:template>

</xsl:stylesheet>
