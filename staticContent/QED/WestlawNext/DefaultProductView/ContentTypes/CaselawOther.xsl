<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="AnalyticalReferenceBlock.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Synopsis.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Headnote.xsl"/>
	<xsl:include href="TribalCourtHeadnote.xsl"/>
	<xsl:include href="WestlawDescription.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>
			<xsl:call-template name="DisplayPublisherLogo" />
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates/>
			<xsl:call-template name="EndOfDocument" />
			<xsl:call-template name="DisplayPublisherLogo" />
		</div>
	</xsl:template>

	<xsl:template match="docket.block">
		<xsl:call-template name="wrapContentBlockWithAdditionalCobaltClasses">
			<xsl:with-param name="additionalClass" select="'&centerClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="date.block" priority="1">
		<xsl:call-template name="dateBlock">
			<xsl:with-param name="extraClasses" select="'&centerClass;'" />
		</xsl:call-template>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

</xsl:stylesheet>
