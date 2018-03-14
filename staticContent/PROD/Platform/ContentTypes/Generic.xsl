<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Address.xsl"/>
	<xsl:include href="Analysis.xsl"/>
	<xsl:include href="AnalyticalReferenceBlock.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="CorrelationTable.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Dialogue.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Form.xsl"/>
	<xsl:include href="Headnote.xsl"/>
	<xsl:include href="Jurisdictions.xsl"/>
	<xsl:include href="Letter.xsl"/>
	<xsl:include href="SubjectHeading.xsl"/>
	<xsl:include href="SummaryToc.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="TribalCourtHeadnote.xsl"/>
	<xsl:include href="WestlawDescription.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>
			<xsl:comment> &genericStylesheetMessageText; </xsl:comment>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>
	
	<xsl:template match="prop.block" priority="1">
		<xsl:call-template name="wrapContentBlockWithCobaltClass"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>


</xsl:stylesheet>
