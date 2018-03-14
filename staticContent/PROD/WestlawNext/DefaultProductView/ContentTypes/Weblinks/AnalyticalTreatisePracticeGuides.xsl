<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Analysis.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="CorrelationTable.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Form.xsl"/>
	<xsl:include href="Jurisdictions.xsl"/>
	<xsl:include href="Letter.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="Document.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<xsl:apply-templates select="." mode="CheckEasyEdit">
			<xsl:with-param name="contentType" select="'&contentTypeAnalyticalTreatisePracticeGuidesClass;'"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="md.references"/>

	<!-- suppresses all but the first content.metadata.block and the one that contains the copyright -->
	<xsl:template match="content.metadata.block[preceding-sibling::content.metadata.block and not(cmd.royalty/cmd.copyright)]" />

	<!-- suppress the first line cite at the bottom of the document -->
	<xsl:template match="content.metadata.block/cmd.identifiers/cmd.cites/cmd.first.line.cite" />

	<xsl:template match="section.front[not(//doc.title)]" priority="1">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>
	
	<xsl:template match="doc.title" priority="1">
		<xsl:call-template name="titleBlock"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

</xsl:stylesheet>